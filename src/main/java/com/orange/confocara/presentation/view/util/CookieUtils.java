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

package com.orange.confocara.presentation.view.util;

import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.presentation.view.controller.RuleController;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Classe utilitaire pour externaliser l'ajout de cookies dans une {@link HttpServletResponse}
 *
 */
public class CookieUtils {

    /**
     *
     * @param target a {@link HttpServletResponse} to decorate
     * @param item a {@link RulesCategory} to set as {@link Cookie}
     */
    public static void decorateResponse(HttpServletResponse target, RulesCategory item) {

        if (item != null) {
            Cookie cookie = new Cookie(RuleController.SELECTED_RULES_CATEGORY_COOKIE, String.valueOf(item.getId()));

            // The Secure flag is a directive to the browser to make sure that the cookie is not
            // sent for insecure communication (http://).
            cookie.setSecure(true);

            // The HttpOnly flag is a directive to the browser to make sure that the cookie can not
            // be red by malicious script. When a user is the target of a "Cross-Site Scripting",
            // the attacker would benefit greatly from getting the session id for example.
            cookie.setHttpOnly(true);

            cookie.setPath("/");
            target.addCookie(cookie);
        }
    }
}
