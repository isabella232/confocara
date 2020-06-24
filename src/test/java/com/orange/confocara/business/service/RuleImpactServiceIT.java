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
import com.orange.confocara.connector.persistence.model.RuleImpact;
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
public class RuleImpactServiceIT {

    private static final String PROFILE_TYPE_NAME = "profileTypeName";
    private static final String IMPACT_NAME = "impactValueName";

    @Autowired
    private RuleImpactService ruleImpactService;
    @Autowired
    private ProfileTypeService profileTypeService;
    @Autowired
    private ImpactValueService impactValueService;
    @Autowired
    private RulesCategoryService rulesCategoryService;

    @Test
    public void createRuleImpact() {
        RuleImpact ruleImpact = getRuleImpact();
        RuleImpact byName = ruleImpactService.findById(ruleImpact.getId());
        Assertions.assertThat(ruleImpact).isNotNull();
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void updateRuleImpact() {
        RuleImpact ruleImpact = getRuleImpact();
        ProfileType profileType = ruleImpact.getProfileType();
        profileType.setReference("newRef");
        ruleImpact.setProfileType(profileType);
        ruleImpactService.update(ruleImpact);
        RuleImpact byName = ruleImpactService.findById(ruleImpact.getId());
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void deleteRuleImpact() {
        RuleImpact ruleImpact = getRuleImpact();
        ruleImpactService.delete(ruleImpact.getId());
        RuleImpact byNameDel = ruleImpactService.findById(ruleImpact.getId());
        Assertions.assertThat(byNameDel).isNull();
    }

    public static RuleImpact createRuleImpact(ImpactValueService impactValueService,
                                              RulesCategoryService rulesCategoryService,
                                              ProfileTypeService profileTypeService,
                                              RuleImpactService ruleImpactService) {

        ProfileType profileType = ProfileTypeServiceIT.generateProfileType(PROFILE_TYPE_NAME,
                impactValueService, rulesCategoryService, profileTypeService);

        ImpactValue impactValue = ImpactValueServiceIT
                .createImpactValue(IMPACT_NAME, impactValueService);

        RuleImpact ruleImpact = new RuleImpact();
        ruleImpact.setProfileType(profileType);
        ruleImpact.setImpact(impactValue);

        return ruleImpactService.create(ruleImpact);
    }

    private RuleImpact getRuleImpact() {
        return createRuleImpact(impactValueService, rulesCategoryService, profileTypeService, ruleImpactService);
    }
}
