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

$(document).ready(function () {
    showOrHideClearFilterIcon("title", "clearFilter1");

    var secondFilter = document.getElementById("clearFilter2");
    if (secondFilter != null) {
        showOrHideClearFilterIcon("title2", "clearFilter2");
    }
});

function showOrHideClearFilterIcon(title, icon) {
    $('#'+title).keyup(function(){
        $('#'+icon).toggle(Boolean($(this).val()));
    });
    $('#'+icon).toggle(Boolean($('#'+title).val()));
    $('#'+icon).click(function(){
        $('#'+title).val('').focus();
        $(this).hide();
    });
}

/**
 * Used on list pages with only 1 filter item
 *
 * @param e the event
 * @param id the label id
 * @param columnNumber the label column number
 * @param tableId the id of the table
 * @param cookieName the cookie name to set label value inside
 * @returns {boolean} false
 */
function filterValuesAndSetCookie(e, id, columnNumber, tableId, cookieName) {
    //label is the filter value
    if (e == null || e.keyCode == 13) {
        var label = document.getElementById(id);

        $('#' + tableId + ' tbody tr td:nth-child(' + (columnNumber + 1) + '):not(:containsIgnoreCase("' + label.value + '"))').closest('tr').hide();
        $('#' + tableId + ' tbody tr td:nth-child(' + (columnNumber + 1) + '):containsIgnoreCase("' + label.value + '")').closest('tr').show();

        if (cookieName != null) {
            setCookie(cookieName, label.value, 30);
        }
        return false;
    }
}

/**
 * Used on list pages with only 1 filter item
 *
 * @param id the label id
 * @param columnNumber the label column number
 * @param tableId the id of the table
 * @returns {boolean} false
 */
function filterValues(id, columnNumber, tableId) {
        //label is the filter value
        var label = document.getElementById(id);

        $("#"+tableId+" tbody tr td:nth-child("+ (columnNumber+1) +"):not(:containsIgnoreCase('" + label.value + "'))").closest('tr').hide();
        $("#"+tableId+" tbody tr td:nth-child("+ (columnNumber+1) +"):containsIgnoreCase('" + label.value + "')").closest('tr').show();

        return false;
}

/**
 * Used on list pages with only 1 filter item when enter is pressed
 *
 * @param e the event
 * @param id the label id
 * @param columnNumber the label column number
 * @param tableId the id of the table
 * @returns {boolean} false
 */
function filterValuesOnEnter(e, id, columnNumber, tableId) {
    if (e.keyCode == 13) {
        return filterValues(id, columnNumber, tableId);
    }
}

/**
 * Reset cookie with cookie name
 * @param cookieName the name of the cookie to reset
 * @returns {boolean} false
 */
function resetFilterAndCookie(cookieName) {
    setCookie(cookieName, '', -1);
    return false;
}

function filterRulesetsTableByRulesCategory(languageId, languageColumnNumber,
                                            tableId, selectRulesCategoryId, rulesCategoryColumnNumber, languageCookieName,
                                            selectPublishedId, publishedColumnNumber, publishedCookie) {
    var languageSpinner = document.getElementById(languageId);
    var languageValue = languageSpinner.options[languageSpinner.selectedIndex].value;

    var publishedSpinner = document.getElementById(selectPublishedId);
    var publishedValue = publishedSpinner.options[publishedSpinner.selectedIndex].text;

    var selectRulesCategory = document.getElementById(selectRulesCategoryId);
    var selectedRulesCategoryName = selectRulesCategory.options[selectRulesCategory.selectedIndex].text;

    if (publishedSpinner.selectedIndex == 0) {
        publishedValue = '';
    }

    if (selectRulesCategory.selectedIndex == 0) {
        selectedRulesCategoryName = '';
    }

    var table = $('#'+tableId);

    if (languageCookieName != null) {
        setCookie(languageCookieName, languageValue, 30);
        setCookie('rulesCategoryPropertyFilter', selectRulesCategory.options[selectRulesCategory.selectedIndex].value, 30);
    }

    if (publishedCookie != null) {
        setCookie(publishedCookie, publishedSpinner.options[publishedSpinner.selectedIndex].value, 30);
    }

    return filterTableWithFilterByRulesCategory(null, languageValue, languageColumnNumber, table, selectedRulesCategoryName, rulesCategoryColumnNumber, publishedValue, publishedColumnNumber);
}

/**
 *
 * @param allFilter : =true if "all value" is at the first position of rulescategory spinner
 * @param e the event
 * @param id the label id
 * @param columnNumber the label column number
 * @param tableId the id of the table
 * @param selectRulesCategoryId the rulescategory id
 * @param rulesCategoryColumnNumber the rulescategory column number
 * @param cookieName the cookie name to set label value inside
 * @param thirdFilterId the third filter id (optional)
 * @param thirdFilterColumn the third filter column (optional)
 * @returns {boolean} false
 */
