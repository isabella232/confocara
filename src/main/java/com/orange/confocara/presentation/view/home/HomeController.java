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

package com.orange.confocara.presentation.view.home;

import com.orange.confocara.connector.persistence.model.utils.Role;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private static final String HOME = "home";
    private static final String USERNAME = "username";
    private static final String USERS = "users";
    private static final String AUTHENTICATED_USERNAME = "authenticatedUsername";
    private static final String IS_ADMIN = "isAdmin";
    private static final String IS_AUTHENTICATED = "isAuthenticated";
    private static final String EQUIPMENTS = "equipments";

    private final EquipmentInfoService equipmentInfoService;

    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        setAuthenticated(principal, model);

        String name = "";
        if (principal != null) {
            name = principal.getName();
        }
        model.addAttribute(USERNAME, name);
        model.addAttribute(EQUIPMENTS, equipmentInfoService.all());

        return HOME;
    }

    @GetMapping("/users")
    public String showUsers(Model model, Principal principal) {
        setAuthenticated(true, model);
        model.addAttribute(USERNAME, principal.getName());
        return USERS;
    }

    private void setAuthenticated(Principal principal, Model model) {
        boolean authenticated = principal != null && principal.getName() != null;
        setAuthenticated(authenticated, model);
        model.addAttribute(AUTHENTICATED_USERNAME,
                authenticated ? principal.getName() : "Please sign in");
        model.addAttribute(IS_ADMIN, authenticated && isAdmin(principal));
    }

    private void setAuthenticated(boolean isAuthenticated, Model model) {
        model.addAttribute(IS_AUTHENTICATED, isAuthenticated);
    }

    private boolean isAdmin(Principal principal) {
        return ((UsernamePasswordAuthenticationToken) principal).getAuthorities().stream()
                .filter(authority -> authority.getAuthority().equals(Role.ROLE_ADMIN.toString())
                        || authority.getAuthority().equals(Role.ROLE_SUPERADMIN.toString()))
                .count() > 0;
    }
}
