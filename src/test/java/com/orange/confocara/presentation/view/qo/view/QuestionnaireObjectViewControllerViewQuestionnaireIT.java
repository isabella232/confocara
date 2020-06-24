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

package com.orange.confocara.presentation.view.qo.view;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.view.qo.view.QuestionnaireObjectViewControllerViewQuestionnaireIT.QuestionnaireObjectViewControllerViewQuestionnaireITConfig;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link QuestionnaireObjectViewController#viewQuestionnaire(Principal, String, Integer,
 * Model)}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class,
        QuestionnaireObjectViewControllerViewQuestionnaireITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
public class QuestionnaireObjectViewControllerViewQuestionnaireIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/questionnaires/view";

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    private static String expectedReference = randomAlphanumeric(10);

    private static String expectedCategory = randomAlphanumeric(10);

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    private ResultActions when(String reference, Integer version) throws Exception {
        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(get(url + "?r=" + reference + "&v=" + version)
                        .principal(principal)
                        .accept(JSON_MIME_TYPE)
                        .contentType(JSON_MIME_TYPE))
                .andDo(print());
    }

    static final class QuestionnaireObjectViewControllerViewQuestionnaireITConfig {

        @Bean
        public QuestionnaireObjectQueryService questionnaireObjectQueryService() {
            QuestionnaireObjectQueryService mock = mock(QuestionnaireObjectQueryService.class);

            QuestionnaireViewDto dto = ImmutableQuestionnaireViewDto
                    .builder()
                    .rulesets(newArrayList("RS1"))
                    .chains(newArrayList(ImmutableChainView.builder().id(1).name("name").reference("ref").build()))
                    .questionnaire(ImmutableQuestionnaireView.builder().id(1).name("name").reference("ref").version(1).build())
                    .build();

            Mockito.when(mock.retrieveOneQuestionnaire(anyString(), anyInt())).thenReturn(dto);

            return mock;
        }
    }
}
