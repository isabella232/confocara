
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
 * class for a View that displays a rule
 */
class ItemView {

  /**
   *
   * @param _item data about the element
   * @param _deleteCallback a callback that handles the removing of the element
   */
  constructor(_item, _deleteCallback) {

    this.id = _item.reference;
    this.html = $('<a class="list-group-item list-group-item-action" '
        + 'data-checkbox-id="' + _item.reference + '" '
        + 'id="' + _item.reference + '" '
        + 'data-sortable-id="' + _item.reference + '">'
        + '<div class="row">'
        + '<span class="col-sm-1">' + _item.reference + '</span>'
        + '<span class="col-sm-10">' + _item.label.replace(/\r\n|\n|\r/g, '<br />') + '</span>'
        + '<span class="col-sm-1"><i class="js-remove">✖</i></span>'
        + '</div></a>');

    /**
     * configuring the deletion event of the item
     *
     * @link https://api.jquery.com/click/
     */
    this.html
    .find(".js-remove")
    .closest('span')
    .click(_item, function(event) {

      $(this).closest('a').remove();

      _deleteCallback(event.data);
    });
  }

  getHtml() {
    return this.html;
  }
}
