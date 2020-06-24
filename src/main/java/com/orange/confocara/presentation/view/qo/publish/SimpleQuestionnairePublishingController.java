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

package com.orange.confocara.presentation.view.qo.publish;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller dedicated to the publishing of a unique questionnaire
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class SimpleQuestionnairePublishingController {

    private static final String REDIRECT_QUESTIONNAIRES = "redirect:/questionnaires";

    private final QuestionnaireObjectPublishingService questionnaireObjectPublishingService;

    @GetMapping(value = "/questionnaires/publish")
    public String publishQuestionnaireObject(@ModelAttribute QuestionnaireObject questionnaireObject) {

        log.info("Message=Requesting the publication of a questionnaire;QuestionnaireId={}", questionnaireObject.getId());
        questionnaireObjectPublishingService.publishOneQuestionnaire(questionnaireObject.getId());

        return REDIRECT_QUESTIONNAIRES;
    }
}
