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
public class QuestionnaireRepositoryFindChainsNameByReferenceIT {

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
    private ChainRepository chainRepository;

    @Before
    public void init() {
        GeneratorUtil.saveQuestionnaire("name", "QO1", new ArrayList<>(), impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);
    }

    @Test
    public void shouldReturnEmptyListWhenDBContains1QuestionnaireWithOChain() {
        List<String> chainNames = questionnaireObjectRepository.findChainsNameByReference("QO1");
        Assert.assertNotNull(chainNames);
        Assert.assertTrue(chainNames.isEmpty());
    }

    @Test
    public void shouldReturnAssociatedChainNameWhenDBContains1QuestionnaireWith1Chain() {
        RulesCategory rulesCategory = rulesCategoryRepository.findAll().get(0);
        List<Chain> chains = new ArrayList<>();
        chains.add(GeneratorUtil.saveChain("chain", "refChain", rulesCategory, new ArrayList<>(), chainRepository));
        GeneratorUtil.saveQuestionnaire("qo2", "QO2", chains, impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);

        List<String> chainNames = questionnaireObjectRepository.findChainsNameByReference("QO2");
        Assert.assertNotNull(chainNames);
        Assert.assertFalse(chainNames.isEmpty());
        Assert.assertTrue(chainNames.size() == 1);
        Assert.assertTrue(chainNames.get(0).equals(chains.get(0).getName()));
    }

    @Test
    public void shouldReturnAssociatedChainNameWhenDBContains1QuestionnaireWith2Chain() {
        RulesCategory rulesCategory = rulesCategoryRepository.findAll().get(0);
        List<Chain> chains = new ArrayList<>();
        chains.add(GeneratorUtil.saveChain("chain1", "refChain1", rulesCategory, new ArrayList<>(), chainRepository));
        chains.add(GeneratorUtil.saveChain("chain2", "refChain2", rulesCategory, new ArrayList<>(), chainRepository));
        GeneratorUtil.saveQuestionnaire("qo3", "QO3", chains, impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);

        List<String> chainNames = questionnaireObjectRepository.findChainsNameByReference("QO3");
        Assert.assertNotNull(chainNames);
        Assert.assertFalse(chainNames.isEmpty());
        Assert.assertTrue(chainNames.size() == 2);
        Assert.assertTrue(chainNames.get(0).equals(chains.get(0).getName()));
        Assert.assertTrue(chainNames.get(1).equals(chains.get(1).getName()));
    }

    @Test
    public void shouldReturnEmptyListWhenRefDoesNotExists() {
        questionnaireObjectRepository.deleteAll();

        List<String> chainNames = questionnaireObjectRepository.findChainsNameByReference("none");
        Assert.assertNotNull(chainNames);
        Assert.assertTrue(chainNames.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListWhenDBContains0Questionnaire() {
        questionnaireObjectRepository.deleteAll();

        List<String> chainNames = questionnaireObjectRepository.findChainsNameByReference("ref");
        Assert.assertNotNull(chainNames);
        Assert.assertTrue(chainNames.isEmpty());
    }
}
