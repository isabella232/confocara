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

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.IllustrationRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class QuestionServiceIT {

    @Autowired
    UserService userService;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    RuleService ruleService;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    IllustrationService illustrationService;

    @Autowired
    IllustrationRepository illustrationRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ImpactValueService impactValueService;

    @Autowired
    RulesCategoryService rulesCategoryService;

    private final String username = "username";
    private final String reference = "reference";
    private final String label = "label";

    @Test
    public void createQuestion() {
        User user = createUser(username);
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        Question question = initQuestionValues();
        question.setRulesCategory(rulesCategory);
        question.setUser(user);
        question.setSubject(subject);

        Question question1 = questionService.create(question);
        Question question2 = questionService.withReference(reference);

        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
        Assertions.assertThat(question2.getReference()).isEqualTo(reference);

        Assertions.assertThat(question1.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void delQuestion() {
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        Question question = initQuestionValues();
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        Question question1 = questionService.create(question);
        questionService.delete(question1.getId());

        Assertions.assertThat(questionService.all()).isEmpty();
    }

    @Test
    public void updateQuestion() {
        String newLabel = "test";
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        User user = createUser(username);
        Subject subject = createSubject();

        Question question = initQuestionValues();
        question.setUser(user);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        Question question1 = questionService.create(question);
        question1.setLabel(newLabel);
        Question update = questionService.update(question1);

        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
        Assertions.assertThat(update.getReference()).isEqualTo(reference);
        Assertions.assertThat(update.getLabel()).isEqualTo(newLabel);
        Assertions.assertThat(question1.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void createQuestionWithNextQ() {
        User user = createUser(username);
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        Question question = initQuestionValues();
        question.setUser(user);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        Question question1 = questionService.create(question);

        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
    }

    @Test
    @Transactional
    public void createQuestionWithRules() {
        User user = createUser(username);
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        Question question = initQuestionValues();
        question.setUser(user);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        final Rule rule = new Rule();
        rule.setReference(reference);
        rule.setLabel(label);
        rule.setRulesCategory(rulesCategory);
        rule.setUser(user);
        ruleRepository.save(rule);
        List<Rule> rules = (List<Rule>) ruleRepository.findAll();
        question.setRules(rules);

        Question question1 = questionService.create(question);

        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
    }

    @Test
    @Transactional
    public void updateQuestionWithRules() {
        User user = createUser(username);
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        Question question = initQuestionValues();
        question.setUser(user);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        final Rule rule = new Rule();
        rule.setReference(reference + "1");
        rule.setLabel(label);
        rule.setUser(user);
        rule.setRulesCategory(rulesCategory);
        ruleRepository.save(rule);
        List<Rule> rules = (List<Rule>) ruleRepository.findAll();

        question.setRules(rules);
        Question question1 = questionService.create(question);

        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
        Assertions.assertThat(question1.getUser().getId()).isEqualTo(user.getId());

    }

    @Test
    @Transactional
    public void createQuestionWithDeletedRules() {
        User user = createUser(username);
        Subject subject = createSubject();
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        final Rule ruleDB = new Rule();
        ruleDB.setReference(reference);
        ruleDB.setLabel(label);
        ruleDB.setUser(user);
        ruleDB.setRulesCategory(rulesCategory);
        Rule rule = ruleRepository.save(ruleDB);
        List<Rule> rules = (List<Rule>) ruleRepository.findAll();

        Question question = initQuestionValues();
        question.setUser(user);
        question.setRulesCategory(rulesCategory);
        question.setSubject(subject);
        question.setRules(rules);

        Question question1 = questionService.create(question);

        ruleService.delete(rule.getId());
        Assertions.assertThat(question1).isNotNull();
        Assertions.assertThat(question1.getReference()).isEqualTo(reference);
    }

    private Subject createSubject() {
        Subject subject = new Subject();
        subject.setName("subject");
        return subjectService.create(subject);
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("1234");
        user.setEmail("myAdress@orange.com");
        return userService.create(user);

    }

    private Question initQuestionValues() {
        String state = "active";

        Question question = new Question();
        question.setReference(reference);
        question.setLabel(label);
        question.setState(state);

        return question;
    }

}
