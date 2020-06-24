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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.webservice.RestApi;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class ImportControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private long id1 = 11;
    private String username1 = "Duchess";

    private long id2 = 22;
    private String username2 = "Thomas";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        User user = new User();
        user.setId(id1);
        user.setUsername(username1);
        user.setPasswordHash("");
        User user2 = new User();
        user2.setId(id2);
        user2.setUsername(username2);
        user2.setPasswordHash("");
        List<User> users = Arrays.asList(user, user2);
        given(userService.all()).willReturn(users);
    }

    @Test
    @WithMockUser
    public void retrieveUsers() throws Exception {

        MvcResult result = mockMvc
                .perform(get(RestApi.WS_SEC_GET_USERS))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        // then
        assertThat(json).contains(username1);
        assertThat(json).contains(username2);
    }

}
