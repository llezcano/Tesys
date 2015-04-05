requirejs(["common"], function (common) { 
  requirejs(["app", "handlers"], function(App) {
    App.start() ;
  });
});
