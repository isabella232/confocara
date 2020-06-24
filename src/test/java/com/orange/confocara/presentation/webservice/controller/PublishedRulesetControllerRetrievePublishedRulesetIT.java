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
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.PublishedRulesetService;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.presentation.CommonWebConfig;
import com.orange.confocara.presentation.webservice.controller.PublishedRulesetControllerRetrievePublishedRulesetIT.PublishedRulesetControllerRetrievePublishedRulesetITConfig;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link PublishedRulesetController#retrievePublishedRuleset(String, Integer)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        PublishedRulesetControllerRetrievePublishedRulesetITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class PublishedRulesetControllerRetrievePublishedRulesetIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/ws/findruleset";

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldDelegateOperation() throws Exception {
        // Given
        String ref = randomAlphabetic(5);
        Integer version = nextInt();

        // When
        ResultActions result = when(ref, version);

        // Then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MIME_TYPE))
                .andExpect(content().string(containsString("reference")))
                .andExpect(content().string(containsString("RS2")))
                .andExpect(content().string(containsString("version")));
    }

    private ResultActions when(String ref, Integer version) throws Exception {

        return mockMvc
                .perform(post(url + "?ref=" + ref + "&version=" + version))
                .andDo(print());
    }

    @TestConfiguration
    static class PublishedRulesetControllerRetrievePublishedRulesetITConfig {

        @Primary
        @Bean
        public PublishedRulesetService publishedRulesetService() {

            PublishedRulesetService mock = Mockito.mock(PublishedRulesetService.class);
            PublishedRuleset ruleset = mock(PublishedRuleset.class);
            Mockito.when(ruleset.getContent()).thenReturn("{\n"
                    + "   \"reference\": \"RS2\",\n"
                    + "   \"version\": 2,\n"
                    + "   \"language\": \"fr\",\n"
                    + "   \"type\": \"Accessibilité des Circulations\""
                    + "}");

            Mockito.doAnswer(invocation -> {
                String id = (String) invocation.getArguments()[0];
                log.info("Retrieve the PublishedRuleset %{}", id);
                return ruleset;
            })
                    .when(mock)
                    .retrieveRuleset(Mockito.anyString(), Mockito.anyInt());
            return mock;
        }
    }
}