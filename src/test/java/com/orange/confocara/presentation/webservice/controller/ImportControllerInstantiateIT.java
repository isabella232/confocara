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

package com.orange.confocara.presentation.webservice.controller;

import com.orange.ConfOcaraSpringApplication;
import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * this test aims to verify that the controller exists as a bean
 *
 * see {@link ExportController}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ConfOcaraSpringApplication.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class ImportControllerInstantiateIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldProvideControllerWhenServletContextIsGiven() {
        // Given

        // When
        ServletContext servletContext = wac.getServletContext();

        // Then
        Assertions.assertThat(servletContext).isNotNull();
        Assertions.assertThat(servletContext).isInstanceOf(MockServletContext.class);
        Assertions.assertThat(wac.getBean("importController")).isNotNull();
    }
}