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

function checkImpacts() {
    var checkedBoxes = document.querySelectorAll('input[name=acceptedImpactIds]:checked');
    var selectedImpactsDiv = document.getElementById("check_impacts_div");

    $("#check_impacts").html("");

    if (checkedBoxes.length > 0) {
        selectedImpactsDiv.style.visibility = "visible" ;
        var list = $("#check_impacts").append('<ul></ul>').find('ul');
    } else {
        selectedImpactsDiv.style.visibility = "hidden" ;
    }

    var checkboxesChecked = [];
    for (var i = 0; i < checkedBoxes.length; i++) {
        checkboxesChecked.push(getLabel(checkedBoxes[i].id));
        list.append('<li>' + checkboxesChecked[i] + '</li>');
    }

    var selectedDefaultImpactDiv = document.getElementById("check_default_impact_div");
    var checkedRadio = document.querySelectorAll('input[name=defaultImpactId]:checked');
    $("#check_default_impact").html("");

    if (checkedRadio.length > 0) {
        selectedDefaultImpactDiv.style.visibility = "visible" ;
        var defaultValue = getRadioLabel(checkedRadio[0].id);
        $("#check_default_impact").append(defaultValue+" ");
    } else {
        selectedDefaultImpactDiv.style.visibility = "hidden" ;
    }

    return checkboxesChecked;
}

function checkProfileTypes() {
    var checkedBoxes = document.querySelectorAll('input[name=profileTypeIds]:checked');
    var selectedImpactsDiv = document.getElementById("check_profile_types_div");

    $("#check_profile_types").html("");

    if (checkedBoxes.length > 0) {
        selectedImpactsDiv.style.visibility = "visible" ;
        var list = $("#check_profile_types").append('<ul></ul>').find('ul');
    } else {
        selectedImpactsDiv.style.visibility = "hidden" ;
    }

    var checkboxesChecked = [];
    for (var i = 0; i < checkedBoxes.length; i++) {
        checkboxesChecked.push(getLabel(checkedBoxes[i].id));
        list.append('<li>' + checkboxesChecked[i] + '</li>');
    }
}

function getRadioLabel(id) {
    return $("label[for=impact" + id + "]").text();
}

function checkCorrespondingCheckboxWhenDefaultValueIsSelected() {
    $('input[type="radio"]').change(function () {
        if ($(this).is(':checked')) {
            var id = this.id;
            $('#impact' + id).prop('checked', true);
        }
    });
}

function checkDefaultValueCheckbox() {
    var radioInputs = $('input[type="radio"]');
    if(radioInputs.is(':checked')) {
        for (var i = 0; i < radioInputs.length; i++) {
            if (radioInputs[i].checked) {
                $('#impact' + radioInputs[i].id).prop('checked', true);
                break;
            }
        }
    }
}
