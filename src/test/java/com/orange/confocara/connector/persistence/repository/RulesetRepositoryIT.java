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

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
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
public class RulesetRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private ProfileTypeRepository profileTypeRepository;

    @Autowired
    private RulesetRepository rulesetRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    private String subjectName = "subject1";

    private String username1 = "Duchess";
    private String password1 = "1234";
    private String reference = "R1";
    private String name = "name";
    private String label = "label";
    private String state = "complete";

    @Test
    public void createRuleset() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        Ruleset ruleset = new Ruleset();
        ruleset.setApp("app0");
        ruleset.setVersion(1);

        ruleset.setLanguage("fr");
        ruleset.setReference(reference);
        ruleset.setState(state);
        ruleset.setRulesCategory(rulesCategory);
        ruleset.setType("type");
        ruleset.setUser(userFromDB);
        rulesetRepository.save(ruleset);
        Assert.assertNotNull(rulesetRepository.findByReference(reference));
    }

    @Test
    public void createRulesetWithProfileType() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);

        ProfileType profileType = GeneratorUtil
                .saveProfileType("myRef", "myName", "icon", profileTypeRepository);

        List<ProfileType> profileTypeList = new ArrayList<>();
        profileTypeList.add(profileType);

        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        Ruleset ruleset = new Ruleset();
        ruleset.setApp("app0");
        ruleset.setRulesCategory(rulesCategory);
        ruleset.setVersion(1);
        ruleset.setLanguage("fr");
        ruleset.setReference(reference);
        ruleset.setState(state);
        ruleset.setType("type");
        ruleset.setUser(userFromDB);
        rulesetRepository.save(ruleset);
        Assert.assertNotNull(rulesetRepository.findByReference(reference));
    }

    @Test
    public void createRulesetWithQuestionnaire() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        final Subject subject1 = new Subject();
        subject1.setName(subjectName);
        Subject subject = subjectRepository.save(subject1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        final Question question = new Question();
        question.setReference(reference);
        question.setLabel(label);
        question.setState(state);
        question.setRulesCategory(rulesCategory);
        question.setUser(userFromDB);
        question.setSubject(subject);
        Question Q1 = questionRepository.save(question);

        List<Question> questions = new ArrayList<>();
        questions.add(Q1);

        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setName(name);
        chain.setRulesCategory(rulesCategory);
        chain.setQuestions(questions);

        chain = chainRepository.save(chain);
        List<Chain> chains = new ArrayList<>();
        chains.add(chain);

        Category category = new Category();
        category.setName("categoryName");
        Category savedCategory = categoryRepository.save(category);

        Equipment equipment1 = new Equipment();
        equipment1.setReference("");
        equipment1.setName("porte");
        //equipment1.setIcon("");
        equipment1.setType("");
        equipment1.setUser(userFromDB);
        List<Category> categories = new ArrayList<>();
        categories.add(savedCategory);
        equipment1.setCategories(categories);
        Equipment equipment = equipmentRepository.save(equipment1);
        QuestionnaireObject questionnaireObject = new QuestionnaireObject();
        questionnaireObject.setReference("QO1");
        questionnaireObject.setState("complete");
        questionnaireObject.setEquipment(equipment);
        questionnaireObject.setRulesCategory(rulesCategory);
        questionnaireObject.setChains(chains);
        questionnaireObject.setUser(userFromDB);
        questionnaireObject.setName("QOname");

        QuestionnaireObject questionnaireObject1 = questionnaireObjectRepository.save(questionnaireObject);
        List<QuestionnaireObject> questionnaireObjects = new ArrayList<>();
        questionnaireObjects.add(questionnaireObject1);

        Ruleset ruleset = new Ruleset();
        ruleset.setApp("app0");
        ruleset.setVersion(1);
        ruleset.setLanguage("fr");
        ruleset.setReference(reference);
        ruleset.setState(state);
        ruleset.setRulesCategory(rulesCategory);
        ruleset.setType("type");
        ruleset.setUser(userFromDB);

        ruleset.setQuestionnaireObjects(questionnaireObjects);
        rulesetRepository.save(ruleset);

        Assert.assertNotNull(rulesetRepository.findByReference(reference));
    }
}

