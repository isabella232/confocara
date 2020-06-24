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

package com.orange.confocara.presentation.view.chain;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.RandomUtils.*;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.presentation.view.chain.ChainEditViewControllerEditChainIT.ChainEditViewControllerEditChainITConfig;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
 * see {@link ChainEditViewController#editChain(Principal, Long, String, Model, HttpServletResponse)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        ChainEditViewControllerEditChainITConfig.class
})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
@ActiveProfiles("test")
public class ChainEditViewControllerEditChainIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String url = "/chains/edit";

    private static final String HTML_MIME_TYPE = "text/html;charset=UTF-8";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given
        Long chainId = nextLong(0, 100);

        // When
        ResultActions result = when(chainId);

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(HTML_MIME_TYPE));
    }

    private ResultActions when(Long chainId) throws Exception {
        UsernamePasswordAuthenticationToken principal = mock(
                UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext().setAuthentication(principal);

        return mockMvc
                .perform(get(url + "?id=" + chainId)
                        .principal(principal)
                        .accept(HTML_MIME_TYPE)
                        .contentType(HTML_MIME_TYPE))
                .andDo(print());
    }

    @TestConfiguration
    static class ChainEditViewControllerEditChainITConfig {

        @Primary
        @Bean
        public QuestionService questionService() {
            Question question = new Question();
            question.setReference(randomAlphanumeric(10));
            question.setLabel(randomAlphanumeric(10));

            RulesCategory category = new RulesCategory();
            category.setId(nextLong());
            category.setName(randomAlphabetic(10));
            question.setRulesCategory(category);

            QuestionService mock = Mockito.mock(QuestionService.class);
            Mockito.when(mock.all()).thenReturn(Lists.newArrayList(question));
            return mock;
        }

        @Primary
        @Bean
        public ChainService chainService() {
            RulesCategory category = new RulesCategory();
            category.setId(nextLong());
            category.setName(randomAlphanumeric(10));

            Question question = new Question();
            question.setReference(randomAlphanumeric(10));
            question.setLabel(randomAlphanumeric(10));
            question.setRulesCategory(category);

            Chain chain = new Chain();
            chain.setReference(randomAlphanumeric(10));
            chain.setName(randomAlphanumeric(10));
            chain.setRulesCategory(category);
            chain.setQuestions(Lists.newArrayList(question));

            ChainService mock = Mockito.mock(ChainService.class);
            Mockito.when(mock.withId(Mockito.anyLong())).thenReturn(chain);
            return mock;
        }
    }
}