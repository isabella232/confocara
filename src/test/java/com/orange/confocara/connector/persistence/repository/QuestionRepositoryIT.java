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

package com.orange.confocara.connector.persistence.repository;

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class QuestionRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    private String username1 = "username";
    private String password1 = "1234";

    private String reference = "R1";
    private String label = "label";
    private String state = "complete";
    private String subjectName = "subject1";

    @Test
    public void createQuestion() {
        // given
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);
        final User userDB = new User();
        userDB.setUsername(username1);
        userDB.setPasswordHash( password1);
        userDB.setEmail("email");
        User user = userDB;
        User userFromDB = userRepository.save(user);
        final Subject subject1 = new Subject();
        subject1.setName(subjectName);
        Subject subject = subjectRepository.save(subject1);

        // do
        Question q1 = new Question();
        q1.setReference(reference);
        q1.setLabel(label);
        q1.setState(state);
        q1.setUser(userFromDB);
        q1.setSubject(subject);
        q1.setRulesCategory(rulesCategory);
        questionRepository.save(q1);

        // then
        Question question = questionRepository.findByReference(reference);
        Assertions.assertThat(question.getReference()).isEqualTo(reference);
    }

    @Test
    public void updateQuestion() {

        // given
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        final Subject subject1 = new Subject();
        subject1.setName(subjectName);
        Subject subject = subjectRepository.save(subject1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        // do
        Question question1 = new Question();
        question1.setReference(reference);
        question1.setLabel(label);
        question1.setState(state);
        question1.setUser(userFromDB);
        question1.setRulesCategory(rulesCategory);
        question1.setSubject(subject);
        questionRepository.save(question1);

        // then
        Question question = questionRepository.findByReference(reference);
        question.setLabel("newLabel");
        questionRepository.save(question);

        Assertions.assertThat(question.getReference()).isEqualTo(reference);
    }

    @Test
    public void createQuestionWithSubject() {
        // given
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        final Subject subjectDB = new Subject();
        subjectDB.setName(subjectName);
        Subject subject = subjectRepository.save(subjectDB);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        // do
        Question question = new Question();
        question.setReference(reference);
        question.setLabel(label);
        question.setState(state);
        question.setUser(userFromDB);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);
        questionRepository.save(question);
        Assert.assertNotNull(question);
    }

    @Test
    public void createQuestionWithRulesAndNext() {
        // given
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);
        final Rule ruleDB = new Rule();
        ruleDB.setReference(reference);
        ruleDB.setLabel(label);
        ruleDB.setRulesCategory(rulesCategory);
        ruleDB.setUser(userFromDB);
        Rule rule = ruleRepository.save(ruleDB);
        final Subject subjectDB = new Subject();
        subjectDB.setName(subjectName);
        Subject subject = subjectRepository.save(subjectDB);

        // do
        List<Rule> rules = new ArrayList<>();
        rules.add(rule);

        Question question1 = new Question();
        question1.setReference(reference);
        question1.setLabel(label);
        question1.setState(state);
        question1.setUser(userFromDB);
        question1.setRulesCategory(rulesCategory);
        question1.setSubject(subject);

        Question q2 = new Question();
        q2.setReference("A"+reference);
        q2.setLabel(label);
        q2.setState(state);
        q2.setUser(userFromDB);
        q2.setRulesCategory(rulesCategory);
        q2.setSubject(subject);
        q2.setRules(rules);
        questionRepository.save(question1);
        questionRepository.save(q2);

        // then
        Question question = questionRepository.findBySubject(subject).get(0);
        Assertions.assertThat(question.getSubject()).isEqualTo(subject);
    }

}
