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

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import com.orange.confocara.presentation.view.qo.publish.QuestionnaireObjectPublishingService.QuestionnaireObjectPublishingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link QuestionnaireObjectPublishingServiceImpl#publishOneQuestionnaire(Long)}
 */
public class QuestionnaireObjectPublishingServicePublishOneQuestionnaireTest {

    private QuestionnaireObjectPublishingServiceImpl subject;

    private QuestionnaireObjectReadRepository repository;

    private QuestionnaireObjectPublisher publisher;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectReadRepository.class);
        publisher = mock(QuestionnaireObjectPublisher.class);

        subject = new QuestionnaireObjectPublishingServiceImpl(repository, publisher);
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenQuestionnaireIdDoesNotExistInRepository() {

        // Given
        Long questionnaireId = nextLong();
        when(repository.exists(Mockito.anyLong())).thenReturn(false);

        // When
        subject.publishOneQuestionnaire(questionnaireId);

        // Then
    }

    @Test
    public void shouldApplyPublishingWhenQuestionnaireIdExistsInRepository() {

        // Given
        Long questionnaireId = nextLong();
        when(repository.exists(Mockito.anyLong())).thenReturn(true);

        // When
        subject.publishOneQuestionnaire(questionnaireId);

        // Then
        verify(publisher).publishQuestionnaire(questionnaireId);
        verify(publisher).draftQuestionnaire(questionnaireId);
    }



}