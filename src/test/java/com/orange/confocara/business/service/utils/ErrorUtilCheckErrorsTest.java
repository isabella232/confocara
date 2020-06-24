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

package com.orange.confocara.business.service.utils;

import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import org.junit.Test;

/**
 * see {@link ErrorUtil#checkErrors(BizErrors)}
 */
public class ErrorUtilCheckErrorsTest {

    private ErrorUtil subject = ErrorUtil.instance();

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenGivenErrorsIsFilled() {
        // Given
        BizErrors errors = new BizErrors();
        errors.reject(ErrorCode.INVALID);

        // When
        subject.checkErrors(errors);

        // Then
    }

    @Test
    public void shouldThrowExceptionWhenGivenErrorsIsEmpty() {
        // Given
        BizErrors errors = new BizErrors();

        // When
        subject.checkErrors(errors);

        // Then
    }
}