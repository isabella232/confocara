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

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
public class ChainRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    private String subjectName = "subject1";
    private String username = "Duchess";
    private String password = "1234";

    private String reference = "R1";
    private String name = "chainName";
    private String label = "label";
    private String state = "complete";

    @Test
    public void createChain() {
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);
        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setRulesCategory(rulesCategory);
        chain.setName(name);
        chainRepository.save(chain);

        Chain chain1 = chainRepository.findByReference(reference);
        Assertions.assertThat(chain1.getReference()).isEqualTo(reference);
    }

    @Test
    public void createChainwithQs() {
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash(password);
        user1.setEmail("e");
        User userFromDB = userRepository.save(user1);
        final Subject subject1 = new Subject();
        subject1.setName(subjectName);
        Subject subject = subjectRepository.save(subject1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        final Question question = new Question();
        question.setReference(reference);
        question.setLabel(label);
        question.setState(state);
        question.setUser(userFromDB);
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        Question Q1 = questionRepository.save(question);
        final Question question1 = new Question();
        question1.setReference(reference+"12");
        question1.setLabel(label);
        question1.setState(state);
        question1.setUser(userFromDB);
        question1.setRulesCategory(rulesCategory);
        question1.setSubject(subject);

        Question Q2 = questionRepository.save(question1);
        // do
        Chain chain = new Chain();
        chain.setReference(reference+"1");
        chain.setName(name);
        chain.setRulesCategory(rulesCategory);
        List<Question> questions = new ArrayList<>();
        questions.add(Q1);
        questions.add(Q2);
        chain.setQuestions(questions);

        chainRepository.save(chain);

        // then
        Assertions.assertThat(chain.getReference()).isEqualTo(reference+"1");
        chainRepository.findAll().forEach(System.out::println);
    }
}

