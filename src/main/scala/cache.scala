package com.meetup

import redis.clients.jedis._

trait Cache[T] {
  def apply(k: String, v: T): T
  def apply(k: String): Option[T]
}

private case class RedisCache(pool: JedisPool, prefix: String) extends Cache[String] {

  private def key(k: String) = "%s:%s" format(prefix, k)

  def client[T](f: Jedis => T): T = {
    var r = pool.getResource()
    try { f(r) }
    finally { pool.returnResource(r) }
  }
  def apply(k: String, v: String) = client { r =>
    r.set(key(k), v)
    r.expire(key(k), 60*2)
    v
  }
  def apply(k: String) = client { r =>
    r.get(key(k)) match {
      case null  => None
      case value => Some(value)
    }
  }
}

object Caches {
  lazy val pool = new JedisPool(new JedisPoolConfig(), "localhost")
  private lazy val caches = Map(
    "events" -> RedisCache(pool, "events"),
    "members" -> RedisCache(pool, "members")
  )
  def apply(name: String): Cache[String] = caches(name)
}
