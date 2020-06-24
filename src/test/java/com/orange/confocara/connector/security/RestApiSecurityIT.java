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

package com.orange.confocara.connector.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.webservice.RestApi;
import com.orange.confocara.presentation.webservice.model.UserCredentialsWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RestApiSecurityIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    private String username = "roro";
    private String password = "rara";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        if (shouldCreateUser()) {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(password);
            user.setEmail("toto@a");
            userService.create(user);
        }
    }

    private boolean shouldCreateUser() {
        return userService.all().stream()
                .filter(u -> u.getUsername().equals(username))
                .count() == 0;
    }

    @Test
    public void failedToRetrieveUsersAsAnAuthenticatedUser() throws Exception {
        mockMvc
                // do
                .perform(get(RestApi.WS_SEC_GET_USERS)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void notAuthorizedToCreateUserWithoutAuth() throws Exception {
        // given
        mockMvc
                // do
                .perform(post(RestApi.WS_ADMIN_USER_CREATE).with(csrf()))

                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void forbiddenToCreateUserWithBasicAuthAsANonAdminUser() throws Exception {
        // given
        mockMvc
                // do
                .perform(post(RestApi.WS_ADMIN_USER_CREATE).with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyForUserCreation()))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    public void authorizedToCreateUserWithBasicAuth() throws Exception {
        // given
//        mockMvc
//                // do
//                .perform(
//                        post(RestApi.WS_ADMIN_USER_CREATE).with(httpBasic(GlobalSecurityConfig.SUPERADMIN_USERNAME, AppSecurityRoles.ADMIN_PASSWORD))
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(bodyForUserCreation())
//                )
//                // then
//                .andExpect(status().isForbidden());
    }

    private String bodyForUserCreation() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserCredentialsWS titi = new UserCredentialsWS();
        titi.setUsername("test");
        return mapper.writeValueAsString(titi);
    }
}
