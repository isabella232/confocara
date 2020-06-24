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

/**
 * requires at least a dialog box with the id "myModal", based on Bootstrap
 * modals, that embeds :
 * - a table named "search_table" with at least 3 columns
 * - an input field name "label" which is related to the 3rd column
 * - an input field name "reference" which is related to the 2nd column
 *
 * may also need :
 * - one element named "clear_search_label" which helps on resetting the input "filter_label"
 * - one element named "clear_search_reference" which helps on resetting the input "filter_reference"
 */
class SearchView {

  constructor(_params) {
    this.modal = $('#' + _params.modalId);
    if (this.modal.length  === 0) {
      console.error("No valid modal found for id #" + _params.modalId);
    }
    if (this.modal.find("[name='search_table']").length  === 0) {
      console.error("No valid table found with [name='search_table']");
    }

    this.labelInput = this.modal.find("input[name='filter_label']");
    if (this.labelInput.length  === 0) {
      console.error("No valid input found with [name='filter_label']");
    }

    this.labelResetAction = this.modal.find("span[name='clear_search_label']");
    if (this.labelResetAction.length  === 0) {
      console.error("No valid element found with [name='clear_search_label']");
    }

    this.referenceInput = this.modal.find("input[name='filter_reference']");
    if (this.labelInput.length  === 0) {
      console.error("No valid input found with [name='filter_reference']");
    }

    this.referenceResetAction = this.modal.find("span[name='clear_search_reference']");
    if (this.labelResetAction.length  === 0) {
      console.error("No valid element found with [name='clear_search_reference']");
    }

    this.params = _params;
  }

  initView() {
    this.labelInput
    .keyup(function(){
      let withValue = Boolean(this.labelInput.val());
      this.labelResetAction.toggle(withValue);
      this.applyFilter();
    }.bind(this));

    // configuring the element "clear_search_label"
    this.labelResetAction
    .click(function(){
      this.labelInput.val('').focus();
      this.labelResetAction.toggle(false);
      this.applyFilter();
    }.bind(this))
    .toggle(Boolean(this.labelInput.val()));

    // configuring the element "search_reference"
    this.referenceInput
    .keyup(function(){
      let withValue = Boolean(this.referenceInput.val());
      this.referenceResetAction.toggle(withValue);
      this.applyFilter();
    }.bind(this));

    // configuring the element "clear_search_reference"
    this.referenceResetAction
    .click(function(){
      this.referenceInput.val('').focus();
      this.referenceResetAction.toggle(false);
      this.applyFilter();
    }.bind(this))
    .toggle(Boolean(this.referenceInput.val()));

    // configuring the modal :
    // when showing the modal, the app focuses on the input "search_reference",
    // and then triggers the filter
    this.modal.on('show.bs.modal', function () {
      this.referenceInput.focus();
      this.applyFilter();
    }.bind(this));
  }

  /**
   * Function that aim to hide every row that do not match the reference and the label entered by the user.
   *
   * @private
   */
  applyFilter() {
    let reference = this.modal.find("[name='filter_reference']").val();
    let label = this.modal.find("[name='filter_label']").val();
    let params = this.params;

    this.modal
      .find("[name='search_table']")
      .find('tbody tr')
      // first, we hide all the rows
      .hide()
      //second, we find the rows that do match the reference and the label
      .filter(function() {
        let rowDisplayed = false;

        // by default, every row are displayed (ie not hidden) when both arguments are empty
        if (reference.length === 0 && label.length === 0) {
          rowDisplayed = true;
        } else {
          let referenceValid = true;
          if (reference.length > 0) {
            referenceValid = $(this).find("td:nth-child(" + params.reference_column_index + ")").text().toUpperCase().indexOf(reference.toUpperCase()) >= 0;
          }

          let labelValid = true;
          if (label.length > 0) {
            labelValid = $(this).find("td:nth-child(" + params.label_column_index + ")").text().toUpperCase().indexOf(label.toUpperCase()) >= 0;
          }

          rowDisplayed = referenceValid && labelValid;
        }
        return rowDisplayed;
      })
      // third, we show the rows that were filtered
      .show();
  }

}
