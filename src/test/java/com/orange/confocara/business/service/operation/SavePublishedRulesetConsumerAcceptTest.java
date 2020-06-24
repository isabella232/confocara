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

package com.orange.confocara.business.service.operation;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.function.BiFunction;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * see {@link SavePublishedRulesetConsumer#accept(Ruleset)}
 */
public class SavePublishedRulesetConsumerAcceptTest {

    private SavePublishedRulesetConsumer subject;

    @Test
    public void shouldDelegateTransformationAndSaving() {
        // Given
        Ruleset input = mock(Ruleset.class);
        RulesetRepository repository = mock(RulesetRepository.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(input);

        BiFunction function = mock(BiFunction.class);
        PublishedRuleset output = mock(PublishedRuleset.class);
        when(output.getId()).thenReturn(nextLong());
        when(output.getReference()).thenReturn(randomAlphabetic(10));
        when(output.getVersion()).thenReturn(nextInt());
        when(function.apply(Mockito.any(), Mockito.any())).thenReturn(output);

        RulesetPublishingRepository publishingRepository = mock(RulesetPublishingRepository.class);
        when(publishingRepository.save(Mockito.any(PublishedRuleset.class))).thenReturn(mock(PublishedRuleset.class));
        ErrorUtil errorUtil = mock(ErrorUtil.class);

        subject = new SavePublishedRulesetConsumer(function, repository, publishingRepository, errorUtil);

        // When
        subject.accept(mock(Ruleset.class));

        // Then
        verify(function).apply(Matchers.any(), Matchers.any());
        verify(publishingRepository).save(Matchers.any(PublishedRuleset.class));
        verify(errorUtil).checkErrors(Matchers.any());
    }
}