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

package com.orange.confocara.presentation.view.qo.publish;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.presentation.view.qo.publish.SimpleQuestionnairePublishingControllerPublishQuestionnaireObjectIT.SimpleQuestionnairePublishingControllerPublishQuestionnaireObjectITConfig;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link SimpleQuestionnairePublishingController#publishQuestionnaireObject(QuestionnaireObject)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        SimpleQuestionnairePublishingControllerPublishQuestionnaireObjectITConfig.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class SimpleQuestionnairePublishingControllerPublishQuestionnaireObjectIT {

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

        // When
        ResultActions result = when();

        // Then
        result.andExpect(status().is3xxRedirection());
    }

    private ResultActions when() throws Exception {

        return mockMvc
                .perform(get("/questionnaires/publish"))
                .andDo(print());
    }

    @TestConfiguration
    static class SimpleQuestionnairePublishingControllerPublishQuestionnaireObjectITConfig {

        @Primary
        @Bean
        public QuestionnaireObjectPublishingService questionnaireObjectPublishingService() {
            return Mockito.mock(QuestionnaireObjectPublishingService.class);
        }
    }
}