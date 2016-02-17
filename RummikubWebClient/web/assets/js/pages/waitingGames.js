// Filename: app.js
define([
    'jquery'
], function (jQuery) {
    var updateWaitingGamesTable = function (waitingGames) {
        var tableBody = jQuery("#waitingGamesTable tbody").empty();
        /*
         * TODO: the last element should be a list of name
         */
        for (var i = 0; i < waitingGames.length; i++) {
            tableBody.append("<tr>" +
                    "<td>" + waitingGames[i].gameDetails.name + "</td>" +
                    "<td>" + waitingGames[i].gameDetails.humanPlayers + "</td>" +
                    "<td>" + waitingGames[i].gameDetails.computerizedPlayers + "</td>" +
                    "<td>" + waitingGames[i].gameDetails.joinedHumanPlayers + "</td>" +
                    "<td>" + waitingGames[i].gameDetails.name + "</td>" +
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

        $("#creatGameBtn").click(function () {
                    $(".waitingGames").hide();
                    $(".newGame").show();
                });
    };

    return {
        initialize: initialize
    };
});