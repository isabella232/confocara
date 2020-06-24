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

package com.orange.confocara.presentation.webservice.illustration;

import com.orange.confocara.presentation.webservice.RestApi;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webservice dedicated to the reading of questions
 */
@Api(value = "WebServices for the reading of questions")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = RestApi.WS_ROOT + "illustrations")
public class IllustrationWebService {

    private final IllustrationQueryService queryService;

    /**
     * Populates the model with a list of illustrations.
     *
     * @param ruleId an identifier for a rule
     *
     * @return an instance of {@link IllustrationsResponse}
     */
    @GetMapping(value = "/{id:[0-9]+}", produces = "application/json")
    public IllustrationsResponse illustrationsByRule(@PathVariable("id") Long ruleId) {

        log.info("Message=Retrieving a list of illustrations related to a rule;RuleId={};", ruleId);

        return queryService.retrieveAllIllustrations(ruleId);
    }
}
