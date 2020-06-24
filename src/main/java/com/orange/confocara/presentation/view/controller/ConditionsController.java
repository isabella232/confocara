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

package com.orange.confocara.presentation.view.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConditionsController {

    private static final String USERNAME = "username";
    private static final String CONDITIONS = "conditions";

    @GetMapping("/conditions")
    public String index(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute(USERNAME, principal.getName());
        }

        return CONDITIONS;
    }
}
