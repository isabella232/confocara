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

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.RulesetPublishingService;
import com.orange.confocara.presentation.view.controller.RulesetPublishingControllerPublishRulesetIT.RulesetPublishingControllerPublishRulesetITConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * see {@link RulesetPublishingController#publishRuleset(long, RedirectAttributes)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        RulesetPublishingControllerPublishRulesetITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class RulesetPublishingControllerPublishRulesetIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/rulesets/publish";

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldDelegateOperation() throws Exception {
        // Given
        long rulesetId = nextInt();

        // When
        ResultActions result = when(rulesetId);

        // Then
        result.andExpect(status().is3xxRedirection());
    }

    private ResultActions when(long rulesetId) throws Exception {
        return mockMvc
                .perform(get(url + "?id=" + rulesetId)
                        .accept(JSON_MIME_TYPE)
                        .contentType(JSON_MIME_TYPE))
                .andDo(print());
    }

    static final class RulesetPublishingControllerPublishRulesetITConfig {

        @Bean
        public RulesetPublishingService rulesetPublishingService() {

            RulesetPublishingService mock = Mockito.mock(RulesetPublishingService.class);
            Mockito
                    .doAnswer(invocation -> {
                        String id = (String) invocation.getArguments()[0];
                        log.info(String.format("Folder declined for token %s", id));
                        return "blabla";
                    })
                    .when(mock)
                    .publishRuleset(Mockito.anyByte());
            return mock;
        }
    }
}