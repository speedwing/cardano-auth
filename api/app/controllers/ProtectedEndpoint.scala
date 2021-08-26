package controllers

import actions.UserAuthenticatedBuilder
import io.gimbalabs.cardano.auth.v0.models.json._
import io.gimbalabs.cardano.auth.v0.models.{ProtectedEndpoint, SignatureType}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import javax.inject.{Inject, Singleton}

@Singleton
class ProtectedEndpoint @Inject()(userAuthenticatedBuilder: UserAuthenticatedBuilder) extends InjectedController with Logging {

  def get = userAuthenticatedBuilder { foo =>
    val user = foo.user
    logger.info(s"Hello ${user.publicKey}")

    val message = user.signatureType match {
      case SignatureType.Payment => List(
        "Hello Wallet!",
        s"Public Key ${user.publicKey}",
        "Cardano is the best blockchain!"
      )
      case SignatureType.Vrf => List(
        "Hello EASY1!",
        s"Public Key ${user.publicKey}",
        "Cardano is the best blockchain!"
      )
      case SignatureType.UNDEFINED(toString) => Nil
    }

    Ok(Json.toJson(ProtectedEndpoint(message)))
  }

}
