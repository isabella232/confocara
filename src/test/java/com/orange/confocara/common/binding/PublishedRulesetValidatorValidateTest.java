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

package com.orange.confocara.common.binding;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.validation.Errors;

/**
 * see {@link PublishedRulesetValidator#validate(Object, Errors)}
 */
public class PublishedRulesetValidatorValidateTest {

    private PublishedRulesetValidator subject = new PublishedRulesetValidator();

    @Test
    public void shouldNotPopulateErrorsWhenInputIsValid() {
        // Given
        PublishedRuleset input = mock(PublishedRuleset.class);
        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(input, errors);

        // Then
        verify(errors, never()).reject(Matchers.any(ErrorCode.class));
    }

    @Test
    public void shouldPopulateErrorsWhenInputIsNull() {
        // Given
        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(null, errors);

        // Then
        verify(errors).reject(ErrorCode.NOT_FOUND);
    }
}