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

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
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
public class ChainServiceIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChainRepository chainRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    ChainService chainService;

    @Autowired
    ImpactValueService impactValueService;

    @Autowired
    RulesCategoryService rulesCategoryService;

    @Test
    public void createChain() {
        // given
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        String reference = "R6";
        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName("ch");

        // do
        Chain chain1 = chainService.create(chain);

        // then
        Assertions.assertThat(chain1).isNotNull();
        Assertions.assertThat(chain1.getReference()).isEqualTo(reference);
    }

    @Test
    public void updateChain() {
        // given
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        String reference = "R5";
        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName("ch");

        // do
        Chain chain1 = chainService.create(chain);
        Chain ch = new Chain();
        ch.setId(chain1.getId());
        ch.setRulesCategory(rulesCategory);
        ch.setReference(reference + "2");
        ch.setName("ch");
        ch.setDate(new Date());
        Chain chain2 = chainService.update(ch);

        // then
        Assertions.assertThat(chain2).isNotNull();
        Assertions.assertThat(chain2.getReference()).isEqualTo(reference + "2");
    }

    @Test
    public void createChainWithQs() {
        // given
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username + "3");
        user1.setPasswordHash("aaa");
        user1.setEmail("email@em");
        User user = userRepository.save(user1);
        final Subject subject1 = new Subject();
        subject1.setName("subject1");
        Subject subject = subjectRepository.save(subject1);
        String reference = "R1";

        Question question = new Question();
        question.setSubject(subject);
        question.setUser(user);
        question.setReference("ref");
        question.setRulesCategory(rulesCategory);
        question.setLabel("label");
        question.setState("state");
        Question question1 = questionService.create(question);

        List<Question> questions = new ArrayList<>();
        questions.add(question1);

        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName("ch");
        chain.setQuestions(questions);
        Chain chain1 = chainService.create(chain);

        // then
        Assertions.assertThat(chain1).isNotNull();
        Assertions.assertThat(chain1.getReference()).isEqualTo(reference);
    }

    @Test
    public void updateChainwithQs() {
        // given
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username + "4");
        user1.setPasswordHash("password");
        user1.setEmail("e");
        User user = userRepository.save(user1);
        final Subject subject1 = new Subject();
        subject1.setName("subject1");
        Subject subject = subjectRepository.save(subject1);
        String reference = "R3";

        Question question = new Question();
        question.setSubject(subject);
        question.setUser(user);
        question.setReference("ref");
        question.setLabel("label");
        question.setState("state");
        question.setRulesCategory(rulesCategory);
        Question question1 = questionService.create(question);
        List<Question> questions = new ArrayList<>();
        questions.add(question1);

        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName("ch1");
        chain.setQuestions(questions);

        // do
        Chain chain1 = chainService.create(chain);
        chain1.setReference("newRef");
        chain1.setRulesCategory(rulesCategory);
        Chain chain2 = chainService.update(chain1);
        // then
        Assertions.assertThat(chain2).isNotNull();
        Assertions.assertThat(chain2.getReference()).isEqualTo("newRef");
    }

    @Test
    @Transactional
    public void delChain() {
        // given
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("e");
        User user = userRepository.save(user1);
        String reference = "R4";
        String label = "...";
        String state = "";
        Question question1 = new Question();
        question1.setReference(reference);
        question1.setLabel(label);
        question1.setState(state);
        question1.setRulesCategory(rulesCategory);
        question1.setUser(user);

        Subject subject = new Subject();
        subject.setName("subject");
        question1.setSubject(subjectService.create(subject));
        Question question = questionService.create(question1);

        List<Question> questions = (List<Question>) questionRepository.findAll();
        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName("ch2");
        chain.setQuestions(questions);

        // do
        Chain chain1 = chainService.create(chain);
        long id = chain1.getId();
        questionService.delete(question.getId());
        chainService.delete(chain1.getId());

        // then
        Chain chain2 = chainService.withId(id);
        Assert.assertNull(chain2);
    }
}
