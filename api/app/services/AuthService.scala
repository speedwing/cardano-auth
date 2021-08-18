package services

import blockchain.auth.mech.dev
import blockchain.auth.mech.dev.{RandomStringGeneration, SigningService}
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.util.encoders.Hex
import org.joda.time.DateTime
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson, JwtTime}
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

  private val Bearer = "Bearer"

  private val JwtAlgo = JwtAlgorithm.HS256

  private val SECRET = configuration.get[String]("api.jwt.secret")

  private val PublicKey = "public_key"

  def getMessage = {
    val randomMessage = randomStringGenerator.createRandomString()
    list.add(randomMessage)
    randomMessage
  }

  def isAuthenticated(headers: Headers): Option[String] = {
    headers
      .get("Authorization") match {
      case Some(value) if value.startsWith(Bearer) =>
        val token = value.substring(s"$Bearer ".length)
        if (JwtJson.isValid(token, SECRET, List(JwtAlgo))) {
          JwtJson
            .decodeJson(token, SECRET, List(JwtAlgo))
            .toOption
            .map(claims => claims \ "public_key")
            .map(_.as[String])
        } else {
          None
        }
      case _ => None
    }
  }

  def verify(message: String, signedMessage: String, publicKeyHex: String): Either[String, String] = {
    val outcome = signer.verify(
      new dev.Message(message),
      new dev.Message(Hex.decode(signedMessage)),
      new Ed25519PublicKeyParameters(Hex.decode(publicKeyHex), 0))

    if (outcome) {
      val claimObj = Json.obj((PublicKey, publicKeyHex))
      val expiresInAMonth = DateTime.now().plusMonths(1).getMillis / 1000L
      val claim = JwtClaim(Json.stringify(claimObj)).issuedNow.expiresAt(expiresInAMonth)
      val token = JwtJson.encode(claim, SECRET, JwtAlgo)
      Right(token)
    } else {
      Left("Invalid signature")
    }

  }

}
