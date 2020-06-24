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

import static com.orange.confocara.TestUtils.genCategory;
import static com.orange.confocara.TestUtils.genChain;
import static com.orange.confocara.TestUtils.genEquipment;
import static com.orange.confocara.TestUtils.genImpactValue;
import static com.orange.confocara.TestUtils.genQuestionnaire;
import static com.orange.confocara.TestUtils.genRulesCategory;
import static com.orange.confocara.TestUtils.genRuleset;
import static com.orange.confocara.TestUtils.genUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import java.util.Date;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * see {@link RulesetPublishingService#publishRuleset(long)}
 */
@EnableWebSecurity
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RulesetPublishServiceImplPublishRulesetIT {

    @Autowired
    private RulesetPublishingService rulesetPublishService;

    @Autowired
    private RulesetRepository rulesetRepository;

    @Autowired
    private RulesetService rulesetService;

    @Test
    public void shouldPublishRuleset() {
        // Given
        Ruleset ruleset = givenRuleset();

        // When
        rulesetPublishService.publishRuleset(ruleset.getId());

        // Then
        Ruleset result = rulesetRepository.findOne(ruleset.getId());
        assertThat(result.getId()).isEqualTo(ruleset.getId());
        assertThat(result.getVersion()).isEqualTo(ruleset.getVersion() + 1);

        assertThat(rulesetService.findPublishedRulesetByReferenceAndVersion(ruleset.getReference(), ruleset.getVersion())).isNotNull();
        assertThat(rulesetService.findPublishedRulesetByReferenceAndVersion(result.getReference(), result.getVersion())).isNull();
    }

    ////////////////////////////////////////////////////////
    //
    // Test preparation functions and repositories
    //
    ////////////////////////////////////////////////////////

    private Ruleset givenRuleset() {
        // prepare
        Integer version = nextInt();

        ImpactValue impactValue = impactValueRepository.save(genImpactValue());
        RulesCategory rulesCategory = rulesCategoryRepository
                .save(genRulesCategory(impactValue));
        Category category = categoryRepository.save(genCategory());
        Equipment equipment = equipmentRepository.save(genEquipment(category));
        Chain chain = chainRepository.save(genChain(rulesCategory));
        User author = userRepository.save(genUser());
        QuestionnaireObject questionnaireObject = questionnaireObjectRepository.save(
                genQuestionnaire(randomAlphabetic(10),
                        "",
                        version,
                        rulesCategory,
                        equipment,
                        author,
                        Lists.newArrayList(chain),
                        false));

        // publish and upgrade the QuestionnaireObject, in order to create a RevisionNumber
        questionnaireObject.setPublished(true);
        questionnaireObject.setDate(new Date());
        questionnaireObjectService.update(questionnaireObject);

        questionnaireObject.setPublished(false);
        questionnaireObject.setVersion(questionnaireObject.getVersion() + 1);
        questionnaireObjectService.update(questionnaireObject);

        // create the ruleset
        return rulesetRepository.save(
                genRuleset("TEST_REFERENCE", rulesCategory, questionnaireObject));
    }

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private QuestionnaireObjectService questionnaireObjectService;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private UserRepository userRepository;
}