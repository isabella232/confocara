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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Toolbox for errors checking
 */
@FunctionalInterface
public interface ErrorUtil {

    void checkErrors(BizErrors errors);

    static ErrorUtil instance() {
        return new ErrorUtilImpl();
    }

    /**
     * Default implementation of {@link ErrorUtil}
     */
    @Slf4j
    @Service
    final class ErrorUtilImpl implements ErrorUtil {

        @Override
        public void checkErrors(BizErrors errors) {
            if (errors.hasErrors()) {
                log.error("Found errors while validating request : " + errors.toString());
                throw new BizException(ErrorCode.INVALID);
            }
        }
    }
}
