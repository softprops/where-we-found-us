(function($){ $(function(){
  $("#connect").click(function(e){
    $("a", this).addClass("disabled").empty();
      new Spinner({
        lines: 10,
        length: 4,
        width: 4,
        radius: 6,
        color: '#fff',
        speed: 1.3,
        trail: 51,
        shadow: true
    }).spin($(this)[0]);
  });
});})(jQuery);