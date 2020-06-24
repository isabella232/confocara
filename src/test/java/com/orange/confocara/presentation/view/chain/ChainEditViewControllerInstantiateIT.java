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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.presentation.view.chain.ChainEditViewControllerInstantiateIT.ChainEditViewControllerInstantiateITConfig;
import javax.servlet.ServletContext;
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
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link ChainEditViewController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        ChainEditViewControllerInstantiateITConfig.class
})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Slf4j
public class ChainEditViewControllerInstantiateIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void shouldProvideWebServiceWhenServletContextIsGiven() {
        // Given

        // When
        ServletContext servletContext = wac.getServletContext();

        // Then
        assertThat(servletContext).isNotNull();
        assertThat(servletContext).isInstanceOf(MockServletContext.class);
        assertThat(wac.getBean("chainEditViewController")).isNotNull();
    }

    @TestConfiguration
    static class ChainEditViewControllerInstantiateITConfig {

        @Primary
        @Bean
        public QuestionService questionService() {
            return Mockito.mock(QuestionService.class);
        }

        @Primary
        @Bean
        public ChainService chainServicee() {
            return Mockito.mock(ChainService.class);
        }
    }
}