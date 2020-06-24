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

package com.orange.confocara.presentation.view.controller.helper;

public class ProfileTypeDeletionHelper {

    private ProfileTypeDeletionError errorType;
    private String value;

    public ProfileTypeDeletionHelper(ProfileTypeDeletionError errorType, String value) {
        this.errorType = errorType;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ProfileTypeDeletionError getErrorType() {
        return errorType;
    }
}
