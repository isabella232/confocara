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
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.RulesetPublishingService.RulesetPublishServiceImpl;
import com.orange.confocara.business.service.RulesetRevisionPublishingService.RulesetRevisionPublishingServiceImpl;
import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import java.util.function.BiFunction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * see {@link RulesetPublishServiceImpl#publishRuleset(long)}
 */
public class RulesetRevisionPublishingServiceImplGeneratePublishedRulesetTest {

    private RulesetRevisionPublishingServiceImpl subject;

    private RulesetPublishingRepository rulesetPublishingRepository;

    private RulesetService rulesetService;

    private BiFunction function;

    private ErrorUtil errorUtil;

    @Before
    public void setUp() {
        function = mock(BiFunction.class);
        rulesetService = mock(RulesetService.class);
        rulesetPublishingRepository = mock(RulesetPublishingRepository.class);
        errorUtil = mock(ErrorUtil.class);

        subject = new RulesetRevisionPublishingServiceImpl(function, rulesetPublishingRepository, rulesetService, errorUtil);
    }

    @Test
    public void shouldDelegateOperationsWhenInputsAreValid() {
        // Given
        when(rulesetService.findPublishedRulesetByReferenceAndVersion(anyString(), anyInt())).thenReturn(mock(Ruleset.class));
        when(rulesetPublishingRepository.save(Mockito.any(PublishedRuleset.class))).thenReturn(mock(PublishedRuleset.class));

        String reference = randomAlphabetic(5);
        Integer version = nextInt(0, 5);

        // When
        subject.generatePublishedRuleset(reference, version);

        // Then
        verify(rulesetPublishingRepository).save(Matchers.any(PublishedRuleset.class));
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenInputsAreNotValid() {
        // Given
        when(rulesetPublishingRepository.findOneByReferenceAndVersion(anyString(), anyInt())).thenReturn(mock(PublishedRuleset.class));

        String reference = randomAlphabetic(5);
        Integer version = nextInt(0, 5);

        // When
        subject.generatePublishedRuleset(reference, version);

        // Then
    }
}
