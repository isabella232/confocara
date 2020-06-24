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

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.IllustrationRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import java.util.ArrayList;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class RulesetServiceIT {

    @Autowired
    UserRepository userRepository;

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
    QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    QuestionnaireObjectService questionnaireObjectService;

    @Autowired
    ChainRepository chainRepository;

    @Autowired
    ChainService chainService;

    @Autowired
    RulesetService rulesetService;

    @Autowired
    RulesetRepository rulesetRepository;

    @Autowired
    ImpactValueService impactValueService;

    @Autowired
    RulesCategoryService rulesCategoryService;

    @Test
    public void createRuleset() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        String reference = "RS1";

        String state = "";

        //do
        Ruleset ruleset1 = new Ruleset();
        ruleset1.setReference(reference);
        ruleset1.setState(state);
        ruleset1.setUser(user);
        ruleset1.setRulesCategory(rulesCategory);
        Ruleset ruleset = rulesetService.create(ruleset1);

        // then
        Assertions.assertThat(ruleset).isNotNull();
        Assertions.assertThat(ruleset.getReference()).isEqualTo(reference);

    }

    @Test
    public void updateRuleset() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        String reference = "RS1";

        String state = "";

        //do
        Ruleset ruleset1 = new Ruleset();
        ruleset1.setReference(reference);
        ruleset1.setState(state);
        ruleset1.setUser(user);
        ruleset1.setRulesCategory(rulesCategory);
        Ruleset ruleset = rulesetService.create(ruleset1);
        ruleset.setApp("test");
        rulesetService.update(ruleset);

        // then
        Assertions.assertThat(rulesetService.all().size()).isEqualTo(1);

    }

    @Test
    public void delRuleset() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);

        String reference = "RS1";

        String state = "";
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        //do
        Ruleset ruleset1 = new Ruleset();
        ruleset1.setReference(reference);
        ruleset1.setState(state);
        ruleset1.setUser(user);
        ruleset1.setRulesCategory(rulesCategory);
        Ruleset ruleset = rulesetService.create(ruleset1);
        rulesetService.delete(ruleset.getId());
        // then
        Assertions.assertThat(rulesetService.all()).isEmpty();
    }

    @Test
    public void createRulesetWithQOs() {
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        String reference = "RS1";
        QuestionnaireObject questionnaireObject = new QuestionnaireObject();
        questionnaireObject.setReference(reference);
        questionnaireObject.setName("QOname");
        questionnaireObject.setUser(user);
        questionnaireObject.setRulesCategory(rulesCategory);
        questionnaireObject.setListPositionAndChainRefMap();
        QuestionnaireObject questionnaireObjectDB = questionnaireObjectService.create(questionnaireObject);

        List<QuestionnaireObject> questionnaireObjects = new ArrayList<>();
        questionnaireObjects.add(questionnaireObjectDB);

        Ruleset ruleset1 = new Ruleset();
        ruleset1.setReference(reference);
        ruleset1.setState(" ");
        ruleset1.setUser(user);
        ruleset1.setRulesCategory(rulesCategory);

        ruleset1.setQuestionnaireObjects(questionnaireObjects);
        Ruleset ruleset = rulesetService.create(ruleset1);

        Assertions.assertThat(ruleset).isNotNull();
        Assertions.assertThat(ruleset.getReference()).isEqualTo(reference);
    }
}
