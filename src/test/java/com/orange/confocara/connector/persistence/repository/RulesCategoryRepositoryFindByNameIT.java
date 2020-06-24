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

import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
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
public class RulesCategoryRepositoryFindByNameIT {

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;
    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Before
    public void initialize() {
        ImpactValue impactValue = GeneratorUtil.saveImpactValue("impact", impactValueRepository);
        List<ImpactValue> impacts = new ArrayList<>();
        impacts.add(impactValue);

        GeneratorUtil.saveRulesCategory("rulesCat", impactValue, impacts, rulesCategoryRepository);
    }

    @Test
    public void shouldReturn1RulesCategoryWhenDBContains1RulesCategoryWithTheSameName() {
        RulesCategory rulesCategory = rulesCategoryRepository.findByName("rulesCat");
        Assert.assertNotNull(rulesCategory);
    }

    @Test
    public void shouldReturn0RulesCategoryWhenDBDoesNotContainsRulesCategoryWithThisName() {
        RulesCategory rulesCategory = rulesCategoryRepository.findByName("categ");
        Assert.assertNull(rulesCategory);
    }
}
