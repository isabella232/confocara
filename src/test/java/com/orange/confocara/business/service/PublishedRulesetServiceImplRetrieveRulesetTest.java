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
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.PublishedRulesetService.PublishedRulesetServiceImpl;
import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.common.binding.PublishedRulesetValidator;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link PublishedRulesetServiceImpl#retrieveRuleset(String, Integer)}
 */
public class PublishedRulesetServiceImplRetrieveRulesetTest {

    private PublishedRulesetServiceImpl subject;

    private RulesetPublishingRepository repository;

    private PublishedRulesetValidator validator;

    private ErrorUtil errorUtil;

    @Before
    public void setUp() {
        repository = mock(RulesetPublishingRepository.class);
        validator = mock(PublishedRulesetValidator.class);
        errorUtil = mock(ErrorUtil.class);

        subject = new PublishedRulesetServiceImpl(repository, validator, errorUtil);
    }

    @Test
    public void shouldDelegateAndRetrieveExpectedRulesetWhenValidationSucceeds() {
        // Given
        PublishedRuleset expected = givenPublishedRuleset();

        // When
        PublishedRuleset result = subject.retrieveRuleset(
                randomAlphabetic(5),
                nextInt());

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test(expected = RuntimeException.class)
    public void shouldDelegateAndRetrieveNoRulesetWhenValidationFails() {
        // Given
        givenPublishedRuleset();
        givenCheckingErrorsThrowsException();

        // When
        subject.retrieveRuleset(
                randomAlphabetic(5),
                nextInt());

        // Then
    }

    PublishedRuleset givenPublishedRuleset() {
        PublishedRuleset mock = mock(PublishedRuleset.class);

        String content = randomAlphanumeric(100);
        when(mock.getContent()).thenReturn(content);
        when(repository.findOneByReferenceAndVersion(Mockito.anyString(), Mockito.anyInt())).thenReturn(mock);

        return mock;
    }

    void givenCheckingErrorsThrowsException() {
        doThrow(RuntimeException.class).when(errorUtil).checkErrors(Mockito.any(BizErrors.class));
    }
}