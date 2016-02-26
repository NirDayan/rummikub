// Filename: MainGame.js
define([
    'jquery',
    'libs/jquery-ui',
    'utils/PageErrorAlert'
], function (jQuery, jQueryUI, PageErrorAlert) {
    var SOUNDS = {
        GAME_STARTED: "",
        PLAYER_TURN: "",
    };

    function MainGame(gameName, playerName, playersNumber) {
        this.pollingInterval = null;
        this.events = null;
        this.gameName = gameName;
        this.playerName = playerName;
        this.playersNumber = playersNumber;
        this.isPlayerPerformAnyChange = false;
        this.isEnabled = false; //isEnabled == true when this is the player turn        
        this.eventsHandlers = {
            GAME_START: this.handleGameStart.bind(this),
            GAME_OVER: this.handleGameOver.bind(this),
            GAME_WINNER: this.handleGameWinner.bind(this),
            PLAYER_TURN: this.handlePlayerTurn.bind(this),
            PLAYER_FINISHED_TURN: this.handlePlayerFinishedTurn.bind(this),
            PLAYER_RESIGNED: this.handlePlayerResigned.bind(this),
            SEQUENCE_CREATED: this.handleSequenceCreated.bind(this),
            TILE_ADDED: this.handleTileAdded.bind(this),
            TILE_RETURNED: this.TileReturned.bind(this),
            TILE_MOVED: this.handleTileMoved.bind(this),
            REVERT: this.handleRevert.bind(this)
        };
    }

    MainGame.prototype = {
        startPolling: function () {
            this.pollingInterval = setInterval(function () {
                var getEventsPolling = jQuery.get("./GetEvents");

                getEventsPolling.done(function (events) {
                    this.events = events;
                    this.handleNextEvent();
                }.bind(this));

                getEventsPolling.fail(function (errorMessage) {
                    (new PageErrorAlert()).show(errorMessage.responseText);
                });
            }.bind(this), 1000);
        },
        handleNextEvent: function () {
            var event, eventHandler;
            if (this.events && this.events.length) {
                event = this.events.shift();//removes the first event and handle it
                eventHandler = this.eventsHandlers[event.type];
                if (eventHandler) {
                    eventHandler(event).done(function () {
                        this.handleNextEvent();
                    }.bind(this));
                }
            }
        },
        initPlayersNames: function () {
            var tableRow = jQuery("#playersTable thead tr:first");
            for (var i = 0; i < this.playersNumber; i++) {
                tableRow.append("<th>" + (i == 0 ? this.playerName : "") + "</th>");
            }
        },
        updatePlayersNames: function (playersDetails) {
            var tablePlayerNamesCollection = jQuery("#playersTable thead th");
            for (var i = 0; i < playersDetails.length; i++) {
                tablePlayerNamesCollection.eq(i).text(playersDetails[i].name);
            }
        },
        updatePlayerNamesWithCurrentPlayer: function (playerName) {
            var tablePlayerNamesCollection = jQuery("#playersTable thead th");
            tablePlayerNamesCollection.removeClass("currentPlayer");
            for (var i = 0; i < tablePlayerNamesCollection.length; i++) {
                if (tablePlayerNamesCollection.eq(i).text() == playerName) {
                    tablePlayerNamesCollection.eq(i).addClass("currentPlayer");
                    break;
                }
            }
        },
        updatePlayerTiles: function (playerDetails) {
            var playerTiles = playerDetails.tiles;
            var playerStand = jQuery("#playerTilesList");
            jQuery("ul.droptrue").sortable({
                connectWith: "ul"
            });
            jQuery("ul.dropfalse").sortable({
                connectWith: "ul",
                dropOnEmpty: false
            });
            playerStand.disableSelection();

            for (var i = 0; i < playerTiles.length; i++) {
                playerStand.append('<li class="ui-state-default">' +
                        '<div><img class="tileImg" src="assets/images/tiles/' +
                        playerTiles[i].color.toLowerCase() + '/' + playerTiles[i].value + '.png" ' +
                        'width=40px></div></li>');
            }
        },
        switchBackground: function () {
            jQuery("body").removeClass("waitingGamesBackground").addClass("mainGameBackground");
        },
        handlePullTile: function () {
            jQuery.get("./pullTile").done(function () {

            }).fail(function () {

            });
        },
        handleResign: function () {
            jQuery.get("./resign").done(function () {
                //Go back to waitingGames page
                window.location.hash = "waitingGames";
            }).fail(function (errorMessage) {
                (new PageErrorAlert()).show(errorMessage.responseText);
            });
        },
        handleFinishTurn: function () {
            jQuery.get("./finishTurn").done(function () {

            }).fail(function () {

            });
        },
        initButtons: function () {
            jQuery("#mainMenu").on("click", this.handleResign.bind(this));
            jQuery("#pullTile").on("click", this.handlePullTile.bind(this));
            jQuery("#resign").on("click", this.handleResign.bind(this));
            jQuery("#finishTurn").on("click", this.handleFinishTurn.bind(this));
        },
        setGameEnabled: function (isEnabled) {
            var buttons = jQuery("#mainGameButtonsContainer .actionButton");
            if (isEnabled) {
                buttons.attr("disabled", false);
                this.isEnabled = true;
            } else {
                buttons.attr("disabled", true);
                this.isEnabled = false;
            }
        },
        playSound: function (soundType) {
            //TODO: make sound :)
        },
        handleGameStart: function () {
            this.playSound(SOUNDS.GAME_STARTED);

            var playersDetailsPromise = jQuery.get("./playersDetails").done(this.updatePlayersNames.bind(this));
            var currentPlayerDetailsPromise = jQuery.get("./playerDetails").done(this.updatePlayerTiles.bind(this));

            return jQuery.when(playersDetailsPromise, currentPlayerDetailsPromise);
        },
        handleGameOver: function () {
            return new jQuery.Deferred().resolve();
        },
        handleGameWinner: function () {
            return new jQuery.Deferred().resolve();
        },
        handlePlayerTurn: function (event) {
            if (event.playerName.toLowerCase() === this.playerName) {
                this.setGameEnabled(true);
                this.playSound(SOUNDS.PLAYER_TURN);
                this.isPlayerPerformAnyChange = true;
            }

            this.updatePlayerNamesWithCurrentPlayer(event.playerName);

            return new jQuery.Deferred().resolve();
        },
        handlePlayerFinishedTurn: function () {
            return new jQuery.Deferred().resolve();
        },
        handlePlayerResigned: function () {
            return new jQuery.Deferred().resolve();
        },
        handleSequenceCreated: function () {
            return new jQuery.Deferred().resolve();
        },
        handleTileAdded: function () {
            return new jQuery.Deferred().resolve();
        },
        TileReturned: function () {
            return new jQuery.Deferred().resolve();
        },
        handleTileMoved: function () {
            return new jQuery.Deferred().resolve();
        },
        handleRevert: function () {
            return new jQuery.Deferred().resolve();
        },
        initialize: function () {
            this.initButtons();
            this.switchBackground();
            this.initPlayersNames();
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