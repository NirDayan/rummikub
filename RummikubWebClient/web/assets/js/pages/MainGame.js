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
        handleGameEvents: function (events) {

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
        switchBackground: function () {
            jQuery("body").removeClass("waitingGamesBackground").addClass("mainGameBackground");
        },
        handlePullTile: function () {
            jQuery.get({
                'url': "./pullTile"
            }).done(function () {

            }).fail(function () {

            });
        },
        handleResign: function () {
            jQuery.get({
                'url': "./resign"
            }).done(function () {

            }).fail(function () {

            });
        },
        handleFinishTurn: function () {
            jQuery.get({
                'url': "./finishTurn"
            }).done(function () {

            }).fail(function () {

            });
        },
        handleMainMenu: function () {
            window.location.hash = "waitingGames";
        },
        initButtons: function () {
            jQuery("#mainMenu").on("click", this.handleMainMenu.bind(this));
            jQuery("#pullTile").on("click", this.handlePullTile.bind(this));
            jQuery("#resign").on("click", this.handleResign.bind(this));
            jQuery("#finishTurn").on("click", this.handleFinishTurn.bind(this));
        },
        initialize: function () {
            this.initButtons();
            this.switchBackground();
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