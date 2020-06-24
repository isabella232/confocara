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

package com.orange.confocara.presentation.view.question.edit;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.presentation.view.question.edit.QuestionEditViewControllerViewQuestionIT.QuestionEditViewControllerEditQuestionITConfig;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
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
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link QuestionEditViewController#viewQuestion(Principal, Long, String, Model, HttpServletResponse)}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class, QuestionEditViewControllerEditQuestionITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
public class QuestionEditViewControllerViewQuestionIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/questions/edit";

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given
        Long expectedId = RandomUtils.nextLong();

        // When
        ResultActions result = when(expectedId);

        // Then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    private ResultActions when(Long id) throws Exception {
        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(get(url + "?id=" + id)
                        .principal(principal)
                        .accept(JSON_MIME_TYPE)
                        .contentType(JSON_MIME_TYPE))
                .andDo(print());
    }

    static final class QuestionEditViewControllerEditQuestionITConfig {

        private static Object answer(InvocationOnMock invocation) {
            Long id = invocation.getArgumentAt(0, Long.class);
            log.info(String.format("Identifier is %s", id));

            Model model = invocation.getArgumentAt(2, Model.class);
            model.addAttribute(
                    "question",
                    QuestionEditDto
                            .builder()
                            .id(id)
                            .label(randomAlphabetic(10))
                            .reference(randomAlphabetic(10))
                            .ruleIds(newArrayList(randomAlphabetic(10)))
                            .rulesCategory(new RulesCategory())
                            .subject(new Subject())
                            .state(randomAlphabetic(10))
                            .build());
            model.addAttribute("subjects", newArrayList());
            model.addAttribute("rules", newArrayList());
            model.addAttribute("associatedrules", newArrayList());
            model.addAttribute("orderedRuleIds", newArrayList());
            model.addAttribute("rule", new Rule());
            model.addAttribute("id", id);
            model.addAttribute("username", randomAlphabetic(10));

            return null;
        }

        @Bean
        public QuestionEditService questionEditService() {
            QuestionEditService mock = mock(QuestionEditService.class);
            doAnswer(QuestionEditViewControllerEditQuestionITConfig::answer)
                    .when(mock)
                    .loadQuestion(Mockito.anyLong(), Mockito.any(Principal.class), Mockito.any(Model.class));
            return mock;
        }
    }
}
