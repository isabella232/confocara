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

package com.orange.confocara.presentation.view.question.edit;

import com.orange.confocara.common.logging.Logged;
import com.orange.confocara.connector.persistence.model.Question;
import java.security.Principal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller dedicated to the editing of {@link Question}s
 */
@RequiredArgsConstructor
@Controller
public class QuestionEditViewController {

    private static final String REDIRECT_QUESTIONS = "redirect:/questions";
    private static final String ID = "id";
    private static final String QUESTION_EDIT_ID_COOKIE = "questionEditCookieId";
    private static final String EDIT_QUESTION = "editQuestion";

    private final QuestionEditService questionEditService;

    @GetMapping(value = "/questions/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "helpers/editQuestionHelper";
    }

    @Logged
    @GetMapping("/questions/edit")
    public String viewQuestion(
            Principal principal,
            @Param(value = ID) Long id,
            @CookieValue(value = QUESTION_EDIT_ID_COOKIE, required = false) String idCookie,
            Model model,
            HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(QUESTION_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long questionId = id != null ? id : Long.parseLong(idCookie);

        questionEditService.loadQuestion(questionId, principal, model);

        return EDIT_QUESTION;
    }

    @Logged
    @RequestMapping(value = "/questions/update", method = RequestMethod.POST)
    public String saveQuestion(
            @ModelAttribute QuestionEditDto question,
            @RequestParam Long id,
            Principal principal) {

        questionEditService.saveQuestion(id, question, principal);

        return REDIRECT_QUESTIONS;
    }
}
