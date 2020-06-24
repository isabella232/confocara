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

import static com.google.common.collect.Lists.newArrayList;
import static com.orange.confocara.connector.persistence.model.Question.newEntity;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.presentation.view.controller.RuleController;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

/**
 * Behaviour of a service that handles the displaying of an edit form for an existing question and
 * its saving
 */
public interface QuestionAddService {

    /**
     * @param selectedRulesCategory an identifier for a {@link RulesCategory}
     * @param currentUser a {@link Principal}
     * @param model a list to populate
     */
    void loadQuestion(String selectedRulesCategory, Principal currentUser, Model model);

    /**
     * @param question a Data-Transfer Object
     * @param updateAuthor a {@link Principal}
     */
    void saveQuestion(QuestionCreateDto question, Principal updateAuthor);

    static QuestionAddService instance(UserService userService, QuestionService questionService,
            SubjectService subjectService, QuestionRulesQueryService questionRulesQueryService,
            RulesCategoryService rulesCategoryService) {
        return new QuestionAddServiceImpl(userService, questionService, subjectService,
                questionRulesQueryService, rulesCategoryService);
    }

    /**
     * default implementation of {@link QuestionAddService}
     */
    @Transactional(readOnly = true)
    @RequiredArgsConstructor
    class QuestionAddServiceImpl implements QuestionAddService {

        private final UserService userService;

        private final QuestionService questionService;

        private final SubjectService subjectService;

        private final QuestionRulesQueryService questionRulesQueryService;

        private final RulesCategoryService rulesCategoryService;

        private static final String QUESTION = "question";
        private static final String USERNAME = "username";
        private static final String SUBJECTS = "subjects";
        private static final String RULES = "rules";
        private static final String RULE = "rule";
        private static final String RULES_CATEGORIES = "rulesCategories";
        private static final String ASSOCIATEDRULES = "associatedrules";
        private static final String ORDEREDRULES = "orderedRuleIds";

        @Override
        public void loadQuestion(String selectedRulesCategory, Principal currentUser, Model model) {

            model.addAttribute(QUESTION, new QuestionCreateDto());

            model.addAttribute(SUBJECTS, subjectService.all());
            model.addAttribute(RULES, questionRulesQueryService.allRules());
            model.addAttribute(RULE, new Rule());
            model.addAttribute(USERNAME, currentUser.getName());
            model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
            model.addAttribute(ORDEREDRULES, newArrayList());
            model.addAttribute(ASSOCIATEDRULES, newArrayList());

            if (selectedRulesCategory != null) {
                model.addAttribute(RuleController.SELECTED_RULES_CATEGORY, selectedRulesCategory);
            }
        }

        @Transactional
        @Override
        public void saveQuestion(QuestionCreateDto content, Principal updateAuthor) {

            Question entity = newEntity();
            entity.setLabel(content.getLabel());

            Subject subject = null;
            if (content.getSubject() != null) {
                subject = subjectService.withId(content.getSubject().getId());
            }
            entity.setSubject(subject);

            RulesCategory rulesCategory = null;
            if (content.getRulesCategory() != null) {
                rulesCategory = rulesCategoryService.withId(content.getRulesCategory().getId());
            }
            entity.setRulesCategory(rulesCategory);

            List<Rule> rules = newArrayList();
            if (content.getOrderedRuleIds() != null) {
                rules = content
                        .getOrderedRuleIds()
                        .stream()
                        .map(questionRulesQueryService::retrieveOneRuleByReference)
                        .collect(Collectors.toList());
            }
            entity.setRules(rules);
            entity.setRulesOrder(content.getOrderedRuleIds());

            entity.setUser(userService.getUserByUsername(updateAuthor.getName()));

            questionService.create(entity);
        }
    }
}
