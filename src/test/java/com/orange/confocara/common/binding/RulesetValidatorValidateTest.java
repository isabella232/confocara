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
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * see {@link RulesetValidator#validate(Object, BizErrors)}
 */
public class RulesetValidatorValidateTest {

    private RulesetValidator subject;

    private RulesetRepository rulesetRepository;

    private RulesetPublishingRepository repository;

    @Before
    public void setUp() {
        rulesetRepository = mock(RulesetRepository.class);

        repository = mock(RulesetPublishingRepository.class);

        subject = new RulesetValidator(rulesetRepository, repository);
    }

    @Test
    public void shouldRejectInputWhenNull() {
        // Given
        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(null, errors);

        // Then
        verify(errors).reject(ErrorCode.NOT_FOUND, "the ruleset does not exist");
    }

    @Test
    public void shouldRejectInputWhenInputHasNullQuestionnaireObject() {
        // Given
        Ruleset input = mock(Ruleset.class);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(mock(Ruleset.class));

        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(input, errors);

        // Then
        verify(errors).reject(ErrorCode.INVALID, "the ruleset is not well formatted");
    }

    @Test
    public void shouldRejectInputWhenInputHasNoQuestionnaireObject() {
        // Given
        Ruleset input = mock(Ruleset.class);
        when(input.getQuestionnaireObjects()).thenReturn(null);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(input);

        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(mock(Ruleset.class), errors);

        // Then
        verify(errors).reject(ErrorCode.INVALID, "the ruleset is not well formatted");
    }

    @Test
    public void shouldRejectInputWhenInputIdDoesNotExist() {
        // Given
        BizErrors errors = mock(BizErrors.class);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(null);

        // When
        subject.validate(mock(Ruleset.class), errors);

        // Then
        verify(errors).reject(ErrorCode.NOT_FOUND, "the ruleset is missing");
    }

    @Test
    public void shouldRejectInputWhenInputHasAlreadyBeenPublished() {
        // Given
        Ruleset input = mock(Ruleset.class);
        when(input.getQuestionnaireObjects()).thenReturn(Lists.newArrayList(mock(
                QuestionnaireObject.class)));
        when(repository.findOneByReferenceAndVersion(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(mock(
                        PublishedRuleset.class));
        BizErrors errors = mock(BizErrors.class);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(input);

        // When
        subject.validate(mock(Ruleset.class), errors);

        // Then
        verify(errors).reject(ErrorCode.CONFLICT, "the ruleset has already been published");
    }

    @Test
    public void shouldNotRejectInputWhenInputIsValid() {
        // Given
        Ruleset input = mock(Ruleset.class);
        when(input.getQuestionnaireObjects()).thenReturn(Lists.newArrayList(mock(
                QuestionnaireObject.class)));
        when(repository.findOneByReferenceAndVersion(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(null);
        when(rulesetRepository.findOne(Mockito.anyLong())).thenReturn(input);

        BizErrors errors = mock(BizErrors.class);

        // When
        subject.validate(mock(Ruleset.class), errors);

        // Then
        verify(errors, never()).reject(Matchers.any(ErrorCode.class));
        verify(errors, never()).reject(Matchers.any(ErrorCode.class), Matchers.anyString());
    }
}