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

package com.orange.confocara.presentation.webservice;

public class RestApi {
    public static final String WS_ROOT = "/ws/";
    public static final String WS_ADMIN = WS_ROOT + "admin/";
    public static final String WS_SEC_IMPORT = WS_ROOT + "import/";
    public static final String WS_SEC_GET_USERS = WS_ROOT + "users";
    public static final String WS_ADMIN_USER_CREATE = WS_ADMIN + "user/create";

    private RestApi() {
        throw new IllegalStateException("Utility class");
    }

}
