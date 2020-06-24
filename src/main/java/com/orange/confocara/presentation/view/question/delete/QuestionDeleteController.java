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

package com.orange.confocara.presentation.view.question.delete;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.connector.persistence.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller dedicated to the removal of a {@link Question}
 */
@Controller
public class QuestionDeleteController {

    private static final String REDIRECT_QUESTIONS = "redirect:/questions";

    private static final String ID = "id";

    @Autowired
    private QuestionService questionService;

    @GetMapping(value = "/questions/delete")
    public String deleteQuestion(@RequestParam(value = ID) Long id) {
        questionService.delete(id);

        return REDIRECT_QUESTIONS;
    }
}
