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

// Get the sorted NodeList (array) of items
function getOrder(resultInput) {
    var my_srt_items = document.querySelectorAll("[data-sortable-id]");
    var ids = [].map.call(my_srt_items, function (el) {
        return el.dataset.sortableId; // [data-sortable-id]
    });
    $('#'+resultInput).val(ids);
}

function setOrderedIds(listId, resultInput) {
    var sortedList = document.getElementById(listId);
    getOrder(resultInput);
    // Refresh the order everytime the item is dragged & dropped
    sortedList.addEventListener("dragend", function() { getOrder(resultInput); }, false);
}

// group removed
function initEditableList(id, resultInput) {
    var editable  = document.getElementById(id);
    var editableList = Sortable.create(editable, {
        dataIdAttr: 'data-sortable-id',
        filter: '.js-remove',
        onFilter: function (evt) {
            var el = editableList.closest(evt.item); // get dragged item
            var checkboxToUncheckId = el.dataset.checkboxId;
            document.getElementById(checkboxToUncheckId).checked = false;
            el && el.parentNode.removeChild(el);
            getOrder(resultInput);
        }
    });
}