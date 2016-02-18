// Filename: WaitingGames.js
define([
    'jquery',
    'underscore'
], function (jQuery, _) {
    function WaitingGames() {
        this.pollingInterval = null;
        this.currentWaitingGames = null;
    }

    WaitingGames.prototype = {
        updateWaitingGamesTable: function (waitingGames) {
            var tableBody = jQuery("#waitingGamesTable tbody").empty();
            var namesString = "";
            var namesList;

            for (var i = 0; i < waitingGames.length; i++) {
                namesList = waitingGames[i].unjoinedPlayersNames;
                for (var j = 0; j < namesList.length; j++) {
                    namesString += namesList[i];
                    if (j != namesList.length - 1) {
                        namesString += ", ";
                    }
                }
                tableBody.append("<tr>" +
                        "<td>" + waitingGames[i].name + "</td>" +
                        "<td>" + waitingGames[i].humanPlayers + "</td>" +
                        "<td>" + waitingGames[i].computerizedPlayers + "</td>" +
                        "<td>" + waitingGames[i].joinedHumanPlayers + "</td>" +
                        "<td>" + namesString + "</td>");

            }
        },
        initialize: function () {
            this.pollingInterval = setInterval(function () {
                var waitingGamesRequest = jQuery.ajax({
                    url: "./waitingGames"
                });

                waitingGamesRequest.done(function (waitingGames) {
                    if (this.currentWaitingGames && _.isEqual(this.currentWaitingGames, waitingGames)) {
                        //List has not been changed. Do Nothing...
                        return;
                    }
                    this.currentWaitingGames = waitingGames;
                    this.updateWaitingGamesTable(waitingGames);
                }.bind(this));

                waitingGamesRequest.fail(function (err) {
                    //TODO
                    return;
                });
            }.bind(this), 1000);
        },
        close: function () {
            if (this.pollingInterval) {
                clearInterval(this.pollingInterval);
            }
        }
    };

    return WaitingGames;
});