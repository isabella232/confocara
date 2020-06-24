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

package com.orange.confocara.presentation.view.question.create;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.view.question.create.QuestionAddViewControllerSaveQuestionIT.QuestionAddViewControllerSaveQuestionITConfig;
import com.orange.confocara.presentation.view.question.edit.QuestionEditDto;
import com.orange.confocara.presentation.view.question.edit.QuestionEditViewController;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
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
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link QuestionEditViewController#saveQuestion(QuestionEditDto, Long, Principal)}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class, QuestionAddViewControllerSaveQuestionITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
public class QuestionAddViewControllerSaveQuestionIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/questions/create";

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given

        // When
        ResultActions result = when();

        // Then
        result
                .andExpect(status().is3xxRedirection());
    }

    private ResultActions when() throws Exception {
        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(post(url)
                        .principal(principal)
                        .accept(JSON_MIME_TYPE)
                        .contentType(JSON_MIME_TYPE))
                .andDo(print());
    }

    static final class QuestionAddViewControllerSaveQuestionITConfig {

        private static Object answer(InvocationOnMock invocation) {
            log.info(String.format("Question is being created"));
            return null;
        }

        @Bean
        public QuestionAddService questionAddService() {
            QuestionAddService mock = mock(QuestionAddService.class);
            doAnswer(QuestionAddViewControllerSaveQuestionITConfig::answer)
                    .when(mock)
                    .saveQuestion(Mockito.any(QuestionCreateDto.class), Mockito.any(Principal.class));
            return mock;
        }
    }
}
