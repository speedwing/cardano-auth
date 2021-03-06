package actions

import model.User
import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import services.AuthService

import javax.inject.Inject
import scala.concurrent.ExecutionContext


class UserAuthenticatedBuilder(parser: BodyParser[AnyContent], authService: AuthService)(implicit ec: ExecutionContext)
  extends AuthenticatedBuilder[User]({ req: RequestHeader =>
    authService
      .isAuthenticated(req.headers)
  }, parser, _ => Unauthorized) {

  @Inject()
  def this(parser: BodyParsers.Default, authService: AuthService)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent], authService)
  }

}
