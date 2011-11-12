package com.meetup

import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

case class Timeline(c: Client) {
  import dispatch._
  import com.codahale.jerkson.Json._
  import org.joda.time.{DateMidnight, Period}

  def http[T](handler: Handler[T]): T = {
    val h = new Http with HttpsLeniency
    h(handler)
  }

  def eventsBetween(access: String, from: Long, until: Long, page: Int) =
    Meetup.api / "2" / "events" <<? Map(
      "member_id" -> "self",
      "fields" -> "self,group_photo",
      "time" -> "%s,%s".format(from, until),
      "status" -> "past,upcoming",
      "access_token" -> access,
      "desc" -> "true",
      "page" -> "800",
      "offset" -> page.toString
    )

  def intent: unfiltered.Cycle.Intent[Any, Any] = {
    case Path(Seg("timeline" :: "more" :: Nil)) & Params(p) & r =>
      AccessToken(r) match {
        case Some(access) =>
          (p("since"), p("page")) match {
            case (Seq(s), Seq(p)) => try {
              val (since, page) = (s.toLong, p.toInt)

              println("since %s page %s" format(since, page))
              val until = new DateMidnight().plus(Period.weeks(2)).getMillis
              val req = eventsBetween(access, since, until, page)
              val eventJson = Caches("events")(req.to_uri.toString) match {
                case Some(value) =>
                  value
                case _ =>
                  val json  = http(req >- { s => s })
                  Caches("events")(req.to_uri.toString, json)
                  json
              }
              val parsed = parse[EventResponse](eventJson)
              val evts = parsed.results
                          .filter(_.self.rsvp match {
                            case Some(SimpleRsvp(resp)) if(resp == "yes") => true
                            case _ => false
                          })
              println("next since %s until %s total %s next? %s" format(
                since, until, parsed.meta.total_count, parsed.meta.next
              ))
              JsonContent ~> ResponseString(generate(Events(evts, Some(since), Some(page + 1))))
            } catch {
              case StatusCode(401, _) =>
                println("auth expired or was revoked")
                ClearAccessToken ~> Redirect("/")
            }
            case _ => JsonContent ~> ResponseString("[]")
          }
        case _ => Redirect("/")
      }

    case Path("/timeline") & r =>
      AccessToken(r) match {
        case Some(access) => try {
          val mreq = Meetup.api / "2" / "member" / "self" <<? Map("access_token"->access)
          val memberJson = Caches("members")(mreq.to_uri.toString) match {
            case Some(value) =>
              value
            case _ =>
              val json = http(mreq >- { s => s })
              Caches("members")(mreq.to_uri.toString, json)
              json
          }
          val mem = parse[Member](memberJson)
          val since = mem.joined
          val until = new DateMidnight().plus(Period.weeks(2)).getMillis
          val ereq = eventsBetween(access, since, until, 0)
          val eventJson = Caches("events")(ereq.to_uri.toString) match {
            case Some(value) =>
              value
            case _ =>
              val json = http(ereq >- { s => s })
              Caches("events")(ereq.to_uri.toString, json)
              json
          }
          val parsed = parse[EventResponse](eventJson)
          println("starting since %s until %s total %s next? %s" format(
            since, until, parsed.meta.total_count, parsed.meta.next
          ))
          val evts = parsed.results
                      .filter(_.self.rsvp match {
                        case Some(SimpleRsvp(resp)) if(resp == "yes") => true
                        case _ => false
                      })
          Templates.timelineIndex(
            mem, evts, since, parsed.meta.next match {
              case Some(next) if(!next.isEmpty) => 1
               case _ => 0
            })
        } catch {
          case StatusCode(401, _) =>
            println("auth was expired or revoked")
            ClearAccessToken ~> Redirect("/")
        }
        case _ => Redirect("/")
      }
  }
}
