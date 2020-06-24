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

import com.orange.confocara.connector.persistence.model.User;
import org.assertj.core.api.Assertions;
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
public class UserServiceIT {

    @Autowired
    private UserService userService;

    private String username = "userNameTest";


    @Test
    public void createUser() {
        User userTmp = new User();
        userTmp.setUsername(username);
        userTmp.setPasswordHash(username);
        userTmp.setEmail( username+"@" + "b");
        User user = userService.create(userTmp);
        Assertions.assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    public void createUniqueUser() {
        User userTmp = new User();
        userTmp.setUsername(username);
        userTmp.setPasswordHash(username);
        userTmp.setEmail( username+"@" + "c");
        User user = userService.create(userTmp);
        Assertions.assertThat(user.getUsername()).isEqualTo(username);
        Assertions.assertThat(userService.isUsernameAvailable(username)).isFalse();
    }

    @Test
    public void updateUser() {
        String issam = "issam";
        User userTmp = new User();
        userTmp.setUsername(username);
        userTmp.setPasswordHash(username);
        userTmp.setEmail( username+"@" + "e");
        User user = userService.create(userTmp);
        user.setName(issam);
        User update = userService.updateUserWithIconInTx(user, null, null, true);
        Assertions.assertThat(userService.withId(update.getId()).getName()).isEqualTo(issam);

    }

    @Test
    public void delUser() {
        User userTmp = new User();
        userTmp.setUsername(username);
        userTmp.setPasswordHash(username);
        userTmp.setEmail( username+"@" + "f");
        User user = userService.create(userTmp);
        long id = user.getId();
        userService.delete(id);
        Assertions.assertThat(userService.withId(id)).isNull();
    }
}
