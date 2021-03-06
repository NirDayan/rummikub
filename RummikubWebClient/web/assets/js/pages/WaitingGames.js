// Filename: WaitingGames.js
define([
    'jquery',
    'underscore',
    'utils/PageErrorAlert'
], function (jQuery, _, PageErrorAlert) {
    function WaitingGames() {
        this.pollingInterval = null;
        this.currentWaitingGames = null;
        this.selectedTableRow = null;
    }

    WaitingGames.prototype = {
        updateWaitingGamesTable: function (waitingGames) {
            var tableBody = jQuery("#waitingGamesTable tbody").empty();
            var namesString;
            var namesList;

            this.selectedTableRow = null;
            for (var i = 0; i < waitingGames.length; i++) {
                namesList = waitingGames[i].unjoinedPlayersNames;
                namesString = "";
                for (var j = 0; j < namesList.length; j++) {
                    namesString += namesList[j];
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
            this.updateSelectedTableRow(tableBody);
        },
        updateSelectedTableRow: function (tableBody) {
            var that = this;
            tableBody.find('tr').on("click", function () {
                var clickedRow = jQuery(this);
                if (clickedRow.hasClass("selected")) {
                    clickedRow.removeClass("selected");
                    that.selectedTableRow = null;
                } else {
                    tableBody.find('tr').removeClass("selected");
                    clickedRow.addClass("selected");
                    that.selectedTableRow = clickedRow;
                }
                jQuery("#playerNameInput").trigger("keyup");
            });
        },
        startPolling: function () {
            this.pollingInterval = setInterval(function () {
                var waitingGamesRequest = jQuery.get("./waitingGames");

                waitingGamesRequest.done(function (waitingGames) {
                    if (this.currentWaitingGames && _.isEqual(this.currentWaitingGames, waitingGames)) {
                        //List has not been changed. Do Nothing...
                        return;
                    }
                    this.currentWaitingGames = waitingGames;
                    this.updateWaitingGamesTable(waitingGames);
                }.bind(this));

                waitingGamesRequest.fail(function (errorMessage) {
                    (new PageErrorAlert()).show(errorMessage.responseText);
                });
            }.bind(this), 1000);
        },
        initPlayerNameField: function () {
            var that = this;
            jQuery("#playerNameInput").on("keyup", function () {
                if (jQuery(this).val().trim().length > 2 && that.selectedTableRow !== null) {
                    jQuery("#joinGameButton").attr("disabled", false);
                } else {
                    jQuery("#joinGameButton").attr("disabled", true);
                }
            }).on("keypress", function (e) {
                // If ENTER is pressed and joinGameButton is not disabled -> Join game
                if (e.which === 13 && jQuery("#joinGameButton").attr("disabled") !== "disabled") {
                    that.joinGame();
                }
            }
            );
        },
        initJoinGameButton: function () {
            jQuery("#joinGameButton").on("click", this.joinGame.bind(this));
        },
        joinGame: function () {
            var gameName = this.selectedTableRow.find("td").eq(0).text();
            var humanPlayers = parseInt(this.selectedTableRow.find("td").eq(1).text());
            var computerizedPlayers = parseInt(this.selectedTableRow.find("td").eq(2).text());

            var playerName = jQuery("#playerNameInput").val();
            if (gameName && playerName) {
                jQuery.post("./joinGame", {
                    gameName: gameName,
                    playerName: playerName
                }).done(function () {
                    //Move to the next screen
                    this.currentPlayerName = playerName;
                    this.currentGameName = gameName;
                    this.playersNumber = humanPlayers + computerizedPlayers;
                    jQuery("#playerNameInput").val("");
                    window.location.hash = "mainGame";
                }.bind(this)).fail(function (errorMessage) {
                    (new PageErrorAlert()).show(errorMessage.responseText);
                });
            } else {
                (new PageErrorAlert()).show("Invalid player name or selected game.");
            }
        },
        getGameDetails: function () {
            return {
                gameName: this.currentGameName,
                playerName: this.currentPlayerName,
                playersNumber: this.playersNumber
            };
        },
        initLoadGameFromFileButton: function () {
            jQuery("#loadGameFromFile").on('change', function (evt) {
                var files = evt.target.files; // FileList object
                var reader = new FileReader();
                var input = jQuery(this),
                        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');

                reader.onload = function (evt) {
                    jQuery.post("./loadGame", {
                        fileContent: evt.target.result
                    }).done(function (data) {
                        input.parents('.input-group').find(':text').val(label);
                    }).fail(function (errorMessage) {
                        (new PageErrorAlert()).show(errorMessage.responseText);
                    });
                };
                reader.readAsText(files[0]);
            });
        },
        initNewGameForm: function () {
            jQuery('#inputGameName').on('change', function () {
                var inputText = jQuery(this).val().trim();
                jQuery(this).val(inputText);
            });
            jQuery('#newGameForm').on('submit', function (e) {
                e.preventDefault();                
                var gameName = jQuery('#inputGameName').val().trim();
                var humanPlayersNum = parseInt(jQuery('#humanPlayersNum').val());
                var compPlayersNum = parseInt(jQuery('#compPlayersNum').val());
                var playersSum = humanPlayersNum + compPlayersNum;

                if (gameName.length > 1 && playersSum <= 4 && playersSum >= 2) {
                    jQuery.post("./createGame", {
                        gameName: gameName,
                        humanPlayersNum: humanPlayersNum,
                        compPlayersNum: compPlayersNum                        
                    }).done(function (data) {
                        jQuery('#inputGameName').val("");
                        jQuery('#humanPlayersNum').val("");
                        jQuery('#compPlayersNum').val("");
                    }).fail(function (errorMessage) {
                        (new PageErrorAlert()).show(errorMessage.responseText);
                    });
                } else {
                    (new PageErrorAlert()).show("Invalid form fields");
                }
                //Prevent the submittion from changing the URL
                return false;
            });
        },        
        switchBackground: function () {
            jQuery("body").removeClass("mainGameBackground").addClass("waitingGamesBackground");
        },
        initialize: function () {
            this.startPolling();
            this.initPlayerNameField();
            this.initJoinGameButton();
            this.initLoadGameFromFileButton();
            this.initNewGameForm();
            this.switchBackground();
        },
        close: function () {
            var tableBody = jQuery("#waitingGamesTable tbody");
            tableBody.find('tr').off("click");
            jQuery("#playerNameInput").off("keyup");
            jQuery("#joinGameButton").off("click");
            jQuery("#loadGameFromFile").off('change');
            jQuery('#inputGameName').off('change');
            jQuery('#newGameForm').off('submit');
            if (this.pollingInterval) {
                clearInterval(this.pollingInterval);
            }
        }
    };

    return WaitingGames;
});