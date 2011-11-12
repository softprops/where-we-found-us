package com.meetup

import xml._

import unfiltered.response.Html

object Layout {
  def apply(id: String)(body: NodeSeq)(styles: String*)(scripts: String*) = Html(
    <html>
     <head>
       <title>where we met us</title>
       <link rel="stylesheet" type="text/css" href="/css/app.css"/>
       { styles.map { s =>
          <link rel="stylesheet" type="text/css" href={ "/css/%s.css".format(s) } />
       } }
       <script type="text/javascript" src="/js/jquery.min.js"></script>
     </head>
     <body id={id}>
       <div id="container">
         { body }
       </div>
       <script type="text/javascript" src="/js/app.js"></script>
       { scripts.map { s =>
         <script type="text/javascript" src={"/js/%s.js" format s}></script>
       } }
     </body>
    </html>
  )
}

object Templates {
  import java.util.Date
  import java.text.SimpleDateFormat

  def index = Layout("home")(
    <div>
      <div id="what">
       <h1>When we<br/>met <span>us</span>.</h1>
      </div>
      <div id="connect"><a class="btn" href="/auth">connect with Meetup <span></span></a></div>
    </div>
  )("home")("spin.min","home")

  def date(t: Long) = new SimpleDateFormat("MMM dd yyyy").format(new Date(t))

  def year(t: Long) = new SimpleDateFormat("yyyy").format(new Date(t))

  def event(e: Event, side: String) =
    <div id={ "e-%s" format e.id } class={ "box evt %s-side" format(side) }
       data-group={ "g-%s".format(e.group.id) }>
      <div class="head">
        <div><span class="sup">@</span><a target="_blank" href={ e.event_url }>{ e.name }</a></div>
        <div>with <span> { e.yes_rsvp_count - 1 }</span> others on <span class="time">{ date(e.time) }</span></div>
        <div class="sup grp-name">({ e.group.name })</div>
      </div>
    </div>

  def bar = <div class="bar">|</div>

  def timelineIndex(member: Member, evts: Seq[Event], since: Long, nextPage: Int) = Layout("timeline")(

    <div>
      <div id="mem" class="box">
       <div id="top-row">
         <div id="mem-photo">
          <img src={member.photo.get.photo_link}/>
         </div>
         <h1 id="mem-name">{member.name}</h1>
         <div id="mem-actions"><a href="/auth/disconnect" class="btn">sign out</a></div>
        </div>

        <div id="mem-details">
          <ul id="mem-stats" data-joined={ "j-%s".format(date(member.joined)) }
              data-joined-yr={ "j-%s".format(year(member.joined)) }>
           <li>Member since {date(member.joined) }</li>
           <li>Interested in <span>{ member.topics.size }</span> topics</li>
          </ul>
        </div>
      </div>
      <div id="yr-sel">
        <a href="#y-2011">2011</a>
      </div>
      <div id="tl" class="clearfix" data-since={ "s-%s" format since }
           data-next-page={ "p-%s" format nextPage }>
        { evts.groupBy(e => year(e.time)).map {
          case (yr, evts) =>
            <div id={ "y-%s" format(yr) }>
              <div class="bar"/>
              {<div class="year">{yr}</div> ++ (evts.grouped(2).map {
                case Seq(l, r) =>
                  event(l, "left") ++ event(r, "right")
                case Seq(l) =>
                  event(l, "left")
                case _ => <br/>
              })}
            </div>
        }
       }
      </div>

    </div>
  )()("jquery.scrollto.min", "stickyfloat.min")

}
