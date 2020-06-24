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

package com.orange.confocara.business.service.transform;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.business.service.utils.JacksonUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

/**
 * see {@link RulesetToPublishedRulesetFunction#apply(Ruleset, BizErrors)}
 */
public class RulesetToPublishedRulesetFunctionApplyTest {

    private RulesetToPublishedRulesetFunction subject;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        JacksonUtil jacksonUtil = mock(JacksonUtil.class);
        objectMapper = mock(ObjectMapper.class);
        when(jacksonUtil.buildObjectMapper()).thenReturn(objectMapper);

        subject = new RulesetToPublishedRulesetFunction(jacksonUtil);
    }

    @Test
    public void shouldRetrieveValidObjectWithNoErrorsWhenDelegationSucceeds()
            throws JsonProcessingException {
        // Given
        String content = randomAlphanumeric(10);
        when(objectMapper.writeValueAsString(any())).thenReturn(content);

        String reference = randomAlphabetic(5);
        Integer version = nextInt(0, 10);

        Ruleset ruleset = givenRuleset(reference, version);
        BizErrors errors = mock(BizErrors.class);

        // When
        PublishedRuleset result = subject.apply(ruleset, errors);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReference()).isEqualTo(reference);
        assertThat(result.getVersion()).isEqualTo(version);
        assertThat(result.getContent()).isEqualTo(content);
    }

    @Test
    public void shouldRetrieveNullObjectWithErrorsWhenDelegationFails()
            throws JsonProcessingException {
        // Given
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        Ruleset ruleset = mock(Ruleset.class);
        BizErrors errors = mock(BizErrors.class);

        // When
        PublishedRuleset result = subject.apply(ruleset, errors);

        // Then
        assertThat(result).isNull();
        verify(errors).reject(ErrorCode.UNEXPECTED, "N/A");
    }

    private Ruleset givenRuleset(String ref, Integer version) {
        return Ruleset.builder()
                .reference(ref)
                .version(version)
                .questionnaireObjects(Lists.newArrayList())
                .build();
    }
}