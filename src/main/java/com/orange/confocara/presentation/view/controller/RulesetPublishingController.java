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

import com.orange.confocara.business.service.RulesetPublishingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Webservice dedicated to the publication of rulesets
 */
@Slf4j
@Controller
@RequestMapping(path = "/rulesets/publish")
public class RulesetPublishingController {

    @Autowired
    private RulesetPublishingService rulesetPublishingService;

    @GetMapping
    public String publishRuleset(@RequestParam("id") long rulesetId, RedirectAttributes redirectAttributes) {
        log.info("Message=Start publishing a ruleset; RulesetId={}", rulesetId);

        try {
            rulesetPublishingService.publishRuleset(rulesetId);
        } catch (Exception ex) {
            log.error("ErrorMessage=Publication failed - ", ex);
            // error state : no associated questionnaire
            redirectAttributes.addFlashAttribute("err_publish_ruleset", "");
            redirectAttributes.addFlashAttribute("err_publish_empty_ruleset", "");
        }

        return "redirect:/rulesets";
    }
}
