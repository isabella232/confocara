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

package com.orange.confocara.presentation.view.qo.view;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.presentation.view.qo.view.QuestionnaireObjectQueryService.QuestionnaireObjectQueryServiceImpl;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link QuestionnaireObjectQueryService.QuestionnaireObjectQueryServiceImpl#retrieveOneQuestionnaire(String,
 * Integer)}
 */
public class QuestionnaireObjectQueryServiceImplRetrieveOneQuestionnaireTest {

    private QuestionnaireObjectQueryServiceImpl subject;

    private QuestionnaireObjectQueryRepository repository;

    private Function<QuestionnaireObject, QuestionnaireViewDto> mapper;

    @Before
    public void setUp() {

        repository = mock(QuestionnaireObjectQueryRepository.class);

        mapper = mock(Function.class);

        subject = new QuestionnaireObjectQueryServiceImpl(repository, mapper);
    }

    @Test
    public void shouldDelegateToMapperWhenRepositoryFindsOneEntity() {

        // Given
        String inputRef = randomAlphanumeric(10);
        Integer inputVersion = nextInt();

        when(repository.existsByReferenceAndVersion(inputRef, inputVersion)).thenReturn(true);
        when(repository.findByReferenceAndVersion(inputRef, inputVersion))
                .thenReturn(mock(QuestionnaireObject.class));

        QuestionnaireViewDto expected = mock(QuestionnaireViewDto.class);
        when(mapper.apply(Mockito.any())).thenReturn(expected);

        // When
        Object result = subject.retrieveOneQuestionnaire(inputRef, inputVersion);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenEntityDoesNotExistInRepository() {

        // Given
        String inputRef = randomAlphanumeric(10);
        Integer inputVersion = nextInt();

        when(repository.existsByReferenceAndVersion(inputRef, inputVersion)).thenReturn(false);

        // When
        subject.retrieveOneQuestionnaire(randomAlphanumeric(10), nextInt());

        // Then
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenRepositoryFindsNoEntity() {

        // Given
        String inputRef = randomAlphanumeric(10);
        Integer inputVersion = nextInt();

        when(repository.existsByReferenceAndVersion(inputRef, inputVersion)).thenReturn(true);
        when(repository.findByReferenceAndVersion(inputRef, inputVersion)).thenReturn(null);

        // When
        subject.retrieveOneQuestionnaire(randomAlphanumeric(10), nextInt());

        // Then
    }
}