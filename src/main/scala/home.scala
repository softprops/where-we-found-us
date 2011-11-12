package com.meetup

import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

object Home {
  def intent: unfiltered.Cycle.Intent[Any, Any] = {
    case Path("/") & r => AccessToken(r) match {
      case Some(_) => println("we have auth"); Redirect("/timeline")
      case _       => println("we are all aliens");Templates.index
    }
  }
}
