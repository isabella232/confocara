/*
 * Software Name: ConfOCARA
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2020 Orange
 * SPDX-License-Identifier: MPL-2.0
 *
 * This software is distributed under the Mozilla Public License v. 2.0,
 * the text of which is available at http://mozilla.org/MPL/2.0/ or
 * see the "license.txt" file for more details.
 *
 */

function showSpinnerIfRequiredFieldsAreNotEmpty() {
    var canBeValidated = true;
    var requiredInputs = $(':input[required]');
    for (var i=0; i < requiredInputs.length; i++) {
        if (requiredInputs[i].value === '') {
            canBeValidated = false;
            break;
        }
    }

    if (canBeValidated) {
        showSpinner();
    }
}

function showSpinner() {
    var target = document.getElementById('spinner');
    var opts = {
        lines: 10, // The number of lines to draw
        length: 15, // The length of each line
        width: 10, // The line thickness
        radius: 30, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#000', // #rgb or #rrggbb
        opacity: 0.15, // Opacity of the lines
        speed: 0.6, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: 'auto' // Left position relative to parent in px
    };

    var spinner = new Spinner(opts);
    $("body").addClass("loading");
    spinner.spin(target);
}

function showSpinnerWithId(id) {
    var target = document.getElementById(id);
    var opts = {
        lines: 10, // The number of lines to draw
        length: 3, // The length of each line
        width: 4, // The line thickness
        radius: 10, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#000', // #rgb or #rrggbb
        opacity: 0.15, // Opacity of the lines
        speed: 0.6, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: '50%' // Left position relative to parent in px
    };

    var spinner = new Spinner(opts);
    $(target).data('spinner', spinner);
    spinner.spin(target);
}

function stopSpinner(id) {
    var target = document.getElementById(id);
    $(target).data('spinner').stop();
}

function confirmAndShowSpinner(messageDivId) {
    var message = document.getElementById(messageDivId).textContent;
    var accept = confirm(message);

    if (accept) {
        showSpinner();
    }

    return accept;
}

function getLabel(id) {
    return $("label[for=" + id + "]").html();
}

function cleanInputFile(id) {
    document.getElementById(id).value = "";
}

function checkImageSize(target) {
    document.getElementById("iconErrorDiv").style.display = 'none';
    document.getElementById("submitObjButton").disabled = false;

    if (target.files[0] != null && target.files[0].size >= 1048576) {
        document.getElementById("iconErrorDiv").style.display = 'block';
        document.getElementById("submitObjButton").disabled = true;
        return false;
    }

    return true;
}

$(document).ready(function () {
    var path = location.pathname.substring(1);

    // if (path.indexOf("rules-categories") !== -1) {
    //     $('#menuRulesCategories').addClass('active');
    // } else if (path.indexOf("subjects") !== -1) {
    //     $('#menuSubjects').addClass('active');
    // } else if (path.indexOf("impacts") !== -1) {
    //     $('#menuImpacts').addClass('active');
    // } else if (path.indexOf("profile-types") !== -1) {
    //     $('#menuProfileTypes').addClass('active');
    // } else if (path.indexOf("categories") !== -1) {
    //     $('#menuCategories').addClass('active');
    // } else if (path.indexOf("accounts") !== -1) {
    //     $('#menuAccounts').addClass('active');
    // } else if (path.indexOf("illustrations") !== -1) {
    //     $('#menuIllustrations').addClass('active');
    // } else if (path.indexOf("ruleset") !== -1) {
    //     $('#menuRulesets').find("a").addClass('svg-arrow-next');
    // } else if (path.indexOf("objects") !== -1) {
    //     $('#menuEquipments').addClass('active');
    // } else if (path.indexOf("questions") !== -1) {
    //     $('#menuQuestions').addClass('active');
    // } else if (path.indexOf("chains") !== -1) {
    //     $('#menuChains').addClass('active');
    // } else if (path.indexOf("questionnaires") !== -1) {
    //     $('#menuQuestionnaires').addClass('active');
    // } else if (path.indexOf("rules") !== -1) {
    //     $('#menuRules').addClass('active');
    // } else if (path.indexOf("users") !== -1) {
    //     $('#menuUsers').addClass('active');
    // }
});

function changeModalValidationLabel(targetId, valueId) {
    var target = document.getElementById(targetId);
    var value = document.getElementById(valueId);

    target.innerHTML = value.innerHTML;
}


/*
containsIgnoreCase jquery
 */

$.extend($.expr[':'], {
    'containsIgnoreCase': function(elem, i, match, array)
    {
        return (elem.textContent || elem.innerText || '').toLowerCase()
                .indexOf((match[3] || "").toLowerCase()) >= 0;
    }
});