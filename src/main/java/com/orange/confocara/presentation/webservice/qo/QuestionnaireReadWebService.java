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

package com.orange.confocara.presentation.webservice.qo;

import com.orange.confocara.presentation.webservice.RestApi;
import com.orange.confocara.presentation.webservice.qo.QuestionnaireReadQueryService.QuestionnaireResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webservice dedicated to the reading of questions
 */
@Api(value = "WebServices for the reading of questions")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = RestApi.WS_ROOT + "qo")
public class QuestionnaireReadWebService {

    private final QuestionnaireReadQueryService queryService;

    /**
     * retrieves a {@link QuestionnaireResponse}
     *
     * @param reference an identifier for a questionnaire
     * @param version a revision number for a questionnaire
     *
     * @return a lot of information
     */
    @ApiOperation(value = "retrieves a bunch of rules related to a questionnaire")
    @ApiResponses(value = {
        @ApiResponse(
                code = 200,
                message = "Questionnaire retrieval is successful",
                response = QuestionnaireResponse.class)}
    )
    @GetMapping(value = "/{reference:QO[1-9][0-9]*}/{version:[0-9]+}", produces = "application/json")
    @ResponseBody
    public QuestionnaireResponse rulesByQuestionnaire(
            @PathVariable("reference") String reference,
            @PathVariable("version") Integer version) {

        log.info(
                "Message=Retrieving a bunch of rules related to a questionnaire;QuestionnaireReference={};QuestionnaireVersion={};",
                reference, version);

        return queryService.retrieveAllRules(ImmutableQuestionnaireRequest
                .builder()
                .reference(reference)
                .version(version)
                .build());
    }
}