function processFilterTableWithFiltersByRulesCategory(allFilter, e, id, columnNumber, tableId, selectRulesCategoryId, rulesCategoryColumnNumber, cookieName, thirdFilterId, thirdFilterColumn) {
    var label = document.getElementById(id);

    var selectRulesCategoryElement = document.getElementById(selectRulesCategoryId);
    var selectRulesCategory = $('#'+selectRulesCategoryId);
    var selectedRulesCategoryName = '';

    if (selectRulesCategory.is('select')) {
        selectedRulesCategoryName = selectRulesCategoryElement.options[selectRulesCategoryElement.selectedIndex].text;

        if (selectRulesCategoryElement.selectedIndex == 0 && allFilter) {
            selectedRulesCategoryName = '';
        }

        if (cookieName != null) {
            setCookie('rulesCategoryPropertyFilter', selectRulesCategoryElement.options[selectRulesCategoryElement.selectedIndex].value, 30);
        }
    } else {
        selectedRulesCategoryName = selectRulesCategory.val();
    }

    var table = $('#'+tableId);

    if (cookieName != null) {
        setCookie(cookieName, label.value, 30);
    }

    var thirdFilterValue = null;

    if (thirdFilterId != null) {
        var thirdFilterElement = document.getElementById(thirdFilterId);
        var thirdFilter = $('#'+thirdFilterId);

        if (thirdFilter.is('select')) {
            thirdFilterValue = thirdFilterElement.options[thirdFilterElement.selectedIndex].text;
            if (thirdFilterElement.selectedIndex == 0) {
                thirdFilterValue = '';
            }
        } else {
            thirdFilterValue = thirdFilter.val();
        }
    }

    return filterTableWithFilterByRulesCategory(e, label.value, columnNumber, table, selectedRulesCategoryName, rulesCategoryColumnNumber, thirdFilterValue, thirdFilterColumn);
}

/**
 * Filters the table with rulescategory and then with label
 *
 * @param e the event
 * @param label the label
 * @param columnNumber the label column number
 * @param table the table (html)
 * @param selectedRulesCategoryName selected rules category name
 * @param rulesCategoryColumnNumber rules category column number
 * @param thirdFilterValue the third filter value (optional)
 * @param thirdFilterColumn the third filter column (optional)
 * @returns {boolean} false
 */
function filterTableWithFilterByRulesCategory(e, label, columnNumber, table, selectedRulesCategoryName, rulesCategoryColumnNumber, thirdFilterValue, thirdFilterColumn) {
    if (e == null || e.keyCode == 13) {
        $('tr').show();

        if (selectedRulesCategoryName !== "") {
            table
                .find('td:nth-child(' + (rulesCategoryColumnNumber + 1) + ')')
                .filter(function () {
                    return $(this).text().trim() !== selectedRulesCategoryName;
                })
                .closest('tr')
                .hide();
        }

        // hide rows with other label
        table
            .find('tbody tr td:nth-child('+ (columnNumber+1) +'):not(:containsIgnoreCase("' + label + '"))')
            .closest('tr')
            .hide();

        if ( thirdFilterValue != null && thirdFilterValue !== "" && thirdFilterColumn != null) {
            table
                .find('tbody tr td:nth-child('+ (thirdFilterColumn+1) +'):not(:containsIgnoreCase("' + thirdFilterValue + '"))')
                .closest('tr')
                .hide();
        }

        return false;
    }
}

/**
 * show all rows
 * @param id the id of the filter field (to remove text filter)
 * @returns {boolean} false
 */
function onRemoveFilterButtonClick(id) {
    $('#' + id).val("");
    $('tr').show();
    return false;
}

/**
 * Filter table by given rules category get from <select>
 * @param labelId the label id
 * @param tableId the table id
 * @param selectRulesCategoryId the selected rules category id
 * @param rulesCategoryColumnNumber the selected rules category column number
 * @returns {boolean} false
 */
function filterTableByRulesCategoryOnCreate(labelId, tableId, selectRulesCategoryId, rulesCategoryColumnNumber) {
    var selectRulesCategory = document.getElementById(selectRulesCategoryId);
    var selectedRulesCategoryName = selectRulesCategory.options[selectRulesCategory.selectedIndex].text;
    var table = $('#'+tableId);

    return filterTableByRulesCategory(labelId, table, selectedRulesCategoryName, rulesCategoryColumnNumber);
}

/**
 * filter a table with given rules category
 *
 * @param labelId the label id to reset previous filters
 * @param table the table (html)
 * @param selectedRulesCategoryName the selected rules category name
 * @param rulesCategoryColumnNumber the selected rules category column number
 * @returns {boolean} false
 */
function filterTableByRulesCategory(labelId, table, selectedRulesCategoryName, rulesCategoryColumnNumber) {
    $('#' + labelId).val("");
    $('tr').show();

    table
        .find('tbody tr td:nth-child('+ (rulesCategoryColumnNumber+1) +')')
        .filter(function() {
            return $(this).text().trim() !== selectedRulesCategoryName;
        })
        .closest('tr')
        .hide();

    return false;
}

/**
 * uncheck all checkboxes
 */
function resetCheckbox() {
    $('input:checkbox').prop('checked', false);
}

/**
 * set a cookie
 * @param cname cookie name
 * @param cvalue cookie value
 * @param exdays number of days
 */
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + encodeURIComponent(cvalue) + ";" + expires + ";path=/";
}

/**
 * get a cookie value
 * @param cname cookie name
 */
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return decodeURIComponent(c.substring(name.length, c.length));
        }
    }
    return "";
}