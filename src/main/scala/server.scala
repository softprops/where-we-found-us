package com.meetup

object Server {
  import unfiltered._

  def main(args: Array[String]) {
    val (key, sec) = args match {
      case Array(k, s) => (k, s)
      case _ => ("CLIENT_KEY","CLIENT_SECRET")
    }
    val client = Client(key, sec)
    netty.Http(8080)
     .resources(getClass().getResource("/www/"))
     .handler(netty.cycle.Planify{
       Home.intent orElse Oauth(client).intent orElse Timeline(client).intent
     }).run// should call Caches.pool.destroy() on shutdown
  }
}
