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

package com.orange.confocara.presentation.view.qo.view;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller dedicated to display a {@link QuestionnaireObject} for viewing only
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestionnaireObjectViewController {

    private static final String REFERENCE = "r";

    private static final String VERSION = "v";

    /**
     * the name of the target template
     */
    private static final String VIEW_QUESTIONNAIRE = "viewQuestionnaire";

    /**
     * the service that supplies with {@link QuestionnaireObject}s
     */
    private final QuestionnaireObjectQueryService<QuestionnaireViewDto> queryService;

    @GetMapping("/questionnaires/view")
    public String viewQuestionnaire(Principal principal, @RequestParam(value = REFERENCE) String reference,
            @RequestParam(value = VERSION, defaultValue = "CURRENT") Integer version, Model model) {

        log.info(
                "CtrlMessage=Requesting the view of a questionnaire;QuestionnaireId={};QuestionnaireVersion={};CurrentUser={};",
                reference, version, principal.getName());

        QuestionnaireViewDto qo = queryService.retrieveOneQuestionnaire(reference, version);
        model.addAttribute("qo", qo);

        return VIEW_QUESTIONNAIRE;
    }
}
