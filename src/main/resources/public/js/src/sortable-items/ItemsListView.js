
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
 * class for a View that displays a bunch of rules
 */
class ItemsListView {

  /**
   * instantiates
   *
   * @param _contentId an identifier of the element that displays the list of rules
   * @param _resultId an identifier of the element that wills store the result of the sorting
   * @param _title a string that is displayed before the content
   * @param _allItems all the available rules
   */
  constructor(_contentId, _resultId, _title, _allItems) {
    this.id = _contentId;
    this.resultId = _resultId;

    // Validating inputs...
    let errors = [];
    let content = $('#' + _contentId);
    if (content.length === 0) {
      // it doesn't exist
      errors.push("The content element with id '" + _contentId + "' does not exist.");
    }

    let result = $('#' + _resultId);
    if (result.length === 0) {
      // it doesn't exist
      errors.push("The result element with id '" + _resultId + "' does not exist.");
    }

    if (errors.length > 0) {
      console.error(errors);
      throw new Error("Could not create list...");
    }

    content
    .html("<strong>" + _title + "</strong>\n"
        + "<div class=\"row\">\n"
        + "<div class=\"col-sm-12\" id=\"selected_rules_list\"></div>\n"
        + "</div>");

    content.hide();

    this.actionsListener = new ItemsListPresenter(this, new ItemRepository(_allItems));

    let list = content
    .find('#selected_rules_list')
    .append('<div id="editable" class="list-group"></div>')
    .find('div');

    /**
     * @link http://api.jqueryui.com/sortable/
     */
    list.sortable({

      /**
       * @link http://api.jqueryui.com/sortable/#event-update
       */
      update: this.onSortUpdated.bind(this)
    });
    list.disableSelection();

    this.html = content;

    this.initialOrderedRuleIds = [];

    this.currentOrderedRuleIds = [];
  }

  /**
   * initializes the list with a bunch of items that are given in a specfici order
   *
   * @param _orderedItemIds an initial list of ordered identifiers for rules
   */
  initView(_orderedItemIds) {
    console.info("Message=View is being initialized;ActualRules=[" + _orderedItemIds.join() + "]");

    this.initialOrderedRuleIds = _orderedItemIds;
    this.currentOrderedRuleIds = _orderedItemIds;
    this.actionsListener.loadItems(_orderedItemIds);
  }

  /**
   * This event is triggered when the user stopped sorting and the DOM position has changed.
   *
   * @param event
   *             @link http://api.jquery.com/Types/#Event
   * @param ui
   *
   * @private
   */
  onSortUpdated(event, ui) {
    let sortedElements = $('#editable').find('a.list-group-item');
    let ids = [];
    for (let i = 0; i < sortedElements.length; i++) {

      /**
       * Surprise ! Here, we unshift the items (instead of siply push them),
       * because there are sorted in the reverse way.
       *
       * @see the function ItemsListAdapter#getHtml() where all the ItemViews are
       * prepended (instead of appended).
       *
       * It would be nice if these 2 functions were not related one to the other.
       *
       */
      ids.unshift(sortedElements[i].id);
    }
    this.actionsListener.onChangeItemsOrder(ids);
  }

  /**
   *
   * @param list some items'identifiers
   */
  showItemsOrderChanged(list) {

    this.currentOrderedRuleIds = list;

    const message = list.join();
    console.info("Message=Items'order has changed;NewOrder=" + message);

    // we store the information of change in an element of the list
    $('#' + this.resultId).val(message);
  }

  /**
   *
   */
  showNoItems() {
    console.info("Message=There are no items to display");
    this
    .html
    .find("#editable")
    .html("");

    this.html.hide();
  }

  showItemRemoved(_removedItem) {

    this.currentOrderedRuleIds.splice($.inArray(_removedItem.reference, this.currentOrderedRuleIds),1);

    this.actionsListener.loadItems(this.currentOrderedRuleIds);
    this.actionsListener.onChangeItemsOrder(this.currentOrderedRuleIds);
  }

  /**
   *
   * @param content some HTML text to display
   */
  showItems(content) {

    this
    .html
    .find("#editable")
    .html(content);

    this.html.show();
  }

  /**
   * Reloads the items, considering a changing in their order.
   *
   * @param newItems an array that contains the ids of the items to display
   */
  updateView(newItems) {

    // 1- retrieving initial items and new ones
    const initialRulesIds = this.currentOrderedRuleIds;
    let oldRuleIds = [];
    let newRuleIds = [];

    for (let i = 0; i < newItems.length; i++) {
      const item = newItems[i];
      const index = $.inArray(item, initialRulesIds);
      if (index > -1) {
        oldRuleIds.splice(index, 0, item);
      } else {
        newRuleIds.unshift(item);
      }
    }

    // 2- merging initial items with new ones
    let actualRuleIds = oldRuleIds.concat(newRuleIds);
    // console.info("OldRules=" + oldRuleIds);
    // console.info("NewRules=" + newRuleIds);
    console.info(
        "Message=Updating the list;PreviousValues=" +
        (initialRulesIds.length > 0 ? initialRulesIds : 'none') +
        ";PreviousValues=" + $('#' + this.resultId).val() +
        ";NewValues=" + actualRuleIds.join());

    // 3- loading the items
    this.actionsListener.loadItems(actualRuleIds);
    this.actionsListener.onChangeItemsOrder(actualRuleIds);
  }

  /**
   * Renders the content of the list
   *
   * @return {*|jQuery|HTMLElement}
   */
  getHtml() {

    return this.html;
  }
}
