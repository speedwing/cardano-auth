package services

import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson, JwtTime}
import play.api.libs.json.Json

import java.time.Clock

class JwtTesting extends WordSpec with MustMatchers with OptionValues {

//  https://github.com/auth0/java-jwt

  "Foo" in {

    val claim = Json.obj(("user", 1), ("nbf", 1431520421))

    val algo = JwtAlgorithm.HS256

    val foo = JwtJson.encode(claim)

    val key = "bla"

    val token = JwtJson.encode(claim, key, algo)

    println(foo)

    println(token)

    JwtJson.validate(foo)

    JwtJson.validate(token, key, List(algo))

    val decoded = JwtJson.decodeJson(token)
    println(decoded)

    JwtJson.isValid(token)


  }

  "Minimum" in {

    val claim = Json.obj(("user", 1), ("nbf", 1431520421))

    val algo = JwtAlgorithm.HS256

    val foo = JwtJson.encode(claim)

    val key = "bla"

    val token = JwtJson.encode(claim, key, algo)

    println(foo)

    println(token)

    JwtJson.validate(foo)

    JwtJson.validate(token, key, List(algo))

    val decoded = JwtJson.decodeJson(token)
    println(decoded)

    JwtJson.isValid(token)


  }

  "Claims" in {

    implicit val clock: Clock = Clock.systemUTC

//    JwtClaim(Json.obj(("public_key", "asd")))

//    val claim = JwtClaim(notBefore = Some(JwtTime.nowSeconds + 60))
    val claim = JwtClaim("""{"user":1}""", notBefore = Some(JwtTime.nowSeconds + 60)).issuedIn(60).expiresIn(120)
    val algo = JwtAlgorithm.HS256
    val key = "bla"

    val token = JwtJson.encode(claim, key, algo)
    JwtJson.validate(token, key, List(algo))
    val result = JwtJson.isValid(token, key, List(algo))

    assert(result, "bla")
  }

}
