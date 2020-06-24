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

import static java.util.stream.Collectors.toList;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RuleService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

/**
 * Behaviour of a service that handles the displaying of an edit form for an existing question and
 * its saving
 */
public interface QuestionEditService {

    /**
     * @param id an identifier for a {@link Question}
     * @param currentUser a {@link Principal}
     * @param model a view to populate
     */
    void loadQuestion(long id, Principal currentUser, Model model);

    /**
     * @param id an identifier for a {@link Question}
     * @param content a Data-Transfer Object
     * @param updateAuthor a {@link Principal}
     */
    void saveQuestion(Long id, QuestionEditDto content, Principal updateAuthor);

    static QuestionEditService instance(UserService userService, QuestionService questionService,
            SubjectService subjectService, QuestionRulesQueryService questionRulesQueryService, RuleService ruleService) {
        return new QuestionEditServiceImpl(userService, questionService, subjectService,
                questionRulesQueryService, ruleService);
    }

    /**
     * default implementation of {@link QuestionEditService}
     */
    @Transactional(readOnly = true)
    @RequiredArgsConstructor
    @Slf4j
    class QuestionEditServiceImpl implements QuestionEditService {

        private final UserService userService;

        private final QuestionService questionService;

        private final SubjectService subjectService;

        private final QuestionRulesQueryService questionRulesQueryService;

        private final RuleService ruleService;

        private static final String QUESTION = "question";
        private static final String USERNAME = "username";
        private static final String SUBJECTS = "subjects";
        private static final String RULES = "rules";
        private static final String RULE = "rule";
        private static final String ASSOCIATEDRULES = "associatedrules";
        private static final String ORDEREDRULES = "orderedRuleIds";
        private static final String ID = "id";


        @Override
        public void loadQuestion(long id, Principal currentUser, Model model) {

            Question entity = questionService.withId(id);

            List<String> ruleIds = entity.hasRules()
                    ? entity.getRules().stream().map(Rule::getReference).collect(toList())
                    : Collections.emptyList();

            List<String> orderedRulesIds = entity.hasRulesOrder() ? Arrays.asList(entity.getRulesOrder().split(",")) : ruleIds;

            model.addAttribute(QUESTION, new QuestionEditDto(entity));
            model.addAttribute(SUBJECTS, subjectService.all());
            model.addAttribute(RULES, questionRulesQueryService.allRules());
            model.addAttribute(RULE, new Rule());
            model.addAttribute(USERNAME, currentUser.getName());
            model.addAttribute(ASSOCIATEDRULES, ruleIds);
            model.addAttribute(ORDEREDRULES, orderedRulesIds);
            model.addAttribute(ID, id);
        }

        @Transactional
        @Override
        public void saveQuestion(Long id, QuestionEditDto content, Principal updateAuthor) {
            Question entity = questionService.withId(id);

            Subject initialSubject = entity.getSubject();
            if (content.getSubject() == null) {
                // should never happen...
                entity.setSubject(null);
            } else if (initialSubject == null || initialSubject.getName().compareTo(content.getSubject().getName()) != 0) {
                Subject actualSubject = subjectService
                        .getSubjectByName(content.getSubject().getName());
                entity.setSubject(actualSubject);
            }

            if (content.getOrderedRuleIds() != null && !content.getOrderedRuleIds().isEmpty()) {
                List<Rule> initialRules = entity.getRules() != null ? entity.getRules() : Collections
                        .emptyList();

                // removing deleted rules
                List<Rule> actualRules = content
                        .getOrderedRuleIds()
                        .stream()
                        .map(questionRulesQueryService::retrieveOneRuleByReference)
                        .filter(Objects::nonNull)
                        .collect(toList());

                log.info("Message=Saving ordered rules;RuleIds={};RulesCount={}", content.getOrderedRuleIds(), actualRules.size());

                initialRules
                        .removeIf(rule -> !actualRules.contains(rule));

                // adding new rules
                actualRules
                        .stream()
                        .filter(rule -> {
                            log.info(
                                    "Message=Checking if list contains element;QuestionId={};ItemIndex={};ItemId={}",
                                    entity.getId(), initialRules.indexOf(rule), rule.getReference());
                            return !initialRules.contains(rule);
                        })
                        .peek(entity::addRule)
                        .forEach(ruleService::update);

                // adding sorting metada
                log.info(
                        "Message=Setting rules'order;QuestionId={};Order={}",
                        entity.getId(), content.getOrderedRuleIds());

                entity.setRulesOrder(content.getOrderedRuleIds());

                // setting the new state
                entity.setState(State.ACTIVE.toString().toLowerCase());
            } else {
                log.info("Message=No rules to save;");
                entity.resetRules();
            }

            entity.setLabel(content.getLabel());
            entity.setDate(new Date());
            entity.setUser(userService.getUserByUsername(updateAuthor.getName()));

            questionService.update(entity);
        }
    }
}
