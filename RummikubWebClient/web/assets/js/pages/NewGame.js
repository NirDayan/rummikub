// Filename: NewGame.js
define([
    'jquery',
    'utils/PageErrorAlert'
], function (jQuery, PageErrorAlert) {
    function NewGame() {
        this.pollingInterval = null;
        this.currentWaitingGames = null;
        this.selectedTableRow = null;
    }

    NewGame.prototype = {
        initialize: function () {
        },
        close: function () {
        }
    };

    return NewGame;
});