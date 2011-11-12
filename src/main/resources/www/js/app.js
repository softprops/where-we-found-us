(function($){ $(function() {
  var months = {0:'Jan',1:'Feb',2:'Mar',3:'Apr',4:'May',5:'Jun',6:'Jul',7:'Aug',8:'Sep',9:'Oct',10:'Nov',11:'Dec'},
  date = function(d) { return [months[d.getMonth()], ' ', d.getDate(), ', ', d.getFullYear()].join('');},
  event = function(e, side) {
      return ['<div id="e-',e.id,'" class="box evt ',side,'-side" data-group="g-',e.group.id,'">',
              '<div class="head"><div><span class="sup">@</span> <a target="_blank" href="',
              e.event_url,'">',e.name,'</a></div></div>',
              '<div> with <span>' , (e.yes_rsvp_count -1), '</span> others on <span class="time">',
              date(new Date(e.time)), '</span></div><div class="sup grp-name">(',e.group.name,')</div',
             '</div>'].join('');
  };
  //$('#yr-sel').stickyfloat({duration: 400, offsetY:300 });
  var render = function(evts) {
    var partition = function(res, ary, n) {
      var head = ary.slice(0, n), tail = ary.slice(n);
      res.push(head);
      if(tail.lenth > n) {
         return partition(res, tail, n);
      } else {
        if(tail && tail.length > 0) {
          res.push(tail);
        }
        return res;
      }
    },
    grouped = function(f, ary){
      var grps = [];
      for(i in ary) {
        var e = ary[i], k = f(e);
        if(i in grps) { grps[k].push(e); }
        else { grps[k] = [e]; }
      }
      return grps;
    };
    var byYear = grouped(function(e){ return new Date(e.time).getFullYear(); }, evts);
    for(y in byYear) {
      var target = $("#y-"+y);
      if(!target.length) {
         target = $(['<div id="y-',y,'"/>'].join(''));
         $("#tl").append(target);
         target.append('<div class="bar"/>')
         $("#yr-sel").append(['<a href="#y-',y,'">',y,'</a>'].join(''));
         target.append(['<div class="year">',y,'</div>'].join(''));
      }
      var year = byYear[y], buffer = [];
      var partitioned = partition([], year, 2);
      for(i in partitioned) {
        var pair = partitioned[i];
        buffer.push(event(pair[0], 'left'));
        if(pair[1]) {
          buffer.push(event(pair[1], 'right'));
        }
      }
      target.append(buffer.join(''));
    }
  };
  var poll = function() {
    var ds = $("div[data-since|='s']").data()
      , since = (ds || {})['since']
      , page  = (ds || {})['nextPage'];
    if(since) {
      var query = function(snc, pg) {
        $.get("/timeline/more", { "since": snc, "page": pg }
              , function(data){
                  if(data.events && data.events.length > 0) {
                      render(data.events);
                      query(data.since, data.nextPage);
                  } else {
                      var stats = $("#mem-stats").data()
                      , joined = stats['joined'].substring(2)
                      , joinYr = stats['joinedYr'].substring(2)
                      , target = $("#y-" + joinYr);
                      if(!target.length) {
                          target = $(['<div id="y-',joinYr,'"/>'].join(''));
                          $("#tl").append(target)
                          target.append('<div class="bar"></div>');
                          $("#yr-sel").append(['<a href="#y-',joinYr,'">',joinYr,'</a>'].join(''));
                          target.append(['<div class="year">',joinYr,'</div>'].join(''));
                      };
                      target.append(['<div class="box" id="you-joined">Your <a target="_blank"'
                                     , ' href="http://www.meetup.com/">Meetup</a> was born on '
                                     , joined,'</div>'].join(''));
                  }
         });
       };
      query(since.substring(2), page.substring(2));
    }
  };
  poll();
  /*$("#yr-sel a").live('click', function(e){
      e.preventDefault();
      $.ScrollTo($(this).attr('href'));
      return false;
  });*/
  $(".evt").live({
    mouseenter: function() {
      var gid = $(this).data()['group'];
      $(".evt[data-group!='"+gid+"']").addClass('blur');
    },
    mouseleave: function() {
      var gid = $(this).data()['group']
      $(".evt[data-group!='"+gid+"']").removeClass('blur');
    }
  });
});})(jQuery);