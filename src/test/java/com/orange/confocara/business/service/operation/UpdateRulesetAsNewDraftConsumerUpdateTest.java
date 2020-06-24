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

import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link UpdateRulesetAsNewDraftConsumer#accept(Ruleset)}
 */
public class UpdateRulesetAsNewDraftConsumerUpdateTest {

    private UpdateRulesetAsNewDraftConsumer subject;

    @Test
    public void shouldDelegateUpdatingAndSaving() {
        // Given
        Ruleset input = mock(Ruleset.class);
        RulesetRepository repository = mock(RulesetRepository.class);
        when(repository.findOne(Mockito.anyLong())).thenReturn(input);

        subject = new UpdateRulesetAsNewDraftConsumer(repository);

        // When
        subject.accept(mock(Ruleset.class));

        // Then
        verify(input).markAsNewDraft();
    }
}