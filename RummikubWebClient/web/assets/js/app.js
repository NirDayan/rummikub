// Filename: app.js
define([
    'jquery',
    'bootstrap',
    'pages/waitingGames'
], function (jQuery, bootstrap, WaitingGames) {

    function application() {        
        this.currentPage = null;
        this.pagesLoadMap = {
            '#waitingGames': function () {
                var waitingGamesPage = new WaitingGames();
                waitingGamesPage.initialize();
                this.currentPage = waitingGamesPage;
            }.bind(this),
            '#newGame': function () {
            }.bind(this),
            '#mainGame': function () {
            }.bind(this),
            '#gameOver': function () {
            }.bind(this),
            '#error': function () {
            }.bind(this)
        };        
    }

    application.prototype = {
        render: function (url) {
            // Get the keyword from the url.
            var temp = url.split('/')[0];
            // Hide whatever page is currently shown.
            jQuery('.main-content .page').removeClass('visible');

            // Execute the current page close function
            if (this.currentPage != null) {
                this.currentPage.close();
            }
            // Execute the needed function depending on the url keyword (stored in temp).
            if (this.pagesLoadMap[temp]) {
                this.pagesLoadMap[temp]();
            }
            // If the keyword isn't listed in the above - render the error page.
            else {
                this.renderErrorPage("Wrong URL");
            }
        },
        
        renderErrorPage: function (errorMessage) {
            jQuery('.error').addClass('visible');
        },
        
        initialize: function () {
            // An event handler with calls the render function on every hashchange.
            // The render function will show the appropriate content of out page.
            jQuery(window).on('hashchange', function () {
                this.render(decodeURI(window.location.hash));
            }.bind(this));

            // Manually trigger a hashchange to start the app.
            if (decodeURI(window.location.hash).split("/")[0] === "#waitingGames") {
                jQuery(window).trigger('hashchange');
            } else {
                window.location.hash = "waitingGames";
            }
        }
    };

    return application;
});