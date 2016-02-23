// Filename: MainGame.js
define([
    'jquery',
    'utils/PageErrorAlert'
], function (jQuery, PageErrorAlert) {
    function MainGame(gameName, playerName, playersNumber) {
        this.pollingInterval = null;
        this.gameName = gameName;
        this.playerName = playerName;
        this.playersNumber = playersNumber;
    }

    MainGame.prototype = {
        startPolling: function () {
            this.pollingInterval = setInterval(function () {
                var getEventsPolling = jQuery.get({
                    url: "./GetEvents"
                });

                getEventsPolling.done(function (events) {
                    this.handleGameEvents(events);
                }.bind(this));

                getEventsPolling.fail(function (errorMessage) {
                    (new PageErrorAlert()).show(errorMessage.responseText);
                });
            }.bind(this), 1000);
        },
        handleGameEvents: function () {
            return;
            //TODO...
        },
        initPlayersNames: function () {
            var tableRow = jQuery("#playersTable thead tr:first");
            for (var i = 0; i < this.playersNumber; i++) {
                tableRow.append("<th>" + (i == 0 ? this.playerName : "") + "</th>");
            }
        },
        initPlayerTiles: function () {
            jQuery.get({
                'url': "./getPlayerTiles"
            }).done(function (playerTiles) {
                
            }).fail(function () {

            });
        },
        initialize: function () {
            this.initPlayersNames();
            this.initPlayerTiles();
            this.startPolling();
        },
        close: function () {
            if (this.pollingInterval) {
                clearInterval(this.pollingInterval);
            }
        }
    };

    return MainGame;
});