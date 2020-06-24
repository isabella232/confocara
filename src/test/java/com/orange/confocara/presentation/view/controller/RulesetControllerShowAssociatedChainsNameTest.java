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

package com.orange.confocara.presentation.view.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.RulesetService;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

/**
 * see {@link RulesetController#showAssociatedChainsName(String, Model)}
 */
public class RulesetControllerShowAssociatedChainsNameTest {

    private RulesetController subject;

    private RulesetService rulesetService;

    @Before
    public void setUp() {
        rulesetService = mock(RulesetService.class);

        subject = mock(RulesetController.class);
        when(subject.showAssociatedChainsName(anyString(), any(Model.class))).thenCallRealMethod();
        ReflectionTestUtils.setField(subject, "rulesetService", rulesetService);
    }

    @Test
    public void shouldPopulateModelWithServiceResult() {
        // Given
        List expected = mock(List.class);
        when(rulesetService.findQuestionnairesNameAndEquipmentByReference(anyString())).thenReturn(expected);

        Model model = mock(Model.class);

        // When
        subject.showAssociatedChainsName(RandomStringUtils.randomAlphabetic(10), model);

        // Then
        verify(model).addAttribute("questionnaireDtoList", expected);
    }
}