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

package com.orange.confocara.presentation.view.question.list;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.view.question.list.QuestionListViewControllerIndexIT.QuestionListViewControllerIndexITConfig;
import java.security.Principal;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link QuestionListViewController#index(Principal, Model)}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class,
        QuestionListViewControllerIndexITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
public class QuestionListViewControllerIndexIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/questions";

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
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    private ResultActions when() throws Exception {
        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(get(url)
                        .principal(principal)
                        .accept(JSON_MIME_TYPE)
                        .contentType(JSON_MIME_TYPE))
                .andDo(print());
    }

    static final class QuestionListViewControllerIndexITConfig {

        private static Object answer(InvocationOnMock invocation) {
            log.info(String.format("Retrieving a bunch of questions"));

            Question mock = new Question();
            mock.setLabel(randomAlphabetic(10));
            mock.setReference(randomAlphabetic(10));

            Subject s = new Subject();
            s.setName(randomAlphabetic(10));
            mock.setSubject(s);

            mock.setState(randomAlphabetic(10));
            mock.setDate(new Date());

            Rule rule = new Rule();
            rule.setReference("R1");
            rule.setLabel(randomAlphabetic(10));
            mock.setRules(newArrayList(rule));
            mock.setRulesOrder(newArrayList("R1"));

            User user = new User();
            user.setUsername(randomAlphabetic(10));
            mock.setUser(user);

            return newArrayList();
        }

        @Bean
        public QuestionService questionService() {
            QuestionService mock = mock(QuestionService.class);
            doAnswer(QuestionListViewControllerIndexITConfig::answer)
                    .when(mock)
                    .all();
            return mock;
        }
    }
}
