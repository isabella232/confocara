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

package com.orange.confocara.presentation.view.controller;

import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.view.question.list.QuestionListViewController;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    private static final String REDIRECT_LOGIN = "redirect:login";
    private static final String LOGIN = "login";
    private static final String USER = "user";
    private static final String LOGOUT = "logout";

    @GetMapping("/")
    public String log(@ModelAttribute(value = USER) User user) {
        return REDIRECT_LOGIN;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = LOGOUT, required = false) String logout, @ModelAttribute(value = USER) User user,
                        HttpServletRequest request, HttpServletResponse response) {
        if (logout != null) {
            List<String> filterCookies = getListOfFilterCookies();
            for (String filter : filterCookies) {
                Cookie cookie = new Cookie(filter, "");
                cookie.setSecure(true);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }

            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }

        return LOGIN;
    }

    /**
     * Returns the list of cookies' key for filters
     *
     * @return the list of cookies' key for filters
     */
    private List<String> getListOfFilterCookies() {
        List<String> filterCookies = new ArrayList<>();
        filterCookies.add(RulesetController.RULESET_FILTER_COOKIE);
        filterCookies.add(QOController.QUESTIONNAIRE_OBJECT_FILTER_COOKIE);
        filterCookies.add(QuestionListViewController.QUESTION_FILTER_COOKIE);
        filterCookies.add(EquipmentController.OBJECT_CATEGORY_FILTER_COOKIE);
        filterCookies.add(IllustrationController.ILLUSTRATION_FILTER_COOKIE);
        filterCookies.add(RuleController.RULE_LABEL_FILTER_COOKIE);
        filterCookies.add(AccountController.ACCOUNT_FILTER_COOKIE);
        filterCookies.add(ProfileTypeController.PROFILE_TYPE_NAME_FILTER_COOKIE);
        filterCookies.add(RulesCategoryController.RULES_CATEGORY_FILTER_COOKIE);
        filterCookies.add(SubjectController.SUBJECT_FILTER_COOKIE);
        filterCookies.add(CategoryController.CATEGORY_FILTER_COOKIE);
        filterCookies.add(ChainController.CHAIN_FILTER_COOKIE);
        filterCookies.add(ImpactValueController.IMPACT_FILTER_COOKIE);
        filterCookies.add("publishedFilter");
        filterCookies.add("rulesCategoryPropertyFilter");

        return filterCookies;
    }

}
