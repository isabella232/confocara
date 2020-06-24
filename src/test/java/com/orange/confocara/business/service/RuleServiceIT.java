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

import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.User;
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
public class RuleServiceIT {

    private static final String REF = "R1";

    @Autowired
    UserService userService;

    @Autowired
    RuleService ruleService;

    @Autowired
    ImpactValueService impactValueService;

    @Autowired
    RulesCategoryService rulesCategoryService;

    @Autowired
    ProfileTypeService profileTypeService;

    @Autowired
    RuleImpactService ruleImpactService;

    @Test
    public void createRule() {
        User user = saveUserDB();
        Rule rule1 = generateRule(REF, user);

        Assertions.assertThat(rule1).isNotNull();
        Assertions.assertThat(rule1.getReference()).isEqualTo(REF);
        Assertions.assertThat(rule1.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void delRule() {
        User user = saveUserDB();
        Rule rule1 = generateRule(REF, user);

        ruleService.delete(rule1.getId());
        Assertions.assertThat(ruleService.all()).isEmpty();
    }

    @Test
    public void updateRule() {
        User user = saveUserDB();
        Rule rule1 = generateRule(REF, user);

        rule1.setLabel("new");
        ruleService.update(rule1);

        Assertions.assertThat(ruleService.all().size()).isEqualTo(1);
    }

    private User saveUserDB() {
        String username = "titi";
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("password");
        user.setEmail("email@mail.fr");

        return userService.create(user);
    }

    private Rule generateRule(String reference, User user) {
        return generateRule(reference, user, impactValueService,
                rulesCategoryService, profileTypeService,
                ruleService);
    }

    public Rule generateRule(String reference, User user,
                                    ImpactValueService impactValueService,
                                    RulesCategoryService rulesCategoryService,
                                    ProfileTypeService profileTypeService,
                                    RuleService ruleService) {
        String label = "label";

        Rule rule = new Rule();
        rule.setReference(reference);
        rule.setLabel(label);
        rule.setUser(user);

        ProfileType profileType = ProfileTypeServiceIT.generateProfileType("profileTypeName",
                impactValueService, rulesCategoryService, profileTypeService);

        ImpactValue impactValue = ImpactValueServiceIT
                .createImpactValue("ImpactName", impactValueService);
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        RuleImpact ruleImpact = new RuleImpact();
        ruleImpact.setProfileType(profileType);
        ruleImpact.setImpact(impactValue);

        List<RuleImpact> ruleImpacts = new ArrayList<>();
        ruleImpacts.add(ruleImpact);

        rule.setRuleImpacts(ruleImpacts);
        rule.setRulesCategory(rulesCategory);

        return ruleService.create(rule);
    }
}
