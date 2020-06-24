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

function checkEquipment() {
    var selectedObjectDiv = document.getElementById("check_obj_div");
    $("#check_obj").html("");

    var checkedBoxes = document.querySelectorAll('input[name=objectId]:checked');

    if (checkedBoxes.length > 0) {
        selectedObjectDiv.style.display = "inline";
        if (document.getElementById("addQuestionnaireErrors") != null) {
            document.getElementById("addQuestionnaireErrors").style.display = "none";
        }
    } else {
        selectedObjectDiv.style.display = "none";
    }

    var checkboxesChecked = [];
    for (var i = 0; i < checkedBoxes.length; i++) {
        checkboxesChecked.push(getLabel(checkedBoxes[i].id));
        $("#check_obj").append(checkboxesChecked[i] + ' ');
    }
}

function checkChain(isInitialization) {
    var selectedChainDiv = document.getElementById("check_chains_div");
    $("#check_chains").html("");

    var checkedBoxes = document.querySelectorAll('input[name=chainIds]:checked');

    if (checkedBoxes.length > 0) {
        selectedChainDiv.style.display = "inline";
        var list = $("#check_chains").append('<div id="editable" class="list-group"></div>').find('div');
        initEditableList('editable', 'orderedChainIds');
    } else {
        selectedChainDiv.style.display = "none";
        getOrder('orderedChainIds');
    }

    var checkboxesChecked = [];
    var questionnaireId;

    if (isInitialization) {
        var sortedChainIds = $("#orderedChainIds").val();
        var chainIdsArray = sortedChainIds.split(',');
        if (chainIdsArray.length > 0 && chainIdsArray[0] != "") {
            for (var i = 0; i < chainIdsArray.length; i++) {
                var checkboxId = 'chain'+chainIdsArray[i];
                checkboxesChecked.push(getLabel(checkboxId).trim());
                questionnaireId = document.getElementById(checkboxId).value;
                list.append('<a class="list-group-item list-group-item-action" data-checkbox-id="'+checkboxId+
                    '"data-sortable-id="'+questionnaireId+'">' +
                    checkboxesChecked[i] + '<i class="js-remove">✖</i></a>');
            }
        }
    } else {
        for (var i = 0; i < checkedBoxes.length; i++) {
            checkboxesChecked.push(getLabel(checkedBoxes[i].id).trim());
            questionnaireId = document.getElementById(checkedBoxes[i].id).value;
            list.append('<a class="list-group-item list-group-item-action" data-checkbox-id="' + checkedBoxes[i].id +
                '"data-sortable-id="' + questionnaireId + '">' +
                checkboxesChecked[i] + '<i class="js-remove">✖</i></a>');
        }
    }

    if (checkedBoxes.length > 0) {
        setOrderedIds("editable", "orderedChainIds");
    }
}
function checkQuestionnairesFormChanged() {
    document.getElementById("submitQuestionnairesButton").disabled = false;
}
function checkQuestionnaireManagement() {
    checkEquipment();
    checkChain(true);
}
