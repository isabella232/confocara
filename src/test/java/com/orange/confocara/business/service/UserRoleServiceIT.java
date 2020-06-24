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

package com.orange.confocara.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.orange.confocara.connector.persistence.model.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class UserRoleServiceIT {

    @Autowired
    private UserRoleService userRoleService;

    @Test
    public void createUserRole() {
        final UserRole userRoleTmp = new UserRole();
        userRoleTmp.setRole("admin");
        UserRole userRole = userRoleService.create(userRoleTmp);
        UserRole userRoleReturnedByService = userRoleService.findByRole("admin");

        assertThat(userRoleReturnedByService.getRole()).isEqualTo(userRole.getRole());
        assertThat(userRoleReturnedByService.getId() != null).isTrue();
    }
}
