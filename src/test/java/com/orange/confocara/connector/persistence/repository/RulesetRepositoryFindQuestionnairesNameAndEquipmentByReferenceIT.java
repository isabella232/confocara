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

import static org.assertj.core.api.Assertions.assertThat;

import com.orange.confocara.connector.persistence.dto.RulesetQuestionnaireDto;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * see {@link RulesetRepository#findQuestionnairesNameAndEquipmentByReference(String)}
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RulesetRepositoryFindQuestionnairesNameAndEquipmentByReferenceIT {

    @Autowired
    private RulesetRepository rulesetRepository;

    @Test
    public void shouldRetrieveDataWhenReferenceMatchesRuleset() {
        // Given
        Ruleset expectedRuleset = givenRuleset();

        // When
        List<RulesetQuestionnaireDto> result = rulesetRepository
                .findQuestionnairesNameAndEquipmentByReference(expectedRuleset.getReference());

        // Then
        assertThat(result.isEmpty())
                .as("The list of <RulesetQuestionnaireDto> related to the ruleset named " + expectedRuleset.getReference() + " should not be empty")
                .isFalse();
        assertThat(result.size()).isEqualTo(1);
        thenActualQuestionnairesMatchesExpectedQuestionnaires(result, expectedRuleset.getQuestionnaireObjects());
    }

    @Test
    public void shouldRetrieveEmptyListWhenReferenceDoesNotExist() {
        // Given
        String expectedReference = "DOES_NOT_EXIST";

        // When
        List<RulesetQuestionnaireDto> result = rulesetRepository
                .findQuestionnairesNameAndEquipmentByReference(expectedReference);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }

    private void thenActualQuestionnairesMatchesExpectedQuestionnaires(List<RulesetQuestionnaireDto> actual, List<QuestionnaireObject> expected) {
        assertThat(actual.get(0).getEquipmentName()).isEqualTo(expected.get(0).getEquipment().getName());
        assertThat(actual.get(0).getQuestionnaireAuthor()).isEqualTo(expected.get(0).getUser().getUsername());
        assertThat(actual.get(0).getQuestionnaireName()).isEqualTo(expected.get(0).getName());
        assertThat(actual.get(0).getQuestionnaireVersion()).isEqualTo(String.valueOf(expected.get(0).getVersion()));
    }

    ////////////////////////////////////////////////////////
    //
    // Test preparation functions and repositories
    //
    ////////////////////////////////////////////////////////

    Ruleset givenRuleset() {
        // prepare
        ImpactValue impactValue = impactValueRepository.save(TestUtils.genImpactValue());
        RulesCategory rulesCategory = rulesCategoryRepository
                .save(TestUtils.genRulesCategory(impactValue));
        Category category = categoryRepository.save(TestUtils.genCategory());
        Equipment equipment = equipmentRepository.save(TestUtils.genEquipment(category));
        Chain chain = chainRepository.save(TestUtils.genChain(rulesCategory));
        User author = userRepository.save(TestUtils.genUser());
        QuestionnaireObject questionnaireObject = questionnaireObjectRepository
                .save(TestUtils.genQuestionnaire(rulesCategory, equipment, chain, author));

        // create the ruleset
        return rulesetRepository.save(TestUtils.genRuleset("TEST_REFERENCE", rulesCategory, questionnaireObject));
    }

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

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