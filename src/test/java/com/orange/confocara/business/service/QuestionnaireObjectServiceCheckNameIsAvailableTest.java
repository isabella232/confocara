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

package com.orange.confocara.business.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * see {@link QuestionnaireObjectService#checkNameIsAvailable(QuestionnaireObject)}
 */
public class QuestionnaireObjectServiceCheckNameIsAvailableTest {

    private QuestionnaireObjectService subject;

    private QuestionnaireObjectRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireObjectRepository.class);
        subject = new QuestionnaireObjectService();
        ReflectionTestUtils.setField(subject, "questionnaireObjectRepository", repository);
    }

    @Test
    public void shouldReturnTrueWhenNameDoesNotMatchInRepository() {

        // Given
        when(repository.findByName(anyString())).thenReturn(null);

        QuestionnaireObject input = givenInput(nextLong(), randomAlphabetic(10));

        // When
        boolean result = subject.checkNameIsAvailable(input);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnTrueWhenNameDoesMatchInRepositoryAndIdsAreTheSame() {

        // Given
        Long id = nextLong();
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(mock.getId()).thenReturn(id);
        when(repository.findByName(anyString())).thenReturn(mock);

        QuestionnaireObject input = givenInput(id, randomAlphabetic(10));

        // When
        boolean result = subject.checkNameIsAvailable(input);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenNameDoesMatchInRepositoryAndIdsAreDifferent() {

        // Given
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(mock.getId()).thenReturn(nextLong());
        when(repository.findByName(anyString())).thenReturn(mock);

        QuestionnaireObject input = givenInput(nextLong(), randomAlphabetic(10));

        // When
        boolean result = subject.checkNameIsAvailable(input);

        // Then
        assertThat(result).isFalse();
    }

    QuestionnaireObject givenInput(Long id, String name) {
        QuestionnaireObject mock = mock(QuestionnaireObject.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        return mock;
    }
}