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

import com.orange.confocara.connector.persistence.dto.RulesetDto;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
public class RulesetRepositoryFindAllRulesetDtoIT {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    @Autowired
    private RulesetRepository rulesetRepository;

    @Before
    public void init() {
        List<QuestionnaireObject> questionnaires = new ArrayList<>();
        QuestionnaireObject questionnaireObject = GeneratorUtil.saveQuestionnaire("name", "qo1", new ArrayList<>(), impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);
        questionnaires.add(questionnaireObject);

        RulesCategory rulesCategory = rulesCategoryRepository.findAll().get(0);

        GeneratorUtil.saveRuleset("ruleset", "qo1", 1, rulesCategory, "fr", "comment", questionnaires, rulesetRepository);
    }

    @Test
    public void shouldReturnSameRulesetAttributesAsRulesetWhenDBContains1Ruleset() {
        List<RulesetDto> allRulesetDto = rulesetRepository.findAllRulesetDto();
        Assert.assertFalse(allRulesetDto.isEmpty());

        Ruleset ruleset = rulesetRepository.findByType("ruleset");
        Assert.assertEquals(allRulesetDto.get(0).getReference(), ruleset.getReference());
        Assert.assertEquals(allRulesetDto.get(0).getName(), ruleset.getType());
        Assert.assertEquals(allRulesetDto.get(0).getPublished(), ruleset.isPublished());
        Assertions.assertThat(allRulesetDto.get(0).getId()).isEqualTo(ruleset.getId());
        Assert.assertEquals(allRulesetDto.get(0).getRulesCategoryName(), ruleset.getRulesCategory().getName());
        Assert.assertEquals(allRulesetDto.get(0).getVersion(), ruleset.getVersion());
        Assert.assertEquals(allRulesetDto.get(0).getDate().getTime(), ruleset.getDate().getTime());
        Assert.assertEquals(allRulesetDto.get(0).getUsername(), "");
        Assert.assertEquals(allRulesetDto.get(0).getComment(), ruleset.getComment());
        Assert.assertEquals(allRulesetDto.get(0).getLanguage(), ruleset.getLanguage());
    }

    @Test
    public void shouldReturnEmptyRulesetDtoWhenRulesetDbIsEmpty() {
        List<RulesetDto> allRulesetDto = rulesetRepository.findAllRulesetDto();
        Assert.assertFalse(allRulesetDto.isEmpty());
        Assert.assertTrue(allRulesetDto.size() == 1);

        long id = allRulesetDto.get(0).getId();

        rulesetRepository.delete(id);

        List<RulesetDto> allRulesetDtoAfterDelete = rulesetRepository.findAllRulesetDto();
        Assert.assertTrue(allRulesetDtoAfterDelete.isEmpty());
    }
}
