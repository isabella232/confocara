# Description
 This directory contains classes that help on creating sortables lists of items.
 It is based on the MVP pattern (aka Model-View-Presenter)

 For example, the sortable items can be rules, in questions forms
 (@see editQuestion.html and addQuestion.html).

 These elements are :
 - the main view is named ItemsListView. It is dedicated to displaying the
 list;
 - a view named ItemView for one item only;
 - a repository called ItemRepository, that contains all the available items;
 - a bridge ItemsListPresenter that handles communication between the main
 view and the repository;
 - an adapter ItemsListAdapter for converting items into ItemViews.

 All the elements are store in this unique file. One possible task could be to
 separate them in dedicated files, so as to make the reading and the coding easier.

 # How to use it :  
 1- instantiate with correct info  
 <code>
 const view = new ItemsListView('check_rule_div', 'orderedRuleIds', title, allItems);
 </code>  
 
 where :
   - 'check_rule_div' is the identifier for a DOM element where the view should be displayed
   - 'orderedRuleIds' is the identifier for a DOM element where the list of rules'ids are stored
   - 'title' is the name of the view
   - 'allItems' is an array of items

 2- initialize the view with a list of identifiers for items. It can be empty :  
 <code>
 view.initView(orderedItems);
 </code>

 3- if the list may change, it is possible to trigger the update of its view :  
 <code>
 view.updateView(newOrderedItems);
 </code>


