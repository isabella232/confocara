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

package com.orange.confocara.presentation.webservice.subobject;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link QuestionnaireWithSubObjectsPublisher#publishQuestionnaire(Long)}
 */
public class QuestionnaireWithSubObjectsPublisherPublishQuestionnaireTest {

    private QuestionnaireWithSubObjectsPublisher subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new QuestionnaireWithSubObjectsPublisher(repository);
    }

    @Test
    public void shouldApplyEntity() {

        // Given
        QuestionnaireObject entity = mock(QuestionnaireObject.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(entity);

        // When
        subject.publishQuestionnaire(nextLong());

        // Then
        verify(entity).markAsPublished();
    }
}