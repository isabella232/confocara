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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * basic controller for Terms-Of-Use
 *
 * note : This webservice should be only called by the Android app
 */
@Controller
public class TermsOfUseController {

    @GetMapping("/termsOfUse")
    public String index() {

        return "termsOfUse";
    }
}
