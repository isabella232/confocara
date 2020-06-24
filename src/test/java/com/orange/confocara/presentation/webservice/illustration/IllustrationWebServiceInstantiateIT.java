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

package com.orange.confocara.presentation.webservice.illustration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.webservice.illustration.IllustrationWebServiceInstantiateIT.IllustrationWebServiceInstantiateITConfig;
import javax.servlet.ServletContext;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * @see IllustrationWebService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        IllustrationWebServiceInstantiateITConfig.class})
@ActiveProfiles("test")
public class IllustrationWebServiceInstantiateIT {

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
        assertThat(wac.getBean("illustrationWebService")).isNotNull();
    }

    @TestConfiguration
    static class IllustrationWebServiceInstantiateITConfig {

        @Primary
        @Bean
        public IllustrationQueryService illustrationQueryService() {
            return Mockito.mock(IllustrationQueryService.class);
        }
    }
}