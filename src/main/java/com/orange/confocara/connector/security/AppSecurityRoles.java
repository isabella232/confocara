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

import com.orange.confocara.connector.persistence.model.UserRole;
import com.orange.confocara.connector.persistence.model.utils.Role;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import com.orange.confocara.connector.persistence.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppSecurityRoles {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    static List<GrantedAuthority> authorities() {
        return Arrays.stream(Role.values())
                .map(userRole -> new SimpleGrantedAuthority(userRole.toString()))
                .collect(Collectors.toList());
    }

    static List<GrantedAuthority> authoritiesFor(Set<UserRole> roles) {
        return roles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()))
                .collect(Collectors.toList());
    }

    @PostConstruct
    void init() {
        boolean dbNotInitialised = userRoleRepository.count() == 0;
        if (dbNotInitialised) {
            createAndPersistRoles();
        }
    }

    private Iterable<UserRole> createAndPersistRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("Add all roles");
            return userRoleRepository.save(allRoles());
        }
        return userRoleRepository.findAll();
    }

    private Set<UserRole> allRoles() {
        Set<UserRole> set = new HashSet<>();
        Collections.addAll(set, new UserRole(Role.ROLE_USER), new UserRole(Role.ROLE_ADMIN), new UserRole(Role.ROLE_SUPERADMIN));
        return set;
    }

}
