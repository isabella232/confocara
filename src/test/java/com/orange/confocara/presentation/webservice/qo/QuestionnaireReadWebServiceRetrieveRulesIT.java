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

package com.orange.confocara.presentation.webservice.qo;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadQueryService.QuestionnaireResponse;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadWebServiceRetrieveRulesIT.QuestionnaireReadWebServiceRetrieveRulesITConfig;
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
import org.springframework.web.context.WebApplicationContext;

/**
 * @see QuestionnaireReadWebService#rulesByQuestionnaire(String, Integer)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        QuestionnaireReadWebConfig.class,
        QuestionnaireReadConfig.class,
        QuestionnaireReadWebServiceRetrieveRulesITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class QuestionnaireReadWebServiceRetrieveRulesIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given
        String reference = "QO" + nextInt(1, 100);
        Integer version = nextInt(0, 100);

        // When
        ResultActions result = when(reference, version);

        // Then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MIME_TYPE));
    }

    private ResultActions when(String ref, Integer version) throws Exception {

        String urlTemplate = "/ws/qo/" + ref + "/" + version;

        return mockMvc
                .perform(get(urlTemplate))
                .andDo(print());
    }

    @TestConfiguration
    static class QuestionnaireReadWebServiceRetrieveRulesITConfig {

        @Primary
        @Bean
        public QuestionnaireReadQueryService questionnaireReadQueryService() {

            QuestionnaireReadQueryService mock = mock(QuestionnaireReadQueryService.class);

            Mockito
                    .when(mock.retrieveAllRules(any(QuestionnaireRequest.class)))
                    .thenReturn(QuestionnaireResponse.emptyResponse());

            return mock;
        }
    }
}
