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
            var namesString = "";
            var namesList;

            this.selectedTableRow = null;
            for (var i = 0; i < waitingGames.length; i++) {
                namesList = waitingGames[i].unjoinedPlayersNames;
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
                var waitingGamesRequest = jQuery.get({
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
        initPlayerNameField: function () {
            var that = this;
            jQuery("#playerNameInput").on("keyup", function () {
                if (jQuery(this).val().trim().length > 2 && that.selectedTableRow != null) {
                    jQuery("#joinGameButton").attr("disabled", false);
                } else {
                    jQuery("#joinGameButton").attr("disabled", true);
                }
            });
        },
        initJoinGameButton: function () {
            jQuery("#joinGameButton").on("click", function () {
                var gameName = this.selectedTableRow.find("td:first").text();
                var playerName = jQuery("#playerNameInput").val();
                if (gameName && playerName) {
                    jQuery.post("./joinGame", {
                        gameName: gameName,
                        playerName: playerName
                    }).done(function (data) {
                        return;
                        //Todo: continue...
                    }).fail(function (errorMessage) {
                        new PageErrorAlert().show(errorMessage.responseText);
                    });
                } else {
                    new PageErrorAlert().show("Invalid player name or selected game.");
                }
            }.bind(this));
        },
        initLoadGameFromFileButton: function () {
            jQuery("#loadGameFromFile").on('change', function (evt) {
                var files = evt.target.files; // FileList object
                var reader = new FileReader();
                var input = jQuery(this),
                        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
                
                input.parents('.input-group').find(':text').val(label);
                reader.onload = function(evt) {
                    jQuery.post("./loadGame", {
                        fileContent: evt.target.result
                    }).done(function (data) {
                        return;
                        //Todo: continue...
                    }).fail(function (errorMessage) {
                        new PageErrorAlert().show(errorMessage.responseText);
                    });
                };
                reader.readAsText(files[0]);
            });
        },
        initialize: function () {
            this.startPolling();
            this.initPlayerNameField();
            this.initJoinGameButton();
            this.initLoadGameFromFileButton();
        },
        close: function () {
            if (this.pollingInterval) {
                clearInterval(this.pollingInterval);
            }
        }
    };

    return WaitingGames;
});