define([
    'jquery'
], function (jQuery) {
    var initialize = function () {
        
        $("#doneCreate").click(
                define([
                        'pages/WaitingGames'
                ], function (waitingGames) {
                    $(".newGame").hide();
                    $(".waitingGames").show();
                    waitingGames.initialize();
                }));
    };

    return {
        initialize: initialize
    };
});