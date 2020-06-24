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
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import org.springframework.stereotype.Service;

/**
 * a validator for {@link PublishedRuleset}s
 */
@Service
public class PublishedRulesetValidator implements BizValidator {

    @Override
    public void validate(Object target, BizErrors errors) {
        PublishedRuleset ruleset = (PublishedRuleset) target;

        if (ruleset == null) {
            errors.reject(ErrorCode.NOT_FOUND);
        }
    }
}
