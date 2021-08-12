package controllers

import io.gimbalabs.cardano.auth.v0.models
import io.gimbalabs.cardano.auth.v0.models.GenericError
import io.gimbalabs.cardano.auth.v0.models.json._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.AuthService

import javax.inject.{Inject, Singleton}

@Singleton
class ProtectedEndpoint @Inject()(authService: AuthService) extends InjectedController {

  private val LOG = Logger(classOf[ProtectedEndpoint])

  private val Bearer = "Bearer "

  def get = Action { req =>
    req
      .headers
      .get("Authorization") match {
      case Some(value) if value.startsWith(Bearer) =>
        LOG.info(s"Value($value)")
        val token = value.substring("Bearer: ".length)
        authService
          .isAuthenticated(token) match {
          case Right(_) => Ok(Json.toJson(models.ProtectedEndpoint("Cardano is the best blockchain!")))
          case Left(value) => Forbidden(Json.toJson(GenericError(value)))
        }
      case Some(_) => Forbidden(Json.toJson(GenericError(s"Invalid auth type")))
      case None => Unauthorized(Json.toJson(GenericError(s"No authorization found")))
    }
  }

}
