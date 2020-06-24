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

package com.orange.confocara.presentation.webservice.subobject;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.util.Lists.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.presentation.webservice.subobject.SubObjectWebService.SubObjectSaveRequest;
import com.orange.confocara.presentation.webservice.subobject.SubObjectWebServiceListAllIT.SubObjectWebServiceListAllITConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

/**
 * see {@link SubObjectWebService#listAll(Long)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        SubObjectWebServiceListAllITConfig.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class SubObjectWebServiceListAllIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private static final String JSON_MIME_TYPE = "application/json;charset=UTF-8";

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void shouldPerformRequest() throws Exception {
        // Given
        Long id = nextLong(0, 100);

        // When
        ResultActions result = when(id)
                .andDo(print());

        // Then
        result
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON_MIME_TYPE));
    }

    private ResultActions when(Long id) throws Exception {
        return mockMvc
                .perform(get("/ws/subobjects/?id=" + id)
                        .contentType(JSON_MIME_TYPE));
    }

    @TestConfiguration
    static class SubObjectWebServiceListAllITConfig {

        @Bean
        public SubObjectsQueryService subObjectsQueryService() {
            SubObjectsQueryService mock = Mockito.mock(SubObjectsQueryService.class);
            Mockito.when(mock.retrieveSubObjects(Mockito.anyLong())).thenReturn(newArrayList(new EquipmentDto(
                    randomAlphabetic(10), Lists.newArrayList(randomAlphabetic(10)))));
            return mock;
        }

        @Bean
        public SubObjectsPublishService subObjectsPublishService() {
            return Mockito.mock(SubObjectsPublishService.class);
        }
    }
}