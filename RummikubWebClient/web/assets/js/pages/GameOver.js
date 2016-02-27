// Filename: GameOver.js
define([
    'jquery',
    'utils/PageErrorAlert'
], function (jQuery, PageErrorAlert) {
    function GameOver(winnerPlayerName) {
        this.winnerPlayerName = winnerPlayerName;
    }

    GameOver.prototype = {
        switchBackground: function () {
            jQuery("body").removeClass("mainGameBackground").addClass("waitingGamesBackground");
        },
        setPageContent: function () {
            var headline = jQuery("#gameOver h1");
            if (this.winnerPlayerName) {
                headline.text("Game Winner! The winner is: " + this.winnerPlayerName);
            } else {
                headline.text("Game Over!");
            }
        },
        initialize: function () {
            this.switchBackground();
            this.setPageContent();
        },
        close: function () {
        }
    };

    return GameOver;
});