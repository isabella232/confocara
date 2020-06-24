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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadWebServiceInstantiateIT.QuestionnaireReadWebServiceInstantiateITConfig;
import javax.servlet.ServletContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * @see QuestionnaireReadWebService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        QuestionnaireReadWebConfig.class,
        QuestionnaireReadConfig.class,
        QuestionnaireReadWebServiceInstantiateITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class QuestionnaireReadWebServiceInstantiateIT {

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
        assertThat(wac.getBean("questionnaireReadWebService")).isNotNull();
    }

    @TestConfiguration
    static class QuestionnaireReadWebServiceInstantiateITConfig {

        @Primary
        @Bean
        public QuestionnaireReadQueryService questionnaireReadQueryService() {
            return mock(QuestionnaireReadQueryService.class);
        }
    }
}