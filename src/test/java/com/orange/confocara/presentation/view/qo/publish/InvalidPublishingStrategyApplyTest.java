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

import static org.apache.commons.lang3.RandomUtils.*;
import static org.mockito.Mockito.mock;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

/**
 * @see InvalidPublishingStrategy#apply(Long)
 */
public class InvalidPublishingStrategyApplyTest {

    private InvalidPublishingStrategy subject;

    @Test(expected = BizException.class)
    public void shouldThrowException() {

        // Given
        subject = new InvalidPublishingStrategy();

        // When
        subject.apply(nextLong());

        // Then
    }
}