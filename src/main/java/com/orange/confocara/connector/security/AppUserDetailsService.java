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

import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userDetailsService")
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        List<GrantedAuthority> authorities;
        User user = userRepository.findByUsername(username);
        check(user, username);

        authorities = AppSecurityRoles.authoritiesFor(user.getUserRoles());

        return toSpringSecurityUser(user, authorities);
    }

    public String check(User usersDB, String username) {
        String error = "";
        if (usersDB == null) {
            throw getUserNotFoundRuntimeException(username);
        }

        return error;
    }

    private RuntimeException getUserNotFoundRuntimeException(String username) {
        String error = String.format("user '%s' not found", username);
        RuntimeException runtimeException = new UsernameNotFoundException(
                error);
        log.warn(runtimeException.getMessage(), runtimeException);
        return runtimeException;
    }

    private org.springframework.security.core.userdetails.User toSpringSecurityUser(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), true, true, true, true, authorities);
    }
}
