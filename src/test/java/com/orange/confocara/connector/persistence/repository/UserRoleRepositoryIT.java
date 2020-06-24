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

package com.orange.confocara.connector.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.orange.confocara.connector.persistence.model.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRoleRepositoryIT {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    public void createUserRole() {
        final UserRole userRole = new UserRole();
        userRole.setRole("admin");
        userRoleRepository.save(userRole);
        UserRole userRole2 = userRoleRepository.findByRole("admin");

        assertThat(userRole2.getRole()).isEqualTo("admin");
        assertThat(userRole2.getId() != null).isTrue();
    }
}
