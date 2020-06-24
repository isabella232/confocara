
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
 * repository that stores and retrieves rules
 */
class ItemRepository {

  /**
   *
   * @param _items a bunch of compound objects with at least 2 fields :
   * - reference an identifier
   * - label some text
   */
  constructor(_items) {

    this.data = _items;
  }

  /**
   * Retrieves items that match the given ids, and sort them in the same order
   *
   * @param ids a bunch of identifiers of the rules to retrieve
   * @param _successCallback a callback called when query is successful
   * @param _errorCallback a callback called when query has failed
   */
  findAll(ids, _successCallback, _errorCallback) {

    const results = this
    .data
    .filter(function (element, index) {
      /**
       * Search for a specified value within an array and return its index
       * (or -1 if not found).
       *
       * @link https://api.jquery.com/jQuery.inArray/
       */
      return $.inArray(element.reference, ids) > -1;
    })
    .sort(function(a, b){
      /**
       * Sorting the elements in the same order as inputs
       *
       * @link https://developer.mozilla.org/fr/docs/Web/JavaScript/Reference/Objets_globaux/Array/sort#Syntaxe
       */
      return ids.indexOf(a.reference) < ids.indexOf(b.reference) ? -1 : 1;
    });

    _successCallback(results);
  }
}
