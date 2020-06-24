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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.RulesetValidator;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * see {@link ValidateRulesetConsumer#accept(Ruleset)}
 */
public class ValidateRulesetConsumerAcceptTest {

    private ValidateRulesetConsumer subject;

    @Test
    public void shouldDelegateValidationAndChecking() {
        // Given
        RulesetRepository repository = mock(RulesetRepository.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(mock(Ruleset.class));

        RulesetValidator validator = mock(RulesetValidator.class);
        ErrorUtil errorUtil = mock(ErrorUtil.class);

        subject = new ValidateRulesetConsumer(repository, validator, errorUtil);

        // When
        subject.accept(mock(Ruleset.class));

        // Then
        verify(validator).validate(Matchers.any(), Matchers.any());
        verify(errorUtil).checkErrors(Matchers.any());
    }
}