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

package com.orange.confocara.presentation.view.question.create;


import com.orange.confocara.common.logging.Logged;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.presentation.view.util.CookieUtils;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller dedicated to the creation of {@link Question}s
 */
@RequiredArgsConstructor
@Controller
public class QuestionAddViewController {

    private static final String REDIRECT_QUESTIONS = "redirect:/questions";
    private static final String SELECTED_RULES_CATEGORY_COOKIE = "selectedRulesCategory";
    private static final String ADD_QUESTION = "addQuestion";

    private final QuestionAddService questionAddService;

    @GetMapping(value = "/questions/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "helpers/addQuestionHelper";
    }

    @Logged
    @GetMapping("/questions/add")
    public String viewQuestion(
            Principal principal,
            Model model,
            @CookieValue(value = SELECTED_RULES_CATEGORY_COOKIE, required = false) String selectedCategory) {

        questionAddService.loadQuestion(selectedCategory, principal, model);

        return ADD_QUESTION;
    }

    @Logged
    @RequestMapping(value = "/questions/create", method = RequestMethod.POST)
    public String saveQuestion(
            @ModelAttribute QuestionCreateDto question,
            Principal principal,
            HttpServletResponse response) {

        questionAddService.saveQuestion(question, principal);

        CookieUtils.decorateResponse(response, question.getRulesCategory());

        return REDIRECT_QUESTIONS;
    }
}
