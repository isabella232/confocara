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

package com.orange.confocara.presentation.webservice.rule;

import static com.orange.confocara.TestUtils.genCategory;
import static com.orange.confocara.TestUtils.genChain;
import static com.orange.confocara.TestUtils.genDefaultImpactValue;
import static com.orange.confocara.TestUtils.genEquipment;
import static com.orange.confocara.TestUtils.genImageProfileType;
import static com.orange.confocara.TestUtils.genImpactValue;
import static com.orange.confocara.TestUtils.genProfileType;
import static com.orange.confocara.TestUtils.genQuestion;
import static com.orange.confocara.TestUtils.genQuestionnaire;
import static com.orange.confocara.TestUtils.genRule;
import static com.orange.confocara.TestUtils.genRuleImpact;
import static com.orange.confocara.TestUtils.genRulesCategory;
import static com.orange.confocara.TestUtils.genRuleset;
import static com.orange.confocara.TestUtils.genSubject;
import static com.orange.confocara.TestUtils.genUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.business.service.QuestionnaireObjectService;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImageProfileTypeRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.ProfileTypeRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RuleImpactRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import com.orange.confocara.presentation.webservice.rule.RuleQueryRepositoryFindAllIT.RuleQueryRepositoryFindAllITITConfig;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebService.RulePageAttributes;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebService.RuleSearchCriteria;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ConfOcaraSpringApplication.class,
        RuleQueryRepositoryFindAllITITConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class RuleQueryRepositoryFindAllIT {

    @Autowired
    private RuleQueryRepository ruleQueryRepository;

    @Autowired
    private RulesetRepository rulesetRepository;

    @Test
    public void shouldRetrieveAllRulesWhenNoCriteria() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldRetrieveNoRulesWhenEquipmentDoesNotExist() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addEquipmentIds(nextLong()).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void shouldRetrieveNoRulesWhenContextDoesNotMatch() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addContextIds(nextLong()).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void shouldRetrieveNoRulesWhenConcernDoesNotMatch() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addConcernIds(nextLong()).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void shouldRetrieveRulesWhenEquipmentDoesExist() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addEquipmentIds(1L).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldRetrieveRulesWhenContextDoesMatch() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addContextIds(1L).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldRetrieveRulesWhenConcernWithValidImpactDoesMatch() {
        // Given
        prepareDatabase();

        RuleSearchCriteria criteria = ImmutableRuleSearchCriteria.builder().addConcernIds(1L).build();

        // When
        Page<Rule> result = ruleQueryRepository.findAll(criteria, parameters());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    private RulePageAttributes parameters() {
        return ImmutableRulePageAttributes.builder().build();
    }

    ////////////////////////////////////////////////////////
    //
    // Test preparation functions and repositories
    //
    ////////////////////////////////////////////////////////

    @Transactional
    public void prepareDatabase() {

        ImpactValue notConcernedImpactValue = impactValueRepository
                .save(genImpactValue(1L, "Non concerné"));

        ImpactValue annoyingImpactValue = impactValueRepository
                .save(genImpactValue(2L, "Gênant"));

        ImpactValue blockingImpactValue = impactValueRepository
                .save(genImpactValue(3L, "Bloquant"));

        RulesCategory defaultRulesCategory = rulesCategoryRepository
                .save(genRulesCategory("Accessibilité", notConcernedImpactValue));

        Iterable<Category> categories = categoryRepository
                .save(
                        newArrayList(
                                genCategory("Accès zone"),
                                genCategory("Services"),
                                genCategory("Cheminement"))
                );

        List<Equipment> equipments = newArrayList(equipmentRepository.save(
                newArrayList(
                        genEquipment("Amphi", "EQ01", newArrayList(categories), "object", "icon"),
                        genEquipment("Chemin", "EQ02", newArrayList(categories), "object", "icon"))
        ));

        List<ProfileType> profileTypes = newArrayList(profileTypeRepository.save(
                newArrayList(
                        genProfileType("Fauteuil roulant électrique",
                                newArrayList(defaultRulesCategory)),
                        genProfileType("Fauteuil roulant manuel",
                                newArrayList(defaultRulesCategory)),
                        genProfileType("Difficultés de déplacement",
                                newArrayList(defaultRulesCategory)),
                        genProfileType("Difficultés de préhension",
                                newArrayList(defaultRulesCategory)),
                        genProfileType("Petite taille", newArrayList(defaultRulesCategory)),
                        genProfileType("Non-voyant", newArrayList(defaultRulesCategory)),
                        genProfileType("Mal-voyant", newArrayList(defaultRulesCategory)),
                        genProfileType("Sourd", newArrayList(defaultRulesCategory)),
                        genProfileType("Mal-entendant", newArrayList(defaultRulesCategory))
                )
        ));

        List<Subject> subjects = newArrayList(subjectRepository.save(
                newArrayList(
                        genSubject("Caractéristiques & équipements"),
                        genSubject("Eclairage"),
                        genSubject("Qualité d'usage"),
                        genSubject("Qualité de cheminement"),
                        genSubject("Services associés"),
                        genSubject("Signalétique")
                )
        ));

        User author = userRepository.save(genUser());

        // R226
        List<RuleImpact> ruleR226Impacts = newArrayList(ruleImpactRepository.save(
                newArrayList(
                        genRuleImpact(notConcernedImpactValue, defaultRulesCategory,
                                profileTypes.get(8)),
                        genRuleImpact(annoyingImpactValue, defaultRulesCategory,
                                profileTypes.get(0))
                )
        ));

        Rule r226 = ruleRepository.save(genRule("R226", defaultRulesCategory, newArrayList(), author));
        r226.setRuleImpacts(ruleR226Impacts);
        r226 = ruleRepository.save(r226);

        // R227
        List<RuleImpact> ruleR227Impacts = newArrayList(ruleImpactRepository.save(
                newArrayList(
                        genRuleImpact(notConcernedImpactValue, defaultRulesCategory,
                                profileTypes.get(8)),
                        genRuleImpact(notConcernedImpactValue, defaultRulesCategory,
                                profileTypes.get(0)),
                        genRuleImpact(annoyingImpactValue, defaultRulesCategory,
                                profileTypes.get(1))
                )
        ));

        Rule r227 = ruleRepository.save(genRule("R227", defaultRulesCategory, newArrayList(), author));
        r227.setRuleImpacts(ruleR227Impacts);
        r227 = ruleRepository.save(r227);

        // Question Q118
        Question q118 = questionRepository
                .save(genQuestion(
                        "Est-ce que l'accès à l'infirmerie est suffisamment bien signalé pour des personnes ne connaissant pas les locaux ?",
                        defaultRulesCategory, newArrayList(r226, r227), author, subjects.get(0)));

        // Chaine 34
        Chain c34 = chainRepository
                .save(genChain("Contrôle_infirmerie", null, defaultRulesCategory,
                        newArrayList(q118)));

        // Questionnaire 36
        QuestionnaireObject qo36 = questionnaireObjectRepository.save(
                genQuestionnaire(
                        "Questionnaire_infirmerie",
                        "QO36",
                        1,
                        defaultRulesCategory,
                        equipments.get(0),
                        author,
                        newArrayList(c34),
                        false)
        );

        // rulesets 1 & 2
        rulesetRepository.save(newArrayList(
                genRuleset(
                        "Accessibilité des locaux",
                        "RS01",
                        1,
                        defaultRulesCategory,
                        randomAlphabetic(2),
                        randomAlphabetic(10),
                        newArrayList(qo36)),
                genRuleset(
                        "Accessibilité des circulations",
                        "RS02",
                        1,
                        defaultRulesCategory,
                        randomAlphabetic(2),
                        randomAlphabetic(10),
                        newArrayList(qo36))
        ));
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

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RuleImpactRepository ruleImpactRepository;

    @Autowired
    private ProfileTypeRepository profileTypeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ImageProfileTypeRepository imageProfileTypeRepository;

    @TestConfiguration
    static class RuleQueryRepositoryFindAllITITConfig {

    }
}