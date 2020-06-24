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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.orange.confocara.TestUtils;
import com.orange.confocara.connector.persistence.dto.RulesetDto;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/**
 * see {@link RulesetService#getRulesetDtos()}
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class RulesetServiceGetRulesetDtosTest {

    @InjectMocks
    private RulesetService rulesetService;

    @Mock
    RulesetRepository rulesetRepository;

    @Before
    public void setUp() {
        // inject mocks
        initMocks(this);
    }

    @Test
    public void shouldReturnRulesetResultWhenGetRulesetDtosCalled() {
        // given
        List<RulesetDtoWrapper> rulesetDtoWrappers = TestUtils.generateRulesetDtoWrapperList(1);
        List<RulesetDto> rulesetDtos = new ArrayList<>();
        rulesetDtos.add(rulesetDtoWrappers.get(0).getDto());

        when(rulesetRepository.findAllRulesetDto()).thenReturn(rulesetDtos);

        // when
        List<RulesetDtoWrapper> result = rulesetService.getRulesetDtos();

        // then
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.size() == rulesetDtoWrappers.size());
        assertThat(result.get(0).getDto().getId()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getId());
        assertThat(result.get(0).getDto().getName()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getName());
        assertThat(result.get(0).getDto().getVersion()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getVersion());
        assertThat(result.get(0).getDto().getLanguage()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getLanguage());
        assertThat(result.get(0).getDto().getComment()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getComment());
        assertThat(result.get(0).getDto().getRulesCategoryName()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getRulesCategoryName());
        assertThat(result.get(0).getDto().getDate().getTime()).isEqualTo(rulesetDtoWrappers.get(0).getDto().getDate().getTime());
    }
}
