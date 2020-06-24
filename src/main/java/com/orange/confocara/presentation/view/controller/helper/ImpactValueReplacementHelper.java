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

public class ImpactValueReplacementHelper {

    private ImpactValueReplacementError errorType;
    private String value;

    public ImpactValueReplacementHelper(ImpactValueReplacementError errorType, String value) {
        this.errorType = errorType;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ImpactValueReplacementError getErrorType() {
        return errorType;
    }
}
