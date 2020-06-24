
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
 * Helper class that transforms items into list elements
 */
class ItemsListAdapter {

  constructor(_list, _removeCallback) {
    this.list = [];
    this.views = [];

    for (let [key, item] of Object.entries(_list)) {

      this.list.push(item);
      this.views.push(new ItemView(item, _removeCallback));
    }
  }

  /**
   * @return the count of items that are managed by the adapter
   */
  getItemCount() {

    return this.list.length;
  }

  /**
   * @return the item that matches the given argument
   */
  getItem(position) {

    return this.list[position];
  }

  /**
   * @return a jQuery object
   */
  getHtml() {
    let body = $('<div />');

    for (let [key, view] of Object.entries(this.views)) {

      /**
       * @see comment in ItemsListView#onSortUpdated about how these 2 functions
       * are related, in order to present and register items in a correct way.
       */
      body.prepend(view.getHtml());
    }
    return body.contents();
  }
}