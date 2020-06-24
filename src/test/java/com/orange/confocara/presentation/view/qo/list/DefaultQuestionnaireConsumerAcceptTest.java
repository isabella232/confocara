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

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.mockito.Mockito.*;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * see {@link DefaultQuestionnaireConsumer#accept(QuestionnaireDtoWrapper)}
 */
public class DefaultQuestionnaireConsumerAcceptTest {

    private DefaultQuestionnaireConsumer subject = new DefaultQuestionnaireConsumer();

    @Test
    public void shouldApplyNoStateWhenAlreadyDefined() {
        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getState()).thenReturn(randomAlphanumeric(10));

        // When
        subject.accept(input);

        // Then
        verify(input, never()).setState(Matchers.any(State.class));
    }

    @Test
    public void shouldApplyNoPublishingStateWhenAlreadyDefined() {
        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getPublishingState()).thenReturn(randomAlphanumeric(10));

        // When
        subject.accept(input);

        // Then
        verify(input, never()).setPublishingState(Matchers.any(PublishingState.class));
    }

    @Test
    public void shouldApplyActiveStateWhenNotDefined() {
        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getState()).thenReturn(null);

        // When
        subject.accept(input);

        // Then
        verify(input).setState(State.ACTIVE);
    }

    @Test
    public void shouldApplyPublishedStateWhenNotDefined() {
        // Given
        QuestionnaireDtoWrapper input = mock(QuestionnaireDtoWrapper.class);
        when(input.getPublishingState()).thenReturn(null);

        // When
        subject.accept(input);

        // Then
        verify(input).setPublishingState(PublishingState.PUBLISHABLE);
    }
}