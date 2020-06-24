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

package com.orange.confocara.presentation.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orange.confocara.connector.persistence.model.User;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCredentialsWS {
    private String username;
    private String password;
    private String email;

    public static UserCredentialsWS newInstance(User user) {
        final UserCredentialsWS userCredentials = new UserCredentialsWS();
        if (user != null) {
            userCredentials.setUsername(user.getUsername());
        }
        return userCredentials;
    }
}
