// Filename: PageErrorAlert.js
define([
    'jquery'
], function (jQuery) {
    function PageErrorAlert() {
    }

    PageErrorAlert.prototype = {
        show: function (errorMessage) {
            jQuery("#pageError").empty().append('<div id="pageError" class="alert alert-danger alert-dismissible fade in" role="alert">' +
                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                    '<span aria-hidden="true">×</span>' +
                    '</button>' +
                    '<h4>Aw Snap! You got an error:</h4>' +
                    '<p id="pageErrorMessage">' + errorMessage + '</p>' +
                    '</div>');
        }
    };

    return PageErrorAlert;
});