require.config({
    shim: {
        bootstrap: {deps: ['jquery']}
    },
    paths: {
        jquery: 'libs/jquery',
        bootstrap: 'libs/bootstrap'
    }

});

require([
    // Load our app module and pass it to our definition function
    'app',
], function (App) {
    // The "app" dependency is passed in as "App"
    App.initialize();
});