

function updateHeight(elementId, add, substract) {
    updateHeightReal(elementId, add, substract);
    $(window).resize(function() {
        updateHeightReal(elementId, add, substract);
    });
}

function updateHeightReal(elementId, add, substract) {
    $('#' + elementId).css("height","0px");

    var documentHeight = $(document).innerHeight();
    var bodyTopPadding = $(".navbar-fixed-top").outerHeight(false);
    var mainContainerHeight = $('div.mainContainer').outerHeight(true);
    var elementHeight =  $('#' + elementId).outerHeight(true);

    console.log("Document height: " + documentHeight + ", mainContainer: "
        + mainContainerHeight + ", body top-padding: " + bodyTopPadding);

    var height = documentHeight - mainContainerHeight - bodyTopPadding - elementHeight;
    console.log("Height clean: " + height);

    if (substract instanceof Array) {
        for (var i = 0; i < substract.length; i++) {
            console.log("Substract height: " + $(substract[i]).outerHeight(true));
            height -= $(substract[i]).outerHeight(true);
        }
    }
    if (add instanceof Array) {
        for (var i = 0; i < add.length; i++) {
            console.log("Add height: " + $(add[i]).outerHeight(true));
            height += $(add[i]).outerHeight(true);
        }
    }
    console.log("New css height: " + height);
    $('#' + elementId).css("height", height + "px");
}