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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.orange.confocara.TestUtils;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.QuestionnaireQuestionsDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireRulesDto;
import com.orange.confocara.presentation.view.qo.list.QuestionnaireDraftListQueryService.QuestionnaireDraftListQueryServiceImpl;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * see {@link QuestionnaireDraftListQueryService#retrieveQuestionnaires()}
 */
public class QuestionnaireDraftListQueryServiceRetrieveQuestionnairesTest {

    @InjectMocks
    private QuestionnaireDraftListQueryServiceImpl questionnaireDraftListQueryService;

    @Mock
    private QuestionnaireDtoRepository questionnaireDtoRepository;

    @Before
    public void setUp() {
        // inject mocks
        initMocks(this);
    }

    @Test
    public void shouldReturnQuestionnaireResultWhenGetQuestionnaireDtosCalled() {
        // given
        List<QuestionnaireDtoWrapper> questionnaireDtoWrappers = TestUtils
                .generateQuestionnaireDtoWrapperList(1);
        QuestionnaireDtoWrapper expectedWrapper = questionnaireDtoWrappers.get(0);
        List<QuestionnaireDto> questionnaireDtos = newArrayList(questionnaireDtoWrappers.get(0).getDto());
        when(questionnaireDtoRepository.findAll()).thenReturn(questionnaireDtos);

        List<QuestionnaireQuestionsDto> questionnaireQuestionsDtos = newArrayList(
                makeQuestionnaireQuestionsDto(
                        expectedWrapper.getDto().getId(),
                        expectedWrapper.getQuestionsNb()));
        when(questionnaireDtoRepository.findAllQuestionnaireQuestionDto())
                .thenReturn(questionnaireQuestionsDtos);

        List<QuestionnaireRulesDto> questionnaireRulesDtos = newArrayList(
                makeQuestionnaireRuleDtoTest(
                        expectedWrapper.getDto().getId(),
                        expectedWrapper.getRulesNb()));
        when(questionnaireDtoRepository.findAllQuestionnaireRulesDto())
                .thenReturn(questionnaireRulesDtos);

        // when
        List<QuestionnaireDtoWrapper> result = questionnaireDraftListQueryService
                .retrieveQuestionnaires();

        // then
        assertThat(result).hasSize(1);
        assertThat(result).hasSize(questionnaireDtoWrappers.size());

        QuestionnaireDtoWrapper actualWrapper = result.get(0);
        assertThat(actualWrapper.getQuestionsNb()).isEqualTo(expectedWrapper.getQuestionsNb());
        assertThat(actualWrapper.getRulesNb()).isEqualTo(expectedWrapper.getRulesNb());
        assertThat(actualWrapper.getDto().getId()).isEqualTo(expectedWrapper.getDto().getId());
        assertThat(actualWrapper.getDto().getName()).isEqualTo(expectedWrapper.getDto().getName());
        assertThat(actualWrapper.getDto().getRulesCategoryName()).isEqualTo(expectedWrapper.getDto().getRulesCategoryName());
//        assertThat(actualWrapper.getDto().getDate().getTime()).isEqualTo(expectedWrapper.getDto().getDate().getTime());
    }

    private QuestionnaireQuestionsDto makeQuestionnaireQuestionsDto(long id, long questionsNb) {
        return new QuestionnaireQuestionsDto() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public long getQuestionsNb() {
                return questionsNb;
            }
        };
    }

    private QuestionnaireRulesDto makeQuestionnaireRuleDtoTest(long id, long rulesNb) {
        return new QuestionnaireRulesDto() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public long getRulesNb() {
                return rulesNb;
            }
        };
    }
}