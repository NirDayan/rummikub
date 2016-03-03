// Filename: MainGame.js
define([
    'jquery',
    'libs/jquery-ui',
    'utils/PageErrorAlert',
    'utils/PageInfoAlert'
], function (jQuery, jQueryUI, PageErrorAlert, PageInfoAlert) {
    var SOUNDS = {
        GAME_STARTED: "GAME_STARTED",
        PLAYER_TURN: "PLAYER_TURN"
    };
    var PLAYER_STAND_INDEX = -1;

    function MainGame(gameName, playerName, playersNumber) {
        this.pollingInterval = null;
        this.events = [];
        this.playerTilesModel = null;
        this.BoardTilesModel = [];
        this.gameWinner = null;
        this.gameName = gameName;
        this.playerName = playerName;
        this.playersNumber = playersNumber;
        this.isPlayerPerformAnyChange = false;
        this.isEnabled = false; //isEnabled == true when this is the player turn
        this.tileDragged = {srcIndex: -1, srcPosition: -1, destIndex: -1, destPosition: -1};
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
                    this.events = this.events.concat(events);
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
                console.log('Handling: ' + event.type);
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
            tableRow.empty();
            for (var i = 0; i < this.playersNumber; i++) {
                tableRow.append("<th>" + (i === 0 ? this.playerName : "") + "</th>");
            }
        },
        updatePlayersNames: function (playersDetails) {
            var tablePlayerNamesCollection = jQuery("#playersTable thead th");
            for (var i = 0; i < playersDetails.length; i++) {
                if (playersDetails[i].name.toLowerCase() === this.playerName.toLowerCase()) {
                    tablePlayerNamesCollection.eq(i).css("color", "blue");
                }
                tablePlayerNamesCollection.eq(i).text(playersDetails[i].name);
            }
        },
        updatePlayerNamesWithCurrentPlayer: function (playerName) {
            var tablePlayerNamesCollection = jQuery("#playersTable thead th");
            tablePlayerNamesCollection.removeClass("currentPlayer");
            for (var i = 0; i < tablePlayerNamesCollection.length; i++) {
                if (tablePlayerNamesCollection.eq(i).text().toLowerCase() === playerName.toLowerCase()) {
                    tablePlayerNamesCollection.eq(i).addClass("currentPlayer");
                    break;
                }
            }
        },
        initPlayerTilesView: function () {
            jQuery('#playerTilesList').sortable({
                connectWith: '.sortable',
                tolerance: "touch",
                revert: true,
                start: this.onPlayerStartDrag.bind(this),
                beforeStop: this.onPlayerStopDrag.bind(this)
            }).disableSelection();
        },
        updatePlayerTilesView: function () {
            var playerStand = jQuery("#playerTilesList");
          
            playerStand.empty();
            for (var i = 0; i < this.playerTilesModel.length; i++) {
                this.addTileToSequecne(playerStand, this.playerTilesModel[i]);
                this.addJqueryTileData(playerStand.find('li').last(),
                PLAYER_STAND_INDEX, i, this.playerTilesModel[i]);
            }
        },
        addTileToSequecne: function (sequenceElem, tile) {
            sequenceElem.append('<li>' +
                        '<div><img class="tileImg" src="assets/images/tiles/' +
                        tile.color.toLowerCase() +
                        '/' + tile.value + '.png">' +
                        '</div></li>');
        },
        updateBoradTilesView: function () {
            var boardView = jQuery("#board");
            boardView.empty();
            for (var i = 0; i < this.BoardTilesModel.length; i++) {
                var boardSequecne = this.BoardTilesModel[i];
                boardView.append('<ul class="boardSequence sortable"></ul>');
                var sequenceElem = jQuery(".boardSequence").last();
                
                for (var j = 0; j < boardSequecne.length; j++) {
                    this.addTileToSequecne(sequenceElem, boardSequecne[j]);
                    this.addJqueryTileData(jQuery(sequenceElem).find('li').last(),
                    i, j, boardSequecne[j]);
                }
                
                sequenceElem.sortable({
                    connectWith: '.sortable',
                    tolerance: "touch",
                    revert: true,
                    start: this.onBoardStartDrag.bind(this),
                    beforeStop: this.onBoardStopDrag.bind(this)
                });
                
                if (!this.isEnabled){
                    sequenceElem.sortable('disable');
                }
            }
        },
        addJqueryTileData: function (jQueryObj, seqIndex, seqPosition, tile){
            jQueryObj.data("Index", seqIndex);
            jQueryObj.data("Position", seqPosition);
            jQueryObj.data("Tile", tile);
        },
        onPlayerStartDrag: function (event, ui) {
            //Add new empty sequence
            var emptySequence = jQuery('<ul class="boardSequence sortable"></ul>');

            //Add PlaceHolder
            emptySequence.append('<li class="placeholder"><div>' +
                    '<img class="tileImg" src="assets/images/plus_tile.png">' +
                    '</div></li>');

            emptySequence.sortable({
                connectWith: '.sortable',
                tolerance: "touch"
            });
            jQuery("#board").append(emptySequence);
            
            jQuery("#playerTilesList, .boardSequence").sortable("refresh");

            this.tileDragged = {};
            this.tileDragged.srcIndex = ui.item.data().Index;
            this.tileDragged.srcPosition = ui.item.data().Position;
        },
        onBoardStartDrag: function (event, ui) {
            this.onPlayerStartDrag(event,ui);
        },
        onPlayerStopDrag: function (event, ui) {
            console.log('onPlayerStopDrag');
            if (jQuery(ui.placeholder).parent().hasClass('boardSequence') === true) {
                //On Board Drop From Player
                this.tileDragged.destIndex = ui.item.parent().index();
                this.tileDragged.destPosition = ui.item.index();
                this.performAddTile(ui.item.data().Tile);
            }
            this.updateBoradTilesView();
        },
        onBoardStopDrag: function (event, ui) {
            console.log('onBoardStopDrag');
            if (jQuery(ui.item).parent().hasClass('boardSequence') === true) {
                //From Board To Borad
                this.tileDragged.destIndex = ui.item.parent().index();
                this.tileDragged.destPosition = ui.item.index();
                this.performMoveTile(ui.item.data().Tile);
                
            } else if(jQuery(ui.item).parent().attr('id') === 'playerTilesList'){
                //From Board To Player
                this.performTakeBackTile(ui.item.data().Tile);
                
            }else{
                console.log('onBoardStopDrag: unknown destination');
            }
            this.updateBoradTilesView();
        },
        performAddTile: function (tile) {
            console.log('performAddTile (index, pos): '+ this.tileDragged.destIndex +
                    ' ' + this.tileDragged.destPosition);
            jQuery.post("./addTile", {
                tile: tile,
                sequenceIndex: this.tileDragged.destIndex,
                sequencePosition: this.tileDragged.destPosition
            }).done(function () {
                
            }.bind(this)).fail(function (errorMessage) {
                (new PageErrorAlert()).show(errorMessage.responseText);
            });
        },
        performMoveTile: function (tile) {
            if (!this.isMoveTileValid()) {
                (new PageErrorAlert()).show("Invalid move tile action");
                return;
            }
            console.log('performMoveTile (srcInd, srcPos, Ind, Dest): ' +
                    this.tileDragged.srcIndex + ' ' +
                    this.tileDragged.srcPosition + ' ' +
                    this.tileDragged.destIndex + ' ' +
                    this.tileDragged.destPosition + ' '
                    );
            jQuery.post("./moveTile", {
                sourceSequenceIndex: this.tileDragged.srcIndex,
                sourceSequencePosition: this.tileDragged.srcPosition,
                targetSequenceIndex: this.tileDragged.destIndex,
                targetSequencePosition: this.tileDragged.destPosition,
            }).done(function () {
                
            }.bind(this)).fail(function (errorMessage) {
                (new PageErrorAlert()).show(errorMessage.responseText);
            });
        },
        isMoveTileValid: function (){
            if (this.tileDragged.srcIndex === this.tileDragged.destIndex) {
                var seqSize = this.BoardTilesModel[this.tileDragged.destIndex].length;

                if (this.tileDragged.destPosition === seqSize) {
                    return false;
                }
            }

            return true;
        },
        performTakeBackTile: function (tile) {
            console.log('performTakeBackTile (srcInd, srcPos): ' +
                    this.tileDragged.srcIndex + ' ' +
                    this.tileDragged.srcPosition + ' ' 
                    );
            jQuery.post("./takeBackTile", {
                    sequenceIndex: this.tileDragged.srcIndex,
                    sequencePosition: this.tileDragged.srcPosition
                }).fail(function (errorMessage) {
                    (new PageErrorAlert()).show(errorMessage.responseText);
                });
        },
        switchBackground: function () {
            jQuery("body").removeClass("waitingGamesBackground").addClass("mainGameBackground");
        },
        handlePullTile: function () {
            if (this.isPlayerPerformAnyChange) {
                (new PageErrorAlert()).show("Pull tile from deck is not possible since you performed board changes");
                return;
            }
            jQuery.get("./finishTurn").fail(function (errorMessage) {
                (new PageErrorAlert()).show(errorMessage.responseText);
            });
            this.setGameEnabled(false);
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
            if (!this.isPlayerPerformAnyChange) {
                (new PageErrorAlert()).show("No Changes have been made to the board");
                return;
            }
            jQuery.get("./finishTurn").fail(function (errorMessage) {
                (new PageErrorAlert()).show(errorMessage.responseText);
            });

            this.setGameEnabled(false);
        },
        initButtons: function () {
            jQuery("#mainMenu").on("click", this.handleResign.bind(this));
            jQuery("#pullTile").on("click", this.handlePullTile.bind(this));
            jQuery("#resign").on("click", this.handleResign.bind(this));
            jQuery("#finishTurn").on("click", this.handleFinishTurn.bind(this));
        },
        disableButtons: function () {
            jQuery("#mainMenu").off("click");
            jQuery("#pullTile").off("click");
            jQuery("#resign").off("click");
            jQuery("#finishTurn").off("click");
        },
        setGameEnabled: function (isEnabled) {
            var buttons = jQuery("#mainGameButtonsContainer .actionButton");
            var dndElements = jQuery(".sortable");
            if (isEnabled) {
                buttons.attr("disabled", false);
                dndElements.sortable("enable");
                this.isEnabled = true;
            } else {
                buttons.attr("disabled", true);
                dndElements.sortable("disable");
                this.isEnabled = false;
            }
            
        },
        playSound: function (soundType) {
            if (soundType === SOUNDS.PLAYER_TURN) {
                document.getElementById("playerTurnAudio").play();
            } else {
                document.getElementById("gameStartAudio").play();
            }            
        },
        handleGameStart: function () {
            this.playSound(SOUNDS.GAME_STARTED);
            
            var playersDetailsPromise = jQuery.get("./playersDetails").done(this.updatePlayersNames.bind(this));
            var currentPlayerDetailsPromise = jQuery.get("./playerDetails").done(function (playerDetails) {
                this.playerTilesModel = playerDetails.tiles;
                this.updatePlayerTilesView();
                (new PageInfoAlert()).show("Game started!");
            }.bind(this));

            return jQuery.when(playersDetailsPromise, currentPlayerDetailsPromise);
        },
        handleGameOver: function () {
            //Go to gameOver page
            window.location.hash = "gameOver";
            return new jQuery.Deferred().resolve();
        },
        handleGameWinner: function (event) {
            this.gameWinner = event.playerName;
            return this.handleGameOver();
        },
        handlePlayerTurn: function (event) {
            if (event.playerName.toLowerCase() === this.playerName.toLowerCase()) {
                this.setGameEnabled(true);
                this.playSound(SOUNDS.PLAYER_TURN);
                this.isPlayerPerformAnyChange = false;
            }else{
                this.setGameEnabled(false);
            }

            this.updatePlayerNamesWithCurrentPlayer(event.playerName);

            return new jQuery.Deferred().resolve();
        },
        handlePlayerFinishedTurn: function (event) {
            if (event.tiles) {
                if (event.playerName.toLowerCase() === this.playerName.toLowerCase()) {
                    this.playerTilesModel.push(event.tiles[0]);
                    this.updatePlayerTilesView();
                }
                (new PageInfoAlert()).show(event.playerName + " has pulled a tile from the deck");
            }

            return new jQuery.Deferred().resolve();
        },
        handlePlayerResigned: function (event) {
            var tablePlayerNamesCollection = jQuery("#playersTable thead th");
            for (var i = 0; i < tablePlayerNamesCollection.length; i++) {
                if (tablePlayerNamesCollection.eq(i).text().toLowerCase() === event.playerName.toLowerCase()) {
                    tablePlayerNamesCollection.eq(i).text("");
                    break;
                }
            }

            (new PageInfoAlert()).show(event.playerName + " has Resigned from game");
            return new jQuery.Deferred().resolve();
        },
        handleSequenceCreated: function (event) {
            if (event.playerName.toLowerCase() === this.playerName.toLowerCase()
                    && event.tiles) {
                //Remove Tile From Player
                var indexToRemove = this.playerTilesModel.findIndex(function (e) {
                    return _.isEqual(e, event.tiles[0]);
                });
                this.playerTilesModel.splice(indexToRemove, 1);
                
                this.updatePlayerTilesView();
                this.isPlayerPerformAnyChange = true;
            } else if (event.playerName !== "") {
                (new PageInfoAlert()).show(event.playerName + " has added a new sequence");
            }
            var newSequence = [];
            if (event.tiles) {
                newSequence = newSequence.concat(event.tiles);
            }
            this.BoardTilesModel.push(newSequence);
            this.updateBoradTilesView();

            return new jQuery.Deferred().resolve();
        },
        handleTileAdded: function (event) {
            if (event.playerName.toLowerCase() === this.playerName.toLowerCase()) {

                //Remove Tile From Player
                var indexToRemove = this.playerTilesModel.findIndex(function(e){
                    return _.isEqual(e,event.tiles[0]);
                });
                this.playerTilesModel.splice(indexToRemove, 1);
                
                this.isPlayerPerformAnyChange = true;
                this.updatePlayerTilesView();
            }else{
                (new PageInfoAlert()).show(event.playerName + " has added tile to board");
            }
            
            //Add Tile To Board
            this.BoardTilesModel[event.targetSequenceIndex]
                    .splice(event.targetSequencePosition, 0, event.tiles[0]);
            
            this.updateBoradTilesView();
            return new jQuery.Deferred().resolve();
        },
        TileReturned: function (event) {
            if (event.playerName.toLowerCase() === this.playerName.toLowerCase()) {
                //Add Tile To Player
                this.playerTilesModel.push(event.tiles[0]);
                this.isPlayerPerformAnyChange = true;
                this.updatePlayerTilesView();
            } else {
                (new PageInfoAlert()).show(event.playerName + " has taken a tile back");
            }
            //Remove Tile From Board
            this.BoardTilesModel[event.sourceSequenceIndex]
                    .splice(event.sourceSequencePosition, 1);
            if (this.BoardTilesModel[event.sourceSequenceIndex].length === 0) {
                this.BoardTilesModel.splice(event.sourceSequenceIndex, 1);
            }
            this.updateBoradTilesView();
            
            return new jQuery.Deferred().resolve();
        },
        handleTileMoved: function (event) {
            //Remove Tile From Board
            var tileRemoved = this.BoardTilesModel[event.sourceSequenceIndex]
                    .splice(event.sourceSequencePosition, 1)[0];
                
            //Add Tile To Board
            this.BoardTilesModel[event.targetSequenceIndex]
                    .splice(event.targetSequencePosition, 0, tileRemoved);
            
            this.updateBoradTilesView();
            
            return new jQuery.Deferred().resolve();
        },
        handleRevert: function (event) {
            var deferred = new jQuery.Deferred();
            if (event.playerName.toLowerCase() === this.playerName.toLowerCase()) {
                this.BoardTilesModel = [];
                this.updateBoradTilesView();
                jQuery.get("./playerDetails").done(function (playerDetails) {
                    this.playerTilesModel = playerDetails.tiles;
                    this.updatePlayerTilesView();

                    (new PageInfoAlert()).show(event.playerName + " you punished with 3 tiles");
                    deferred.resolve();
                }.bind(this)).fail(function (err) {
                    deferred.reject(err);
                });
            } else {
                (new PageInfoAlert()).show(event.playerName + "player" + event.playerName + " was punished with 3 tiles");
                deferred.resolve();
            }
            return deferred.promise();
        },
        getGameWinner: function () {
            return this.gameWinner;
        },
        initialize: function () {
            this.initButtons();
            this.switchBackground();
            this.initPlayersNames();
            this.initPlayerTilesView();
            this.startPolling();
        },
        cleanUpDomElements: function () {
            jQuery("#board").empty();
            jQuery("#playerTilesList").empty();
        },
        close: function () {
            this.cleanUpDomElements();
            this.disableButtons();
            if (this.pollingInterval) {
                clearInterval(this.pollingInterval);
            }
        }
    };

    return MainGame;
});