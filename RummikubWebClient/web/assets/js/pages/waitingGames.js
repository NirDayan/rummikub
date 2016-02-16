// Filename: app.js
define([
    'jquery'
], function (jQuery) {
    var updateWaitingGamesTable = function (waitingGames) {
        var tableBody = jQuery("#waitingGamesTable tbody").empty();
        /*
         * TODO: we need to change the JSON object to structure like:
         * [
         *  {gameName: "game1", humanPlayers, compPlayers, joinedHuman, playerNames}
         *  {gameName: "game2", humanPlayers, compPlayers, joinedHuman, playerNames}
         *  {gameName: "game3", humanPlayers, compPlayers, joinedHuman, playerNames}
         * ]
         */
        for (var i = 0; i < waitingGames.length; i++) {
            tableBody.append("<tr>" +
                    "<td>" + waitingGames[i] + "</td>" +
                    "<td>" + waitingGames[i] + "</td>" +
                    "<td>" + waitingGames[i] + "</td>" +
                    "<td>" + waitingGames[i] + "</td>" +
                    "<td>" + waitingGames[i] + "</td>" +
                    "</tr>");
        }
    };

    var initialize = function () {
        var waitingGamesRequest = jQuery.ajax({
            url: "./waitingGames"
        });

        waitingGamesRequest.done(function (waitingGames) {
            updateWaitingGamesTable(waitingGames);
        });

        waitingGamesRequest.fail(function () {
            //TODO
        });
    };

    return {
        initialize: initialize
    };
});