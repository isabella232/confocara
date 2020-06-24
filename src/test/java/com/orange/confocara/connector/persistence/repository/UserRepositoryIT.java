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

import com.orange.confocara.connector.persistence.model.User;
import java.io.IOException;
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
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private String username1 = "Duchess";
    private String password1 = "1234";

    private String username2 = "Thomas";
    private String password2 = "4321";

    @Test
    public void returnAllPersisted() throws IOException {
        // given
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        userRepository.save(user);
        final User userDB2 = new User();
        userDB2.setUsername(username2);
        userDB2.setPasswordHash( password2);
        userDB2.setEmail("email@e");
        userRepository.save(userDB2);
        // do
        Iterable<User> users = userRepository.findAll();
        User user1 = userRepository.findByUsername(username1);
        User user2 = userRepository.findByUsername(username2);
        // then
        assertThat(users).hasSize(2);
        assertThat(users).contains(user1);
        assertThat(users).contains(user2);

        assertThat(user1.getUsername()).isEqualTo(username1);
        assertThat(user1.getPasswordHash()).isEqualTo(password1);

        assertThat(user2.getUsername()).isEqualTo(username2);
        assertThat(user2.getPasswordHash()).isEqualTo(password2);
    }

    @Test
    public void createUser() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        userRepository.save(user);
        User user1 = userRepository.findByUsername(username1);

        assertThat(user1.getUsername()).isEqualTo(username1);
        assertThat(user1.getPasswordHash()).isEqualTo(password1);
    }

    @Test
    public void updateUser() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        userRepository.save(user);
        User user1 = userRepository.findByUsername(username1);
        user1.setUsername("issam");
        User user2 = userRepository.save(user1);

        assertThat(user2.getUsername()).isEqualTo("issam");
        assertThat(user2.getPasswordHash()).isEqualTo(password1);
    }

}
