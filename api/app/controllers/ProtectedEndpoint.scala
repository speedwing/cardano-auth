package controllers

import actions.UserAuthenticatedBuilder
import play.api.Logging
import play.api.mvc.InjectedController

import javax.inject.{Inject, Singleton}

@Singleton
class ProtectedEndpoint @Inject()(userAuthenticatedBuilder: UserAuthenticatedBuilder) extends InjectedController with Logging {

  def get = userAuthenticatedBuilder { foo =>
    val user = foo.user
    logger.info(s"Hello ${user.publicKey}")
    Ok("Cardano is the best blockchain!")
  }

}
