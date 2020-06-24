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
import com.orange.confocara.connector.persistence.model.RulesCategory;
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
public class RulesCategoryServiceIT {

    @Autowired
    private RulesCategoryService rulesCategoryService;
    @Autowired
    private ImpactValueService impactValueService;

    private final static String RULES_CATEGORY_NAME_HANDICAP = "HANDICAP";
    private final static String RULES_CATEGORY_NAME_SECURITE = "SECURITE";

    @Test
    public void createRulesCategory() {
        RulesCategory rulesCategory = getRulesCategory();
        RulesCategory byName = rulesCategoryService.findByName(RULES_CATEGORY_NAME_HANDICAP);
        Assertions.assertThat(rulesCategory).isNotNull();
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void updateRulesCategory() {
        RulesCategory rulesCategory = getRulesCategory();
        rulesCategory.setName(RULES_CATEGORY_NAME_SECURITE);
        rulesCategoryService.update(rulesCategory);
        RulesCategory byName = rulesCategoryService.findByName(RULES_CATEGORY_NAME_SECURITE);
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void deleteRulesCategory() {
        RulesCategory rulesCategory = getRulesCategory();
        RulesCategory byName = rulesCategoryService.findByName(RULES_CATEGORY_NAME_HANDICAP);
        Assertions.assertThat(byName).isNotNull();
        rulesCategoryService.delete(rulesCategory.getId());
        RulesCategory byNameDel = rulesCategoryService.findByName(RULES_CATEGORY_NAME_HANDICAP);
        Assertions.assertThat(byNameDel).isNull();
    }

    public static RulesCategory createRulesCategory(String name,
                                                    ImpactValueService impactValueService,
                                                    RulesCategoryService rulesCategoryService) {

        ImpactValue impactValue = ImpactValueServiceIT
                .createImpactValue("impact", impactValueService);
        RulesCategory rulesCategory = new RulesCategory();
        rulesCategory.setName(name);
        rulesCategory.setDefaultImpact(impactValue);

        List<ImpactValue> impactValues = new ArrayList<>();
        impactValues.add(impactValue);

        rulesCategory.setImpactValues(impactValues);

        return rulesCategoryService.create(rulesCategory);
    }

    private RulesCategory getRulesCategory() {
        return createRulesCategory(RULES_CATEGORY_NAME_HANDICAP, impactValueService, rulesCategoryService);
    }
}
