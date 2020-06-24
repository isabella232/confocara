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
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.junit.Before;
import org.junit.Test;

/**
 * see {@link QuestionnaireObjectPublisher#publishQuestionnaire(Long)}
 */
public class QuestionnaireObjectPublisherPublishQuestionnaireTest {

    private QuestionnaireObjectPublisher subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectReadRepository.class);

    }

    @Test
    public void shouldApplyOnentityWhenOneStrategyMatches() {

        // Given
        QuestionnaireObject entity = mock(QuestionnaireObject.class);
        when(repository.findOne(anyLong())).thenReturn(entity);

        PublishingStrategy strategy = mock(PublishingStrategy.class);
        when(strategy.check(any(Long.class))).thenReturn(true);

        subject = new QuestionnaireObjectPublisher(repository, newArrayList(strategy));

        // When
        subject.publishQuestionnaire(nextLong());

        // Then
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenNoStrategyMatches() {

        // Given
        QuestionnaireObject entity = mock(QuestionnaireObject.class);
        when(repository.findOne(anyLong())).thenReturn(entity);

        PublishingStrategy strategy = mock(PublishingStrategy.class);
        when(strategy.check(any(Long.class))).thenReturn(false);

        subject = new QuestionnaireObjectPublisher(repository, newArrayList(strategy));

        // When
        subject.publishQuestionnaire(nextLong());

        // Then
    }
}