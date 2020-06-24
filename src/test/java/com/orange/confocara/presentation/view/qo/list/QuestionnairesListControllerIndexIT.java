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

package com.orange.confocara.presentation.view.qo.list;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.QuestionnaireObjectService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.presentation.view.qo.list.QuestionnairesListControllerIndexIT.QuestionnairesListControllerIndexITConfig;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link QuestionnairesListController#index(Principal, Model)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        QuestionnairesListControllerIndexITConfig.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class QuestionnairesListControllerIndexIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private static final String HTML_MIME_TYPE = "text/html;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldDelegateOperation() throws Exception {
        // Given

        // When
        ResultActions result = when();

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(HTML_MIME_TYPE));
    }

    private ResultActions when() throws Exception {

        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(get("/questionnaires")
                        .principal(principal))
                .andDo(print());
    }

    @TestConfiguration
    static class QuestionnairesListControllerIndexITConfig {

        @Primary
        @Bean
        public RulesCategoryService rulesCategoryService() {
            return mock(RulesCategoryService.class);
        }

        @Primary
        @Bean
        public QuestionnaireObjectService questionnaireObjectService() {
            return mock(QuestionnaireObjectService.class);
        }

        @Primary
        @Bean
        public QuestionnaireDraftListQueryService questionnaireDraftListService() {
            return mock(QuestionnaireDraftListQueryService.class);
        }

        @Primary
        @Bean
        public QuestionnairePublishedListQueryService publishedListQueryService() {
            return mock(QuestionnairePublishedListQueryService.class);
        }
    }
}