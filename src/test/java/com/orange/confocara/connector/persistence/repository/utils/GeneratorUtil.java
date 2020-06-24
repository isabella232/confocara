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

package com.orange.confocara.connector.persistence.repository.utils;

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.ProfileTypeRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeneratorUtil {

    public static Category saveCategory(String name, CategoryRepository categoryRepository) {
        Category category = new Category();
        category.setName(name);

        return categoryRepository.save(category);
    }

    public static Subject saveSubject(String name, SubjectRepository subjectRepository) {
        Subject subject = new Subject();
        subject.setName(name);

        return subjectRepository.save(subject);
    }

    public static ImpactValue saveImpactValue(String name, ImpactValueRepository impactValueRepository) {
        ImpactValue impactValue1 = new ImpactValue();
        impactValue1.setName(name);

        return impactValueRepository.save(impactValue1);
    }

    public static ProfileType saveProfileType(String reference,
                                              String name,
                                              String icon,
                                              ProfileTypeRepository profileTypeRepository) {

        ProfileType profileType = ProfileType.builder().build();
        profileType.setReference(reference);
        profileType.setName(name);

        ImageProfileType imageProfileType = new ImageProfileType();
        imageProfileType.setImageName(icon);
        imageProfileType.setExtension("jpg");
        profileType.setIcon(imageProfileType);

        return profileTypeRepository.save(profileType);
    }

    public static RulesCategory saveRulesCategory(String name,
                                                  ImpactValue impactValue,
                                                  List<ImpactValue> impacts,
                                                  RulesCategoryRepository rulesCategoryRepository) {

        final RulesCategory rulesCategory = new RulesCategory();
        rulesCategory.setName(name);
        rulesCategory.setDefaultImpact(impactValue);
        rulesCategory.setImpactValues(impacts);

        return rulesCategoryRepository.save(rulesCategory);
    }

    public static RulesCategory saveRulesCategoryWithDefaultImpact(String name,
                                                  ImpactValueRepository impactValueRepository,
                                                  RulesCategoryRepository rulesCategoryRepository) {

        ImpactValue impactValue = GeneratorUtil.saveImpactValue("impact", impactValueRepository);
        List<ImpactValue> impacts = new ArrayList<>();
        impacts.add(impactValue);
        return saveRulesCategory(name, impactValue, impacts, rulesCategoryRepository);
    }

    public static Rule saveRuleWithoutImpacts(RulesCategory rulesCategory, String label, String ref, RuleRepository ruleRepository) {
        Rule rule = new Rule();
        rule.setRulesCategory(rulesCategory);
        rule.setReference(ref);
        rule.setLabel(label);
        rule.setRuleImpacts(new ArrayList<>());

        return ruleRepository.save(rule);
    }

    public static Equipment saveEquipment(String name, String ref, List<Category> categories, String type, String icon, EquipmentRepository equipmentRepository) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setReference(ref);
        equipment.setCategories(categories);

        ImageEquipment imageToSave = new ImageEquipment();
        imageToSave.setImageName(icon);
        imageToSave.setExtension("jpg");
        equipment.setIcon(imageToSave);

        equipment.setType(type);

        return equipmentRepository.save(equipment);
    }

    public static Question saveQuestion(Subject subject, String ref, String label, RulesCategory rulesCategory, List<Rule> rules, QuestionRepository questionRepository) {
        Question q = new Question();
        q.setSubject(subject);
        q.setReference(ref);
        q.setLabel(label);
        q.setRulesCategory(rulesCategory);
        q.setRules(rules);
        q.setState(State.ACTIVE.toString().toLowerCase());

        return questionRepository.save(q);
    }

    public static Chain saveChain(String name, String ref, RulesCategory rulesCategory, List<Question> questions, ChainRepository chainRepository) {
        Chain chain = new Chain();
        chain.setName(name);
        chain.setReference(ref);
        chain.setRulesCategory(rulesCategory);
        chain.setQuestions(questions);

        return chainRepository.save(chain);
    }

    public static QuestionnaireObject saveQuestionnaire(String name, String ref, Integer version, RulesCategory rulesCategory, Equipment equipment, List<Chain> chains, QuestionnaireObjectRepository questionnaireObjectRepository) {
        QuestionnaireObject qo = new QuestionnaireObject();
        qo.setReference(ref);
        qo.setVersion(version);
        qo.setName(name);
        qo.setRulesCategory(rulesCategory);
        qo.setEquipment(equipment);
        qo.setPublished(false);
        qo.setChains(chains);
        qo.setState(State.ACTIVE.toString().toLowerCase());
        qo.setDate(new Date());

        return questionnaireObjectRepository.save(qo);
    }

    public static QuestionnaireObject saveQuestionnaire(String name,
                                                        String ref,
                                                        List<Chain> chains,
                                                        ImpactValueRepository impactValueRepository,
                                                        RulesCategoryRepository rulesCategoryRepository,
                                                        CategoryRepository categoryRepository,
                                                        EquipmentRepository equipmentRepository,
                                                        QuestionnaireObjectRepository questionnaireObjectRepository) {

        RulesCategory rulesCategory = rulesCategoryRepository.findByName("rulesCategory");
        if (rulesCategory == null) {
            rulesCategory = saveRulesCategoryWithDefaultImpact("rulesCategory", impactValueRepository, rulesCategoryRepository);
        }

        Category category = categoryRepository.findByName("myCategory");
        if (category == null) {
            category = saveCategory("myCategory", categoryRepository);
        }

        List<Category> categories = new ArrayList<>();
        categories.add(category);

        Equipment equipment = equipmentRepository.findByName("objectName");
        if (equipment == null) {
            equipment = saveEquipment("objectName", "eq1", categories, "type", "icon", equipmentRepository);
        }

        return saveQuestionnaire(name, ref, 1, rulesCategory, equipment, chains, questionnaireObjectRepository);
    }

    public static Ruleset saveRuleset(String name, String ref, Integer version,
                                      RulesCategory rulesCategory,
                                      String language,
                                      String comment,
                                      List<QuestionnaireObject> questionnaireObjects,
                                      RulesetRepository rulesetRepository) {
        Ruleset ruleset = new Ruleset();
        ruleset.setReference(ref);
        ruleset.setVersion(version);
        ruleset.setType(name);
        ruleset.setRulesCategory(rulesCategory);
        ruleset.setPublished(false);
        ruleset.setQuestionnaireObjects(questionnaireObjects);
        ruleset.setState(State.ACTIVE.toString().toLowerCase());
        ruleset.setDate(new Date());
        ruleset.setLanguage(language);
        ruleset.setComment(comment);

        return rulesetRepository.save(ruleset);
    }
}
