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

package com.orange.confocara.presentation.view.qo.list;

import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuestionnairesListController {

    private static final String QO = "QO";
    private static final String QOS = "QOs";
    private static final String PUBLISHED_QOS = "publishedQOs";

    private static final String SIZE = "size";
    private static final String USERNAME = "username";

    private static final String RULES_CATEGORIES = "rulesCategories";

    private final RulesCategoryService rulesCategoryService;

    private final QuestionnaireDraftListQueryService questionnaireDraftListService;

    private final QuestionnairePublishedListQueryService publishedListQueryService;

    @GetMapping("/questionnaires")
    public String index(Principal principal, Model model) {

        List<QuestionnaireDtoWrapper> publishedQOs = publishedListQueryService.getPublishedQuestionnairesDtoWrappers();

        List<QuestionnaireDtoWrapper> questionnaireDtos = questionnaireDraftListService.retrieveQuestionnaires();

        model.addAttribute(QOS, questionnaireDtos);
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(PUBLISHED_QOS, publishedQOs);
        model.addAttribute(SIZE, questionnaireDtos.size() + publishedQOs.size());
        model.addAttribute(USERNAME, principal.getName());

        return "questionnaires";
    }

    @ModelAttribute
    public void defaultQuestionnaire(Model model) {
        if (!model.containsAttribute(QO)) {
            QuestionnaireObject questionnaireObject = new QuestionnaireObject();
            questionnaireObject.setReference("");
            model.addAttribute(QO, questionnaireObject);
        }
    }
}
