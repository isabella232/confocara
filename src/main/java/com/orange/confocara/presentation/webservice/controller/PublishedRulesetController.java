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

package com.orange.confocara.presentation.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.confocara.business.service.PublishedRulesetService;
import com.orange.confocara.business.service.RulesetRevisionPublishingService;
import com.orange.confocara.presentation.webservice.model.RuleSetExport;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webservice dedicated to the reading of published rulesets
 */
@RestController
@Slf4j
@RequestMapping(path = "/ws")
public class PublishedRulesetController {

    @Autowired
    private PublishedRulesetService publishedRulesetService;

    @Autowired
    private RulesetRevisionPublishingService rulesetRevisionPublishingService;

    /**
     * retrieves data about a ruleset that was previously published, based on its reference
     * and version.
     *
     * @param ref     the reference of a published {@link com.orange.confocara.connector.persistence.model.Ruleset}
     * @param version the version number of a published {@link com.orange.confocara.connector.persistence.model.Ruleset}
     * @return informations about the ruleset
     */
    @PostMapping(value = "/findruleset", produces = "application/json")
    public RuleSetExport retrievePublishedRuleset(@RequestParam String ref, @RequestParam Integer version)
            throws IOException {

        log.info("Message=Requesting for a published ruleset;RulesetReference={};RulesetVersion={}", ref, version);
        return new ObjectMapper().readValue(publishedRulesetService.retrieveRuleset(ref, version).getContent(), RuleSetExport.class);
    }

    /**
     * creates a published ruleset, based on its reference and version, if it does not exist yet.
     *
     * @param ref     the reference of a {@link com.orange.confocara.connector.persistence.model.Ruleset}
     * @param version the version number of a {@link com.orange.confocara.connector.persistence.model.Ruleset}
     */
    @PutMapping("/publishing")
    public void makePublishedRuleset(@RequestParam String ref, @RequestParam Integer version) {

        log.info("Message=Creating a published ruleset;RulesetReference={};RulesetVersion={}", ref, version);
        rulesetRevisionPublishingService.generatePublishedRuleset(ref, version);
    }
}
