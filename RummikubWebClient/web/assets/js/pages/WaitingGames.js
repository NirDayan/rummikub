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
            /*
             * TODO: the last element should be a list of name
             */
            for (var i = 0; i < waitingGames.length; i++) {
                tableBody.append("<tr>" +
                        "<td>" + waitingGames[i].name + "</td>" +
                        "<td>" + waitingGames[i].humanPlayers + "</td>" +
                        "<td>" + waitingGames[i].computerizedPlayers + "</td>" +
                        "<td>" + waitingGames[i].joinedHumanPlayers + "</td><td>");
                var namesList = waitingGames[i].unjoinedPlayersNames;
                for (var j = 0; j < namesList.length; j++) {
                    tableBody.append(namesList[j] + " ");
                }
                tableBody.append("</td></tr>");
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