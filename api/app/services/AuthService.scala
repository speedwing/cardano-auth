package services

import blockchain.auth.mech.dev
import blockchain.auth.mech.dev.{RandomStringGeneration, SigningService}
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.util.encoders.Hex

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.{Inject, Singleton}

@Singleton
class AuthService @Inject()() {

  private val map = new ConcurrentHashMap[String, String]()

  private val list = new java.util.Vector[String]()

  private val randomStringGenerator = new RandomStringGeneration()

  private val signer = new SigningService()

  def getMessage = {
    val randomMessage = randomStringGenerator.createRandomString()
    list.add(randomMessage)
    randomMessage
  }

  def isAuthenticated(bearer: String): Either[String, Unit] = {
    if (map.containsKey(bearer)) {
      Right(())
    } else {
      Left("Provided token is not valid... for some reason.")
    }

  }

  def verify(message: String, signedMessage: String, publicKeyHex: String): Either[String, String] = {
    val outcome = signer.verify(
      new dev.Message(message),
      new dev.Message(Hex.decode(signedMessage)),
      new Ed25519PublicKeyParameters(Hex.decode(publicKeyHex), 0))

    if (outcome) {
      val token = UUID.randomUUID().toString
      Right(token)
    } else {
      Left("Invalid signature")
    }

  }

}
