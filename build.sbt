organization := "com.meetup"

name := "where-we-met-us"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.0",
  "net.databinder" %% "unfiltered-netty-server" % "0.5.0",
  "com.codahale" %% "jerkson" % "0.4.2",
  "redis.clients" % "jedis" % "2.0.0",
  "joda-time" % "joda-time" % "1.6.2"
)

resolvers += "jerks" at "http://repo.codahale.com"
