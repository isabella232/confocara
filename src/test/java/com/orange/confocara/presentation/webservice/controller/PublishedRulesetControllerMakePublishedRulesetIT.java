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

package com.orange.confocara.presentation.webservice.controller;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.RulesetRevisionPublishingService;
import com.orange.confocara.presentation.webservice.controller.PublishedRulesetControllerMakePublishedRulesetIT.PublishedRulesetControllerMakePublishedRulesetITConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link PublishedRulesetController#makePublishedRuleset(String, Integer)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                ConfOcaraSpringApplication.class,
                PublishedRulesetControllerMakePublishedRulesetITConfig.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class PublishedRulesetControllerMakePublishedRulesetIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void shouldDelegateOperation() throws Exception {
        // Given
        String ref = randomAlphabetic(5);
        Integer version = nextInt();

        // When
        ResultActions result = when(ref, version)
                .andDo(print());

        // Then
        result.andExpect(status().isOk());
    }

    private ResultActions when(String ref, Integer version) throws Exception {

        String url = "/ws/publishing";
        return mockMvc
                .perform(put(url + "?ref=" + ref + "&version=" + version));
    }

    @TestConfiguration
    static class PublishedRulesetControllerMakePublishedRulesetITConfig {

        @Primary
        @Bean
        public RulesetRevisionPublishingService rulesetRevisionPublishingService() {

            return mock(RulesetRevisionPublishingService.class);
        }

    }
}