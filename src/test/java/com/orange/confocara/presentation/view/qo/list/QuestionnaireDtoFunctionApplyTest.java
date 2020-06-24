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
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Lists.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.QuestionnaireQuestionsDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireRulesDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireSubObjectsDto;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

/**
 * see {@link QuestionnaireDtoFunction#apply(QuestionnaireDto)}
 */
public class QuestionnaireDtoFunctionApplyTest {

    private QuestionnaireDtoFunction subject;

    private QuestionnaireDtoRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireDtoRepository.class);

        subject = new QuestionnaireDtoFunction(repository);
    }

    @Test
    public void shouldCreateWrapperWithSubElementsNb() {
        // Given
        long dtoId = nextLong();

        long expectedQuestionsNb = nextLong();
        QuestionnaireQuestionsDto questions = mock(QuestionnaireQuestionsDto.class);
        when(questions.getId()).thenReturn(dtoId);
        when(questions.getQuestionsNb()).thenReturn(expectedQuestionsNb);
        when(repository.findAllQuestionnaireQuestionDto()).thenReturn(newArrayList(questions));

        long expectedRulesNb = nextLong();
        QuestionnaireRulesDto rules = mock(QuestionnaireRulesDto.class);
        when(rules.getId()).thenReturn(dtoId);
        when(rules.getRulesNb()).thenReturn(expectedRulesNb);
        when(repository.findAllQuestionnaireRulesDto()).thenReturn(newArrayList(rules));

        long expectedSubObjectsNb = nextLong();
        QuestionnaireSubObjectsDto subObjects = mock(QuestionnaireSubObjectsDto.class);
        when(subObjects.getId()).thenReturn(dtoId);
        when(subObjects.getSubObjectsNb()).thenReturn(expectedSubObjectsNb);
        when(repository.findAllQuestionnaireSubObjectDto()).thenReturn(newArrayList(subObjects));

        QuestionnaireDto input = mock(QuestionnaireDto.class);
        when(input.getId()).thenReturn(dtoId);

        // When
        QuestionnaireDtoWrapper result = subject.apply(input);

        // Then
        assertThat(result.getDto()).isEqualTo(input);
        assertThat(result.getQuestionsNb()).isEqualTo(expectedQuestionsNb);
        assertThat(result.getRulesNb()).isEqualTo(expectedRulesNb);
        assertThat(result.getSubObjectsNb()).isEqualTo(expectedSubObjectsNb);
    }

    @Test
    public void shouldCreateWrapperWithoutSubElements() {
        // Given
        long dtoId = nextLong();

        QuestionnaireDto input = mock(QuestionnaireDto.class);
        when(input.getId()).thenReturn(dtoId);

        // When
        QuestionnaireDtoWrapper result = subject.apply(input);

        // Then
        assertThat(result.getDto()).isEqualTo(input);
        assertThat(result.getQuestionsNb()).isEqualTo(0);
        assertThat(result.getRulesNb()).isEqualTo(0);
        assertThat(result.getSubObjectsNb()).isEqualTo(0);
    }
}