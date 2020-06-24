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

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link QuestionnaireObjectPublisher#draftQuestionnaire(Long)}
 */
public class QuestionnaireObjectPublisherDraftQuestionnaireTest {

    private QuestionnaireObjectPublisher subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new QuestionnaireObjectPublisher(repository, Lists.newArrayList());
    }

    @Test
    public void shouldApplyOnEntity() {

        // Given
        QuestionnaireObject entity = mock(QuestionnaireObject.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(entity);

        // When
        subject.draftQuestionnaire(nextLong());

        // Then
        verify(entity).markAsNewDraft();

    }
}