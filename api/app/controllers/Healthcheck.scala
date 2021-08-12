package controllers

import io.gimbalabs.cardano.auth.v0.models
import io.gimbalabs.cardano.auth.v0.models.json._
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import javax.inject.Singleton

@Singleton
class Healthcheck extends InjectedController {

  def get() = Action {
    Ok(Json.toJson(models.Healthcheck("healthy")))
  }

}
