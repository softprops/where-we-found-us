package com.meetup

import dispatch._
import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

case class Client(key: String, secret: String)
case class AccessResponse(access_token: String, expires_in: Int, refresh_token: String)


object Meetup {
  val api = :/("api.meetup.com").secure
  val authorize = :/("secure.meetup.com").secure / "oauth2" / "authorize"
  val access = :/("secure.meetup.com").secure.POST / "oauth2" / "access"
}

object AccessToken {
  import unfiltered.request.HttpRequest
  def unapply[T](r: HttpRequest[T]) = r match {
    case Cookies(c) => c("mu") match {
      case Some(Cookie(_, value, _, _, _, _)) if(!value.isEmpty) => Some(value)
      case _ => None
    }
  }
  def apply[T](r: HttpRequest[T]) = unapply(r)
}

object ClearAccessToken extends ResponseCookies(Cookie("mu","").path("/"))

case class Oauth(c: Client) {
  import com.codahale.jerkson.Json._

  def http[T](handler: Handler[T]): T = {
    val h = new Http with HttpsLeniency
    h(handler)
  }

  def intent: unfiltered.Cycle.Intent[Any, Any] = {

    case GET(Path("/auth") & Host(host)) =>
      Redirect((Meetup.authorize <<? Map(
        "client_id" -> c.key,
        "redirect_uri" -> "http://%s/authed".format(host),
        "response_type" -> "code",
        "scope" -> "ageless"
      )).to_uri.toString)

   case GET(Path(Seg("auth" :: "disconnect" :: Nil))) =>
     println("logging outta here...")
     ClearAccessToken ~> Redirect("/")

    case GET(Path("/authed") & Params(p) & Host(host)) =>
      p("code") match {
        case Seq(code) =>
          val access =
            http(Meetup.access <<? Map(
              "client_id" -> c.key,
              "client_secret" -> c.secret,
              "grant_type" -> "authorization_code",
              "code" -> code,
              "redirect_uri" -> "http://%s/authed".format(host)
            ) >> { s => parse[AccessResponse](s) })
          println("loggin in")
          ResponseCookies(Cookie("mu", access.access_token).path("/")) ~>
            Redirect("/timeline")
        case _ => ResponseString("not today :'(")
      }
  }
}
