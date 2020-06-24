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

package com.orange.confocara.presentation.view.qo.list;

import static org.apache.commons.lang3.RandomUtils.*;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.QuestionnaireIdDto;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * see {@link InactiveQuestionnaireConsumer#accept(QuestionnaireDtoWrapper)}
 */
public class InactiveQuestionnaireConsumerAcceptTest {

    private InactiveQuestionnaireConsumer subject;

    private QuestionnaireDtoRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireDtoRepository.class);

        subject = new InactiveQuestionnaireConsumer(repository);
    }

    @Test
    public void shouldApplyStateChangeWhenInputIsInInactiveList() {
        // Given
        long questionnaireId = nextLong();
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getDtoId()).thenReturn(questionnaireId);

        QuestionnaireIdDto mock = mock(QuestionnaireIdDto.class);
        when(mock.getId()).thenReturn(questionnaireId);
        when(repository.findAllInactiveQuestionnaire()).thenReturn(newArrayList(mock));

        // When
        subject.accept(input);

        // Then
        verify(input).setPublishingState(PublishingState.NOT_PUBLISHABLE);
        verify(input).setState(State.INACTIVE);
    }

    @Test
    public void shouldNotApplyStateChangeWhenInputIsNotInInactiveList() {
        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getDtoId()).thenReturn(nextLong());
        when(repository.findAllInactiveQuestionnaire()).thenReturn(newArrayList(mock(
                QuestionnaireIdDto.class)));

        // When
        subject.accept(input);

        // Then
        verify(input, never()).setPublishingState(Matchers.any());
        verify(input, never()).setState(Matchers.any());
    }
}