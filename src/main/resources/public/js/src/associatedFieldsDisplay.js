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

function displayAssociations(divName, resultSpan, checkboxIds) {
  var selectedDiv = document.getElementById(divName);
  var checkedBoxes = document.querySelectorAll('input[name='+checkboxIds+']:checked');
  var checkSpan =  $('#' + resultSpan);
  checkSpan.html("");

  if (checkedBoxes.length > 0) {
    selectedDiv.style.display = "";
    var list = checkSpan.append('<ul></ul>').find('ul');

    var checkboxesChecked = [];
    for (var i = 0; i < checkedBoxes.length; i++) {
      checkboxesChecked.push(getLabel(checkedBoxes[i].id).trim());
      list.append('<li>' + checkboxesChecked[i] + '</li>');
    }

  } else {
    selectedDiv.style.display = "none";
  }
}
function checkIfRulesetFormChanged() {
    document.getElementById("submitRulesetButton").disabled = false;
}