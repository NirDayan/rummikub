// Filename: PageInfoAlert.js
define([
    'jquery'
], function (jQuery) {
    function PageInfoAlert() {
    }

    PageInfoAlert.prototype = {
        show: function (infoMessage) {
            jQuery("#pageInfo").empty().text(infoMessage).fadeIn().delay(2000).fadeOut("slow");;
        }
    };

    return PageInfoAlert;
});