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

package com.orange.confocara.presentation.webservice.rule;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.webservice.rule.RuleQueryService.RulePageResponse;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebService.RuleSearchCriteria;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebServiceAllRulesIT.RuleSearchWebServiceAllRulesITConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

/**
 * @see RuleSearchWebService#allRules(Long, Long, Long, Integer, Integer, String, String)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        RuleSearchWebConfig.class,
        RuleSearchConfig.class,
        RuleSearchWebServiceAllRulesITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class RuleSearchWebServiceAllRulesIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given

        // When
        ResultActions result = when(nextLong(), nextLong(), nextLong());

        // Then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(0));
    }

    private ResultActions when(Long contextId, Long equipmentId, Long profileTypeId)
            throws Exception {

        String urlTemplate = "/ws/rules?size=500&sort=id,desc&concernIds=4&concernIds=5&contextIds="
                + contextId + "&equipmentIds=" + equipmentId + "&concernIds=" + profileTypeId;

        RequestBuilder request = get(urlTemplate);

        return mockMvc
                .perform(request)
                .andDo(print());
    }

    @TestConfiguration
    static class RuleSearchWebServiceAllRulesITConfig {

        @Primary
        @Bean
        public RuleQueryService ruleQueryService() {

            RuleQueryService mock = mock(RuleQueryService.class);

            Mockito
                    .when(mock.retrieveRules(any(RuleSearchCriteria.class), any(Pageable.class)))
                    .thenReturn(new RulePageResponse());

            return mock;
        }
    }
}