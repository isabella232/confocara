
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
 * Bridge between the View and the Repository, with the help of an adapter
 */
class ItemsListPresenter {

  constructor(_view, _repository) {
    this.view = _view;
    this.repository = _repository;
    this.listAdapter = null;
  }

  /**
   *
   * @param _itemIds a bunch of rule Ids to load from the store
   */
  loadItems(_itemIds) {

    this
    .repository
    .findAll(
        _itemIds,
        this.onListLoadSuccess.bind(this),
        this.onListLoadFailed.bind(this));
  }

  /**
   * @private
   */
  onListLoadSuccess(results) {

    this.listAdapter = new ItemsListAdapter(results, this.onRemovingItem.bind(this));

    console.info("Message=Presenter's loading is successful;AdapterCount="+ this.listAdapter.getItemCount());
    if (this.listAdapter.getItemCount() > 0) {
      this.view.showItems(this.listAdapter.getHtml());
    } else {
      this.view.showNoItems();
    }
  }

  /**
   * @private
   */
  onListLoadFailed() {

    this.view.showNoItems();
  }

  /**
   * @private
   */
  onChangeItemsOrder(ids) {

    // rules identifiers shall be stored in reversed order
    this.view.showItemsOrderChanged(ids);
  }

  /**
   * @private
   */
  onRemovingItem(_item) {

    console.info("removing an item");
    console.info(_item);

    this.view.showItemRemoved(_item);
  }
}
