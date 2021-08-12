package controllers

import io.gimbalabs.cardano.auth.v0.models
import io.gimbalabs.cardano.auth.v0.models.json._
import io.gimbalabs.cardano.auth.v0.models.{AuthenticationToken, GenericError, Message}
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.AuthService

import javax.inject.{Inject, Singleton}

@Singleton
class Auth @Inject()(authService: AuthService) extends InjectedController {


  def getMessage = Action {
    Ok(Json.toJson(Message(authService.getMessage)))
  }

  def postValid = Action(parse.json[models.AuthenticationToken]) { implicit req =>
    authService
      .isAuthenticated(req.body.token) match {
      case Right(_) => Ok
      case Left(message) => Unauthorized(Json.toJson(GenericError(message)))
    }
  }

  def post = Action(parse.json[models.Auth]) { implicit req =>

    val auth = req.body
    authService
      .verify(auth.message, auth.signedMessage, auth.publicKeyHex) match {
      case Right(token) => Ok(Json.toJson(AuthenticationToken(token)))
      case Left(message) => Forbidden(Json.toJson(GenericError(message)))
    }

  }

}
