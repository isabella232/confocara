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

import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import com.orange.confocara.presentation.view.util.CookieUtils;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChainController {

    public static final String CHAIN_FILTER_COOKIE = "chainFilter";

    private static final String CHAIN = "chain";
    private static final String CHAINS = "chains";
    private static final String RULES_CATEGORIES = "rulesCategories";
    private static final String USERNAME = "username";
    private static final String REDIRECT_CHAINS = "redirect:/chains";
    private static final String QUESTIONS = "questions";
    private static final String QUESTION = "question";
    private static final String ADD_CHAIN = "addChain";
    private static final String ID = "id";

    @Autowired
    private QuestionService questionService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private UserService userService;
    @Autowired
    private RulesCategoryService rulesCategoryService;

    @GetMapping("/chains")
    public String index(Principal principal, Model model) {
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(CHAINS, chainService.all());
        model.addAttribute(USERNAME, principal.getName());

        return CHAINS;
    }

    @GetMapping(value = "/chains/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/chainsHelper";
    }

    @GetMapping(value = "/chains/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addChainHelper";
    }

    @GetMapping(value = "/chains/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editChainHelper";
    }

    @GetMapping("/chains/add")
    public String addChain(Principal principal, Model model,
                           @CookieValue(value = RuleController.SELECTED_RULES_CATEGORY_COOKIE, required = false) String selectedRulesCategory) {
        Chain chain = new Chain();
        model.addAttribute(CHAIN, chain);

        model.addAttribute(QUESTIONS, questionService.all());

        Question question = new Question();

        model.addAttribute(QUESTION, question);
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        if (selectedRulesCategory != null) {
            model.addAttribute(RuleController.SELECTED_RULES_CATEGORY, selectedRulesCategory);
        }

        return ADD_CHAIN;
    }

    @RequestMapping(value = "/chains/create", method = RequestMethod.POST)
    public String createChain(@ModelAttribute Chain chain, Principal principal, HttpServletResponse response) {
        if (chain.getOrderedQuestionIds() != null) {
            List<Question> questions = questionService.sortedWithIds(GenericUtils.convertToLongList(chain.getOrderedQuestionIds()));
            chain.setQuestions(questions);
        }

        if (chain.getRulesCategory() != null) {
            RulesCategory rulesCategory = rulesCategoryService.withId(chain.getRulesCategory().getId());
            chain.setRulesCategory(rulesCategory);

            CookieUtils.decorateResponse(response, rulesCategory);
        }

        chain.setDate(new Date());
        chain.setReference("");
        chain.setUser(userService.getUserByUsername(principal.getName()));

        chain.setListPositionAndQuestionRefMap();
        chainService.create(chain);
        return REDIRECT_CHAINS;
    }

    @RequestMapping(value = "/chains/update", method = RequestMethod.POST)
    public String updateChain(@ModelAttribute Chain chain, @RequestParam Long id, Principal principal) {
        List<Question> questions = new ArrayList<>();

        if (chain.getOrderedQuestionIds() != null) {
            questions = questionService.sortedWithIds(GenericUtils.convertToLongList(chain.getOrderedQuestionIds()));
        }

        chain.setId(id);
        chain.setQuestions(questions);
        chain.setRulesCategory(chainService.withId(id).getRulesCategory());
        chain.setDate(new Date());
        chain.setUser(userService.getUserByUsername(principal.getName()));
        chain.setListPositionAndQuestionRefMap();
        chainService.update(chain);

        return REDIRECT_CHAINS;
    }

    @RequestMapping(value = "/chains/delete", method = RequestMethod.GET)
    public String deleteChain(@RequestParam(value = ID) Long id) {
        chainService.delete(id);

        return REDIRECT_CHAINS;
    }
}
