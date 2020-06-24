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
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
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
public class QuestionnaireObjectRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    private String username1 = "Duchess";
    private String password1 = "1234";

    private String reference = "R1";
    private String name = "name";

    @Test
    public void createQO() {
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        Category category = new Category();
        category.setName("categoryName");
        Category savedCategory = categoryRepository.save(category);

        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        Equipment equipment1 = new Equipment();
        equipment1.setReference("");
        equipment1.setName("objectName");
        //equipment1.setIcon("iconName");
        equipment1.setType("definition");
        equipment1.setUser(userFromDB);
        List<Category> categories = new ArrayList<>();
        categories.add(savedCategory);
        equipment1.setCategories(categories);
        Equipment equipment = equipmentRepository.save(equipment1);

        // do
        QuestionnaireObject questionnaireObject = new QuestionnaireObject();
        questionnaireObject.setReference("QO1");
        questionnaireObject.setState("complete");
        questionnaireObject.setName("name");
        questionnaireObject.setRulesCategory(rulesCategory);
        questionnaireObject.setEquipment(equipment);
        QuestionnaireObject questionnaireObject1 = questionnaireObjectRepository.save(questionnaireObject);

        Assert.assertNotNull(questionnaireObject1);
    }

    @Test
    public void createQOwithChains() {

        // given
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        Chain chain = new Chain();
        chain.setReference(reference);
        chain.setName(name);
        chain.setRulesCategory(rulesCategory);

        chain = chainRepository.save(chain);
        List<Chain> chains = new ArrayList<>();
        chains.add(chain);

        Category category = new Category();
        category.setName("categoryName");
        Category savedCategory = categoryRepository.save(category);

        Equipment equipment1 = new Equipment();
        equipment1.setReference("");
        equipment1.setName("objectName");
        //equipment1.setIcon("iconName");
        equipment1.setType("definition");
        equipment1.setUser(userFromDB);
        List<Category> categories = new ArrayList<>();
        categories.add(savedCategory);
        equipment1.setCategories(categories);
        Equipment equipment = equipmentRepository.save(equipment1);

        QuestionnaireObject questionnaireObject = new QuestionnaireObject();
        questionnaireObject.setReference("QO1");
        questionnaireObject.setState("complete");
        questionnaireObject.setEquipment(equipment);
        questionnaireObject.setChains(chains);
        questionnaireObject.setRulesCategory(rulesCategory);
        questionnaireObject.setUser(userFromDB);
        questionnaireObject.setName("name");

        QuestionnaireObject questionnaireObject1 = questionnaireObjectRepository.save(questionnaireObject);

        Assert.assertNotNull(questionnaireObject1);

    }

}

