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

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * see {@link AlmostPublishableQuestionnaireConsumer#accept(QuestionnaireDtoWrapper)}
 */
public class AlmostPublishableQuestionnaireConsumerAcceptTest {

    private AlmostPublishableQuestionnaireConsumer subject;

    private QuestionnaireObjectRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireObjectRepository.class);

        subject = new AlmostPublishableQuestionnaireConsumer(repository);
    }

    @Test
    public void shouldNotApplyStatusWhenQuestionnaireHasNoSubObjects() {

        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getSubObjectsNb()).thenReturn(0L);

        // When
        subject.accept(input);

        // Then
        verify(input, never()).setState(Matchers.any());
        verify(input, never()).setPublishingState(Matchers.any());
    }


    @Test
    public void shouldNotApplyStatusWhenQuestionnaireHasOnlyOneSubObject() {

        // Given
        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(mock.getEquipment()).thenReturn(equipment);
        when(repository.findOne(anyLong())).thenReturn(mock);

        when(repository.findByEquipmentName(anyString())).thenReturn(newArrayList(mock(QuestionnaireObject.class)));

        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getSubObjectsNb()).thenReturn(nextLong(1, 10));

        // When
        subject.accept(input);

        // Then
        verify(input, never()).setState(Matchers.any());
        verify(input, never()).setPublishingState(Matchers.any());
    }

    @Test
    public void shouldApplyStatusWhenQuestionnaireHasMoreThanOneSubObject() {

        // Given
        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(mock.getEquipment()).thenReturn(equipment);
        when(repository.findOne(anyLong())).thenReturn(mock);
        when(repository.findByEquipmentName(anyString())).thenReturn(
                newArrayList(mock(QuestionnaireObject.class), mock(QuestionnaireObject.class)));

        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getSubObjectsNb()).thenReturn(nextLong(1, 10));

        // When
        subject.accept(input);

        // Then
        verify(input).setState(State.ACTIVE);
        verify(input).setPublishingState(PublishingState.ALMOST_PUBLISHABLE);
    }
}