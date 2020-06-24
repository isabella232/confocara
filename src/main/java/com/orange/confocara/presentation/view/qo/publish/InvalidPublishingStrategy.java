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

package com.orange.confocara.presentation.view.qo.publish;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link PublishingStrategy} that always raises an exception
 */
@Slf4j
@RequiredArgsConstructor
public class InvalidPublishingStrategy implements PublishingStrategy {

    /**
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     * @return always true
     */
    @Override
    public boolean check(Long id) {
        return true;
    }

    /**
     * Throws a {@link BizException}
     *
     * @param id an identifier for a  {@link QuestionnaireObject}
     */
    @Override
    public void apply(Long id) {

        log.info("Message=Applying invalid strategy...;QuestionnaireId={};", id);

        throw new BizException(ErrorCode.INVALID);
    }
}
