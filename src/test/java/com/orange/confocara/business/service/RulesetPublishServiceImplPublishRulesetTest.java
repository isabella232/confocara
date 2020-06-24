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

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.RulesetPublishingService.RulesetPublishServiceImpl;
import com.orange.confocara.business.service.operation.SavePublishedRulesetConsumer;
import com.orange.confocara.business.service.operation.UpdateRulesetAsNewDraftConsumer;
import com.orange.confocara.business.service.operation.UpdateRulesetAsPublishedConsumer;
import com.orange.confocara.business.service.operation.ValidateRulesetConsumer;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link RulesetPublishServiceImpl#publishRuleset(long)}
 */
public class RulesetPublishServiceImplPublishRulesetTest {

    private RulesetPublishServiceImpl subject;

    private RulesetRepository rulesetRepository;

    @Before
    public void setUp() {
        rulesetRepository = mock(RulesetRepository.class);
        subject = new RulesetPublishServiceImpl(
                rulesetRepository,
                mock(ValidateRulesetConsumer.class),
                mock(UpdateRulesetAsPublishedConsumer.class),
                mock(SavePublishedRulesetConsumer.class),
                mock(UpdateRulesetAsNewDraftConsumer.class));
    }

    @Test
    public void shouldDelegateOperations() {
        // Given
        when(rulesetRepository.exists(Mockito.anyLong())).thenReturn(true);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(mock(Ruleset.class));

        Integer rulesetId = nextInt(0, 5);

        // When
        subject.publishRuleset(rulesetId);

        // Then
    }
}
