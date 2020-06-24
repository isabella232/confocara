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

package com.orange.confocara.presentation.view.chain;

import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import java.security.Principal;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChainEditViewController {

    private static final String CHAIN = "chain";
    private static final String CHAIN_EDIT_COOKIE_ID = "chainCookieId";
    private static final String EDIT_CHAIN = "editChain";
    private static final String ID = "id";
    private static final String QUESTIONS = "questions";
    private static final String QUESTION = "question";
    private static final String USERNAME = "username";

    private final QuestionService questionService;

    private final ChainService chainService;

    @GetMapping("/chains/edit")
    public String editChain(Principal principal,
            @Param(value = ID) Long id,
            @CookieValue(value = CHAIN_EDIT_COOKIE_ID, required = false) String idCookie,
            Model model,
            HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(CHAIN_EDIT_COOKIE_ID, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        Chain chain = chainService.withId(idFinal);
        chain.setQuestionIdsFromQuestions();
        model.addAttribute(CHAIN, new ChainDto(chain));

        model.addAttribute(QUESTIONS, questionService.all()
                .stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList()));

        model.addAttribute(QUESTION, new Question());

        model.addAttribute(ID, idFinal);
        model.addAttribute(USERNAME, principal.getName());

        return EDIT_CHAIN;
    }

}
