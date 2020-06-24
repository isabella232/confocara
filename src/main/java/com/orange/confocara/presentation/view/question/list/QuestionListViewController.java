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

package com.orange.confocara.presentation.view.question.list;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import java.security.Principal;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class QuestionListViewController {
    public static final String QUESTION_FILTER_COOKIE = "questionFilter";
    private static final String QUESTIONS = "questions";
    private static final String RULES_CATEGORIES = "rulesCategories";
    private static final String USERNAME = "username";

    private final QuestionService questionService;

    private final RulesCategoryService rulesCategoryService;

    @GetMapping("/questions")
    public String index(Principal principal, Model model) {
        model.addAttribute(QUESTIONS, questionService
                .all()
                .stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList()));
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        return QUESTIONS;
    }

    @GetMapping(value = "/questions/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/questionsHelper";
    }
}
