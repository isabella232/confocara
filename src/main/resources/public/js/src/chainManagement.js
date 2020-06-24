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

function checkQuestions(isInitialization) {
    var selectedQuestionsDiv = document.getElementById("check_questions_div");
    var checkQuestionsDiv = $("#check_questions");

    checkQuestionsDiv.html("");
    var checkedBoxes = document.querySelectorAll('input[name=questionIds]:checked');

    if (checkedBoxes.length > 0) {
        selectedQuestionsDiv.style.display = "inline";
        var list = checkQuestionsDiv.append('<div id="editable" class="list-group"></div>').find('div');
        initEditableList('editable', 'orderedQuestionIds');
    } else {
        selectedQuestionsDiv.style.display = "none";
        getOrder('orderedQuestionIds');
    }

    var checkboxesChecked = [];
    var checkboxesCheckedReferences = [];
    var questionId;

    if (isInitialization) {
        var sortedQuestionIds = $("#orderedQuestionIds").val();
        var questionIdsArray = sortedQuestionIds.split(',');
        if (questionIdsArray.length > 0 && questionIdsArray[0] != "") {
            for (var i = 0; i < questionIdsArray.length; i++) {
                var checkboxId = 'question'+questionIdsArray[i];
                checkboxesChecked.push(getLabel(checkboxId).trim());
                questionId = document.getElementById(checkboxId).value;
                list.append('<a class="list-group-item list-group-item-action" data-checkbox-id="'+checkboxId+
                    '"data-sortable-id="'+questionId+'">' +
                  "Q"+questionIdsArray[i] +' &emsp; '+checkboxesChecked[i] + '<i class="js-remove">✖</i></a>');
            }
        }
    } else {
        for (var i = 0; i < checkedBoxes.length; i++) {
            checkboxesChecked.push(getLabel(checkedBoxes[i].id).trim());
            questionId = document.getElementById(checkedBoxes[i].id).value;
            list.append('<a class="list-group-item list-group-item-action" data-checkbox-id="' + checkedBoxes[i].id +
                '"data-sortable-id="' + questionId + '">' +
                "Q"+questionId +' &emsp; '+checkboxesChecked[i] + '<i class="js-remove">✖</i></a>');
        }
    }

    if (checkedBoxes.length > 0) {
        setOrderedIds("editable", "orderedQuestionIds");
    }
}

function checkChainManagement() {
    checkQuestions(true);
}
// bind the on-change event
$(document).ready(function () {

});
function enableChainChange() {
    document.getElementById("submitChainButton").disabled = false;
}