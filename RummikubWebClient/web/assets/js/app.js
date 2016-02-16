// Filename: app.js
define([
  'jquery',
  'bootstrap',
  'pages/waitingGames'
], function(jQuery, bootstrap, waitingGamesPage) {
  var initialize = function() {
      //First draw waitingGames page
      waitingGamesPage.initialize();
  };

  return { 
    initialize: initialize
  };
});