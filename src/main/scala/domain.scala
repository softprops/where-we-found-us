package com.meetup

case class Service(identifier: String)
case class Services(twitter: Option[Service], facebook: Option[Service], tumblr: Option[Service])

case class Member(id: Int,  name: String, bio: Option[String],
                  country: String, city: String, state: Option[String],
                  joined: Long, lang: Option[String], lat: String, lon: String,
                  link: String,services: Option[Services],
                  photo: Option[Photo],
                  photo_url: Option[String],
                  visited: Long,
                  topics: Seq[Topic])


case class Topic(id: Int, urlkey: String, name: String)
case class SimpleMember(member_id: String, member_name: String)
case class Photo(highres_link: String, photo_id: Int, photo_link: String, thumb_link: String)
case class Group(id: Int, name: String, urlname: String,
                 group_lat: String, group_lon: String, group_photo: Option[Photo],
                 join_mode: Option[String])
case class SimpleRsvp(response: String)
case class Self(actions: Seq[String], rsvp: Option[SimpleRsvp])
case class Venue(id: Int, name: Option[String], lat: String, lon: String,
                 city: Option[String], state: Option[String],
                 country: Option[String], zip: Option[String],
                 address_1: Option[String], address_2: Option[String], address_3: Option[String],
                 phone: Option[String])
case class Event(id: String, name: String, description: Option[String],
                 hosts: Option[Seq[SimpleMember]], event_url: String, group: Group,
                 self: Self, yes_rsvp_count: Int, utc_offset: Long,
                 venue: Option[Venue], maybe_rsvp_count: Int, visibility: String,
                 status: String, time: Long)
case class EventResponse(results: Seq[Event], meta: Meta)
case class Meta(id: String, title: String, count: String,
                updated: String, description: String,
                next: Option[String], link: String, method: String,
                total_count: Int, url: String, prev: Option[String],
                lat: String, lon: String)
case class Events(events: Seq[Event], since: Option[Long], nextPage: Option[Int])
