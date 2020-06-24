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
 * Wrapper for JavaScript logging
 *
 * This tool allows developers to use $ for accessing jQuery inside of code
 * and helps to hide some internal methods and properties because of closure.
 *
 * The logging plug-in can be used this way :
 *  $.log('Happy Guacamole !!');
 *  $.info('Happy Guacamole !!');
 */
(function($) {
  $.logger =
      {
        log: function(message) {
          //if('console' in window && 'log' in window.console)
          if (typeof window.console != 'undefined' && typeof window.console.log != 'undefined') {
            console.log(message);
          } else {
            // do nothing
            //alert('console is not supported: ' + message);
          }
        },
        info : function(message) {
          if (typeof window.console != 'undefined' && typeof window.console.log != 'undefined') {
            console.info(message);
          }
        }
      }
})(jQuery);