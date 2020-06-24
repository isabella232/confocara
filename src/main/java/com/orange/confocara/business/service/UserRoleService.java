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

import com.orange.confocara.connector.persistence.model.UserRole;
import com.orange.confocara.connector.persistence.model.utils.Role;
import com.orange.confocara.connector.persistence.repository.UserRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Transactional
    public Set<UserRole> all() {
        ArrayList<UserRole> allRoles = (ArrayList<UserRole>) userRoleRepository.findAll();
        return new HashSet<>(allRoles);
    }

    @Transactional
    public UserRole withId(long id) {
        return userRoleRepository.findOne(id);
    }

    @Transactional
    public UserRole findByRole(@NonNull String role) {
        return userRoleRepository.findByRole(role);
    }

    /**
     * Gets the user role list that the user having "role" can create or edit
     *
     * @param role the session user higher role
     * @return the user role list that the session user can create or edit
     */
    public List<UserRole> findManagedRolesWithUserRole(@NonNull String role) {
        List<UserRole> managedRoles = new ArrayList<>();
        if (role.equals(Role.ROLE_SUPERADMIN.toString())) {
            return new ArrayList<>(all());
        } else if (role.equals(Role.ROLE_ADMIN.toString())) {
            managedRoles.add(findByRole(Role.ROLE_USER.toString()));
            return managedRoles;
        } else {
            // empty list
            return managedRoles;
        }
    }

    @Transactional
    public Set<UserRole> withIds(List<Long> ids) {
        ArrayList<UserRole> allRoles = (ArrayList<UserRole>) userRoleRepository.findAll(ids);

        return new HashSet<>(allRoles);
    }

    @Transactional
    public UserRole create(@NonNull UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

}
