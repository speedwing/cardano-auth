package services

import blockchain.auth.mech.dev
import blockchain.auth.mech.dev.{RandomStringGeneration, SigningService, VrfSigningService}
import io.gimbalabs.cardano.auth.v0.models.{Auth, SignatureType}
import model.User
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.util.encoders.Hex
import org.joda.time.DateTime
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.{Configuration, Logging}

import java.time.Clock
import javax.inject.{Inject, Singleton}

@Singleton
class AuthService @Inject()(configuration: Configuration) extends Logging {

  implicit val clock: Clock = Clock.systemUTC

  private val list = new java.util.Vector[String]()

  private val randomStringGenerator = new RandomStringGeneration()

  private val signer = new SigningService()

  private val vrfSigner = new VrfSigningService()

  private val Bearer = "Bearer"

  private val JwtAlgo = JwtAlgorithm.HS256

  private val SECRET = configuration.get[String]("api.jwt.secret")

  private val PublicKey = "public_key"

  private val KeyType = "key_type"

  def getMessage = {
    val randomMessage = randomStringGenerator.createRandomString()
    list.add(randomMessage)
    randomMessage
  }

  def isAuthenticated(headers: Headers): Option[User] = {
    headers
      .get("Authorization") match {
      case Some(value) if value.startsWith(Bearer) =>
        val token = value.substring(s"$Bearer ".length)
        if (JwtJson.isValid(token, SECRET, List(JwtAlgo))) {
          JwtJson
            .decodeJson(token, SECRET, List(JwtAlgo))
            .toOption
            .map { claims =>
              val publicKey = (claims \ PublicKey).as[String]
              val keyType = (claims \ KeyType).as[String]
              User(publicKey, SignatureType(keyType))
            }
        } else {
          None
        }
      case _ => None
    }
  }

  def verify(auth: Auth): Either[String, String] = {

    def paymentKeyVerify = signer.verify(
      new dev.Message(auth.message),
      new dev.Message(Hex.decode(auth.signedMessage)),
      new Ed25519PublicKeyParameters(Hex.decode(auth.publicKey), 0))

    def vrfKeyVerify = vrfSigner.verify(
      new dev.Message(auth.message),
      new dev.Message(Hex.decode(auth.signedMessage)),
      Hex.decode(auth.publicKey)
    )

    val outcome = auth.signatureType match {
      case SignatureType.Payment => paymentKeyVerify
      case SignatureType.Vrf => vrfKeyVerify
      case SignatureType.UNDEFINED(_) => false
    }

    if (outcome) {
      val claimObj = Json.obj((PublicKey, auth.publicKey), (KeyType, auth.signatureType.toString))
      val expiresInAMonth = DateTime.now().plusMonths(1).getMillis / 1000L
      val claim = JwtClaim(Json.stringify(claimObj)).issuedNow.expiresAt(expiresInAMonth)
      val token = JwtJson.encode(claim, SECRET, JwtAlgo)
      Right(token)
    } else {
      Left("Invalid signature")
    }

  }

}
