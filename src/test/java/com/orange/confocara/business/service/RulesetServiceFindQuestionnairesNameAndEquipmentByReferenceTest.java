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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * see {@link RulesetService#findQuestionnairesNameAndEquipmentByReference(String)}
 */
public class RulesetServiceFindQuestionnairesNameAndEquipmentByReferenceTest {

    private RulesetService subject;

    private RulesetRepository rulesetRepository;

    @Before
    public void setUp() {
        rulesetRepository = mock(RulesetRepository.class);

        subject = mock(RulesetService.class);
        when(subject.findQuestionnairesNameAndEquipmentByReference(Mockito.anyString())).thenCallRealMethod();
        ReflectionTestUtils.setField(subject, "rulesetRepository", rulesetRepository);
    }

    @Test
    public void shouldDelegateToRepository() {
        // Given
        String inputReference = RandomStringUtils.randomAlphabetic(10);

        // When
        subject.findQuestionnairesNameAndEquipmentByReference(inputReference);

        // Then
        verify(rulesetRepository).findQuestionnairesNameAndEquipmentByReference(inputReference);
    }
}
