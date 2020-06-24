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

package com.orange.confocara.presentation.webservice.qo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadQueryService.QuestionnaireReadQueryServiceImpl;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadQueryService.QuestionnaireResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @see QuestionnaireReadQueryServiceImpl#retrieveAllRules(QuestionnaireRequest)
 */
public class QuestionnaireReadQueryServiceImplRetrieveAllRulesTest {

    private QuestionnaireReadQueryServiceImpl subject;

    private RuleDtoQueryRepository repository;

    private RulesetQuestionnaireDtoQueryRepository questionnaireRepository;

    @Before
    public void setUp() {
        repository = mock(RuleDtoQueryRepository.class);

        questionnaireRepository = mock(RulesetQuestionnaireDtoQueryRepository.class);

        subject = new QuestionnaireReadQueryServiceImpl(repository, questionnaireRepository);
    }

    @Test
    public void shouldThrowBizExceptionWhenRepositoryHasNoValue() {

        // Given
        when(questionnaireRepository
                .findAllByReferenceAndVersion(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Lists.newArrayList());

        // When
        try {
            subject.retrieveAllRules(mock(QuestionnaireRequest.class));
        } catch (BizException ex) {

            // Then
            assertThat(ex.getCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Test
    public void shouldRetrieveResultWhenQuestionnaireExists() {

        // Given
        when(questionnaireRepository
                .findAllByReferenceAndVersion(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Lists.newArrayList(Mockito.mock(RulesetQuestionnaireLightDto.class)));


        QuestionnaireRequest input = ImmutableQuestionnaireRequest.builder()
                .reference(RandomStringUtils.randomAlphanumeric(5))
                .version(RandomUtils.nextInt())
                .build();

        // When
        QuestionnaireResponse result = subject.retrieveAllRules(input);

        // Then
        Assertions.assertThat(result).isNotNull();
    }
}