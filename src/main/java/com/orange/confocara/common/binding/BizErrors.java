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

package com.orange.confocara.common.binding;

import com.orange.confocara.common.binding.BizException.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.ObjectError;

/**
 * Storage for data validation errors
 */
public class BizErrors {

    /**
     * list of rejection reasons
     */
    private List<ObjectError> objectErrors = new ArrayList<>();

    public void reject(ErrorCode errorCode) {
        reject(errorCode, "");
    }

    public void reject(ErrorCode errorCode, String defaultMessage) {
        objectErrors.add(new ObjectError(errorCode.name(), defaultMessage));
    }

    public boolean hasErrors() {
        return !objectErrors.isEmpty();
    }
}
