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
import static com.orange.confocara.TestUtils.genImageEquipment;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import com.orange.ConfOcaraSpringApplication;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
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
import com.orange.confocara.connector.persistence.repository.ImageEquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.ProfileTypeRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RuleImpactRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * see {@link RulesetRevisionPublishingService#generatePublishedRuleset(String, Integer)}
 */
@Slf4j
@EnableWebSecurity
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConfOcaraSpringApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RulesetRevisionPublishingServiceImplGeneratePublishedRulesetIT {

    @Autowired
    private RulesetRevisionPublishingService rulesetRevisionPublishingService;

    @Autowired
    private RulesetRepository rulesetRepository;

    @Autowired
    private RulesetPublishingRepository rulesetPublishingRepository;

    @Test
    public void shouldPublishRuleset() {
        // Given
        Ruleset ruleset = givenRuleset();

        // When
        rulesetRevisionPublishingService
                .generatePublishedRuleset(ruleset.getReference(), ruleset.getVersion());

        // Then
        PublishedRuleset actualRuleset = rulesetPublishingRepository
                .findOneByReferenceAndVersion(ruleset.getReference(), ruleset.getVersion());
        assertThat(actualRuleset).isNotNull();
        assertThat(actualRuleset.getVersion()).isEqualTo(ruleset.getVersion());
        assertThat(actualRuleset.getReference()).isEqualTo(ruleset.getReference());
        assertThat(actualRuleset.getContent()).isNotNull();
        assertThat(actualRuleset.getContent()).isNotEmpty();
    }

    ////////////////////////////////////////////////////////
    //
    // Test preparation functions and repositories
    //
    ////////////////////////////////////////////////////////

    @Transactional
    public Ruleset givenRuleset() {

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

        ImageEquipment icon = imageEquipmentRepository.save(genImageEquipment("image1"));
        Equipment equipment = equipmentRepository.save(
                genEquipment("Amphi", "EQ01", newArrayList(categories), "object"));
        icon.setEquipment(equipment);
        imageEquipmentRepository.save(icon);
        equipment.setIcon(icon);
        equipmentRepository.save(equipment);

        ImageEquipment otherIcon = imageEquipmentRepository.save(genImageEquipment("image2"));
        Equipment otherEquipment = equipmentRepository.save(
                genEquipment("Chemin", "EQ02", newArrayList(categories), "object"));
        otherIcon.setEquipment(otherEquipment);
        imageEquipmentRepository.save(otherIcon);
        otherEquipment.setIcon(otherIcon);
        equipmentRepository.save(otherEquipment);

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
                        equipment,
                        author,
                        newArrayList(c34),
                        false)
        );
        qo36.setPublished(true);
        qo36.setDate(new Date());
        questionnaireObjectService.update(qo36);

        qo36.setPublished(false);
        qo36.setVersion(qo36.getVersion() + 1);
        questionnaireObjectService.update(qo36);


        // rulesets 1 & 2
        Ruleset ruleset = rulesetRepository.save(
                genRuleset(
                        "Accessibilité des locaux",
                        "RS01",
                        1,
                        defaultRulesCategory,
                        randomAlphabetic(2),
                        randomAlphabetic(10),
                        newArrayList(qo36))
        );
        ruleset.markAsPublished();

        log.info("created ruleset with reference={} and version={}", ruleset.getReference(), ruleset.getVersion());
        return rulesetRepository.save(ruleset);
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
    private ImageEquipmentRepository imageEquipmentRepository;

    @Autowired
    private QuestionnaireObjectService questionnaireObjectService;
}