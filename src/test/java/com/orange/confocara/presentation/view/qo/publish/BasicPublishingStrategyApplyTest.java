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
import static org.mockito.Mockito.*;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @see BasicPublishingStrategy#apply(Long)
 */
public class BasicPublishingStrategyApplyTest {

    private BasicPublishingStrategy subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new BasicPublishingStrategy(repository);
    }

    @Test
    public void shouldApplyOnGivenQuestionnaireObject() {

        // Given
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        subject.apply(nextLong());

        // Then
        verify(mock).markAsPublished();
    }
}