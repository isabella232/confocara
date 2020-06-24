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

package com.orange.confocara.business.service.helper;

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class that aims to format and produce a list of {@link QuestionnaireObject}s
 */
@Slf4j
public class PublishedRulesetHelper {

    // objects mapped by id
    private Map<Long, Chain> chainsMap = new HashMap<>();
    private Map<Long, Question> questionsMap = new HashMap<>();
    private Map<Long, Equipment> equipmentsMap = new HashMap<>();
    private Map<Long, Rule> rulesMap = new HashMap<>();
    private Map<Long, Illustration> illustrationsMap = new HashMap<>();
    private Map<Long, RulesCategory> rulesCategoriesMap = new HashMap<>();
    private Map<Long, ProfileType> profileTypesMap = new HashMap<>();
    private Map<Long, ImpactValue> impactsMap = new HashMap<>();
    private Map<Long, Subject> subjectsMap = new HashMap<>();
    private Map<Long, Category> categoriesMap = new HashMap<>();

    /**
     * As we can publish questionnaires before ruleset, a ruleset can contains many objects with the same
     * reference but different contents. To prevent irrelevant data, we decided to keep the most recent
     * revision of the objects in that case. This method enables to set relevant data in case of duplicates objects
     *
     * @param revNumberQuestionnaireMap questionnaires mapped by revision number
     * @return the same questionnaires but with relevant data = no duplicates references with different content
     */
    public List<QuestionnaireObject> checkQuestionnairesData(@NonNull Map<Integer, QuestionnaireObject> revNumberQuestionnaireMap) {
        clearMaps();
        fillMapsWithMostRecentRevisions(revNumberQuestionnaireMap);

        List<QuestionnaireObject> questionnaireObjectList = new ArrayList<>(revNumberQuestionnaireMap.values());
        replaceQuestionnaireElementsWithMostRecentRevisions(questionnaireObjectList);

        return questionnaireObjectList;
    }

    /**
     * Replace objects in questionnaireObjectList with the most recent revision
     *
     * @param questionnaireObjectList the questionnaires
     */
    private void replaceQuestionnaireElementsWithMostRecentRevisions(@NonNull List<QuestionnaireObject> questionnaireObjectList) {
        for (QuestionnaireObject qo : questionnaireObjectList) {
            setRelevantChainsQuestionsAndRulesCategory(qo);
            setRelevantEquipments(qo);

            for (Question question : qo.getQuestions()) {
                // important log : helps on preventing NullPointerException in sub-functions
                log.debug("QuestionId={};QuestionLabel={};QuestionRulesQuantity={}", question.getId(),
                        question.getLabel(), question.getRules().size());

                setRelevantRulesCategoryRulesAndSubject(question);

                for (Rule rule : question.getRules()) {
                    setRelevantRulesCategoryAndIllustrations(rule);

                    setRelevantRuleImpacts(rule);
                }
            }

            replaceQuestionnaireElementsWithMostRecentRevisions(qo.getQuestionnaireSubObjects());
        }
    }

    /**
     * Replaces rules'ruleImpacts with the most recent revisions
     *
     * @param rule the rule to update
     */
    private void setRelevantRuleImpacts(Rule rule) {
        for (RuleImpact impact : rule.getRuleImpacts()) {
            setRelevantImpactAndProfileType(impact);

            for (RulesCategory rulesCategory : impact.getProfileType().getRulesCategories()) {
                rulesCategory.getImpactValues().replaceAll(x -> impactsMap.get(x.getId()));
                rulesCategory.setDefaultImpact(impactsMap.get(rulesCategory.getDefaultImpact().getId()));
            }
        }
    }

    /**
     * Replaces ruleImpact'objects with the most recent revisions
     *
     * @param impact the rule impact to update
     */
    private void setRelevantImpactAndProfileType(RuleImpact impact) {
        impact.setImpact(impactsMap.get(impact.getImpact().getId()));
        impact.setProfileType(profileTypesMap.get(impact.getProfileType().getId()));
        impact.getProfileType().getRulesCategories().replaceAll(x -> rulesCategoriesMap.get(x.getId()));
    }

    /**
     * Replaces rules' rules category and illustrations with the most recent revisions
     *
     * @param rule the rule to update
     */
    private void setRelevantRulesCategoryAndIllustrations(Rule rule) {
        rule.setRulesCategory(rulesCategoriesMap.get(rule.getRulesCategory().getId()));
        rule.getIllustrations().replaceAll(x -> illustrationsMap.get(x.getId()));
    }

    /**
     * Replaces qo's chain/questions/rules category with the most recent revisions
     *
     * @param qo the qo to update
     */
    private void setRelevantChainsQuestionsAndRulesCategory(QuestionnaireObject qo) {
        qo.getChains().replaceAll(x -> chainsMap.get(x.getId()));
        qo.getQuestions().replaceAll(x -> questionsMap.get(x.getId()));
        qo.setRulesCategory(rulesCategoriesMap.get(qo.getRulesCategory().getId()));
    }

    /**
     * Replaces question's rules/subject/rules category with the most recent revisions
     *
     * @param question the question to update
     */
    private void setRelevantRulesCategoryRulesAndSubject(Question question) {
        question.setRulesCategory(rulesCategoriesMap.get(question.getRulesCategory().getId()));
        question.getRules().replaceAll(x -> rulesMap.get(x.getId()));
        question.setSubject(subjectsMap.get(question.getSubject().getId()));
    }

    /**
     * Replaces qo's equipment and subequipment (and their illustrations and categories) with most recent revisions
     *
     * @param qo the questionnaire to update
     */
    private void setRelevantEquipments(@NonNull QuestionnaireObject qo) {
        qo.setEquipment(equipmentsMap.get(qo.getEquipment().getId()));
        qo.getEquipment().getIllustrations().replaceAll(x -> illustrationsMap.get(x.getId()));
        qo.getEquipment().getSubobjects().replaceAll(x -> equipmentsMap.get(x.getId()));
        qo.getEquipment().getSubobjects().replaceAll(x -> equipmentsMap.get(x.getId()));
        qo.getEquipment().getCategories().replaceAll(x -> categoriesMap.get(x.getId()));

        for (Equipment subObject : qo.getEquipment().getSubobjects()) {
            subObject.getIllustrations().replaceAll(x -> illustrationsMap.get(x.getId()));
        }
    }

    /**
     * Get the most recent revision for each object and put it into this class maps
     */
    private void fillMapsWithMostRecentRevisions(@NonNull Map<Integer, QuestionnaireObject> revNumberQuestionnaireMap) {

        Integer revisionNb;
        QuestionnaireObject questionnaireObject;
        for (Entry<Integer, QuestionnaireObject> entry  : revNumberQuestionnaireMap.entrySet()) {
            revisionNb = entry.getKey();
            questionnaireObject = entry.getValue();

            final Equipment equipment = questionnaireObject.getEquipment();

            saveIllustrationsInMap(revisionNb, equipment.getIllustrations());

            saveEquipmentInMaps(revisionNb, equipment);

            saveEquipmentSubObjectsInMaps(revisionNb, equipment);

            saveChainsElementsInMaps(revisionNb, questionnaireObject);

            saveSubQuestionnairesInMaps(revisionNb, questionnaireObject);
        }
    }

    /**
     * Fill maps with most recent equipment's subobjects
     *
     * @param revisionNb the revision number of equipment
     * @param equipment  the equipment
     */
    private void saveEquipmentSubObjectsInMaps(Integer revisionNb, Equipment equipment) {
        for (Equipment subEquipment : equipment.getSubobjects()) {
            saveEquipmentInMaps(revisionNb, subEquipment);
            saveIllustrationsInMap(revisionNb, subEquipment.getIllustrations());
        }
    }

    /**
     * Fill maps with most recent revisions for questionnaireObject's subQuestionnaires
     *
     * @param revisionNb          the revision number of questionnaireObject
     * @param questionnaireObject the questionnaireObject
     */
    private void saveSubQuestionnairesInMaps(Integer revisionNb, QuestionnaireObject questionnaireObject) {
        for (QuestionnaireObject subQuestionnaire : questionnaireObject.getQuestionnaireSubObjects()) {
            Map<Integer, QuestionnaireObject> subQuestionnairesMap = new HashMap<>();
            subQuestionnairesMap.put(revisionNb, subQuestionnaire);

            fillMapsWithMostRecentRevisions(subQuestionnairesMap);
        }
    }

    /**
     * Fill maps with most recent revisions for questionnaireObject
     *
     * @param revisionNb          the revision number of questionnaireObject
     * @param questionnaireObject the questionnaireObject
     */
    private void saveChainsElementsInMaps(Integer revisionNb, QuestionnaireObject questionnaireObject) {
        for (Chain chain : questionnaireObject.getChains()) {
            saveChainInMap(revisionNb, chain);
            saveQuestionsElementsInMaps(revisionNb, chain.getQuestions());
        }
    }

    /**
     * Fill maps with most recent revisions for questions
     *
     * @param revisionNb the revision number of questions
     * @param questions  the questions
     */
    private void saveQuestionsElementsInMaps(Integer revisionNb, List<Question> questions) {
        for (Question question : questions) {
            saveQuestionInMaps(revisionNb, question);
            saveRulesElementsInMaps(revisionNb, question.getRules());
        }
    }

    /**
     * Fill maps with most recent revisions for rules1
     *
     * @param revisionNb the revision number of rules1
     * @param rules1     the rules
     */
    private void saveRulesElementsInMaps(Integer revisionNb, List<Rule> rules1) {
        for (Rule rule : rules1) {
            saveRuleInMap(revisionNb, rule);

            for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                saveImpactInMap(revisionNb, ruleImpact.getImpact());
                final ProfileType profileType = ruleImpact.getProfileType();
                saveProfileTypeInMap(revisionNb, profileType);
                final List<RulesCategory> rulesCategories = profileType.getRulesCategories();
                for (RulesCategory rulesCategory : rulesCategories) {
                    saveRulesCategoryInMap(revisionNb, rulesCategory);

                    for (ImpactValue value : rulesCategory.getImpactValues()) {
                        saveImpactInMap(revisionNb, value);
                    }
                }
            }

            saveIllustrationsInMap(revisionNb, rule.getIllustrations());
        }
    }

    /**
     * Fill rulesCategoriesMap with most recent rulesCategory
     *
     * @param revisionNb    the revision number of rulesCategory
     * @param rulesCategory the rulesCategory
     */
    private void saveRulesCategoryInMap(Integer revisionNb, RulesCategory rulesCategory) {
        if (!rulesCategoriesMap.containsKey(rulesCategory.getId())) {
            rulesCategory.setRevisionNb(revisionNb);
            rulesCategoriesMap.put(rulesCategory.getId(), rulesCategory);
        } else if (rulesCategoriesMap.get(rulesCategory.getId()).getRevisionNb() < revisionNb) {
            rulesCategory.setRevisionNb(revisionNb);
            rulesCategoriesMap.put(rulesCategory.getId(), rulesCategory);
        }
    }

    /**
     * Fill profileTypesMap with most recent profileType
     *
     * @param revisionNb  the revision number of profileType
     * @param profileType the profileType
     */
    private void saveProfileTypeInMap(Integer revisionNb, ProfileType profileType) {
        if (!profileTypesMap.containsKey(profileType.getId())) {
            profileType.setRevisionNb(revisionNb);
            profileTypesMap.put(profileType.getId(), profileType);
        } else if (profileTypesMap.get(profileType.getId()).getRevisionNb() < revisionNb) {
            profileType.setRevisionNb(revisionNb);
            profileTypesMap.put(profileType.getId(), profileType);
        }
    }

    /**
     * Fill impactsMap with most recent impactValue
     *
     * @param revisionNb  the revision number of impactValue
     * @param impactValue the impactValue
     */
    private void saveImpactInMap(Integer revisionNb, ImpactValue impactValue) {
        if (!impactsMap.containsKey(impactValue.getId())) {
            impactValue.setRevisionNb(revisionNb);
            impactsMap.put(impactValue.getId(), impactValue);
        } else if (impactsMap.get(impactValue.getId()).getRevisionNb() < revisionNb) {
            impactValue.setRevisionNb(revisionNb);
            impactsMap.put(impactValue.getId(), impactValue);
        }
    }

    /**
     * Fill rulesMap with most recent rule
     *
     * @param revisionNb the revision number of rule
     * @param rule       the rule
     */
    private void saveRuleInMap(Integer revisionNb, Rule rule) {
        if (!rulesMap.containsKey(rule.getId())) {
            rule.setRevisionNb(revisionNb);
            rulesMap.put(rule.getId(), rule);
        } else if (rulesMap.get(rule.getId()).getRevisionNb() < revisionNb) {
            rule.setRevisionNb(revisionNb);
            rulesMap.put(rule.getId(), rule);
        }
    }

    /**
     * Fill chainsMap with most recent chain
     *
     * @param revisionNb the revision number of chain
     * @param chain      the chain
     */
    private void saveChainInMap(Integer revisionNb, Chain chain) {
        if (!chainsMap.containsKey(chain.getId())) {
            chain.setRevisionNb(revisionNb);
            chainsMap.put(chain.getId(), chain);
        } else if (chainsMap.get(chain.getId()).getRevisionNb() < revisionNb) {
            chain.setRevisionNb(revisionNb);
            chainsMap.put(chain.getId(), chain);
        }
    }

    /**
     * Fill questionsMap and subjectsMap with most recent question
     *
     * @param revisionNb the revision number of question
     * @param question   the question
     */
    private void saveQuestionInMaps(Integer revisionNb, Question question) {
        if (!questionsMap.containsKey(question.getId())) {
            question.setRevisionNb(revisionNb);
            questionsMap.put(question.getId(), question);
        } else if (questionsMap.get(question.getId()).getRevisionNb() < revisionNb) {
            question.setRevisionNb(revisionNb);
            questionsMap.put(question.getId(), question);
        }

        saveSubjectInMap(revisionNb, question.getSubject());
    }

    /**
     * Fill subjectsMap with most recent subject
     *
     * @param revisionNb the revision number of subject
     * @param subject    the subject
     */
    private void saveSubjectInMap(Integer revisionNb, Subject subject) {
        if (!subjectsMap.containsKey(subject.getId())) {
            subject.setRevisionNb(revisionNb);
            subjectsMap.put(subject.getId(), subject);
        } else if (subjectsMap.get(subject.getId()).getRevisionNb() < revisionNb) {
            subject.setRevisionNb(revisionNb);
            subjectsMap.put(subject.getId(), subject);
        }
    }

    /**
     * Fill equipmentsMap and categoriesMap with most recent equipment
     *
     * @param revisionNb the revision number of equipment
     * @param equipment  the equipment
     */
    private void saveEquipmentInMaps(Integer revisionNb, Equipment equipment) {
        if (!equipmentsMap.containsKey(equipment.getId())) {
            equipment.setRevisionNb(revisionNb);
            equipmentsMap.put(equipment.getId(), equipment);
        } else if (equipmentsMap.get(equipment.getId()).getRevisionNb() < revisionNb) {
            equipment.setRevisionNb(revisionNb);
            equipmentsMap.put(equipment.getId(), equipment);
        }

        saveCategoriesInMap(revisionNb, equipment.getCategories());
    }

    /**
     * Fill illustrationsMap with most recent illustrations from illustrations
     *
     * @param revisionNb    the revision number of illustrations
     * @param illustrations the illustrations
     */
    private void saveIllustrationsInMap(Integer revisionNb, List<Illustration> illustrations) {
        for (Illustration illustration : illustrations) {
            if (!illustrationsMap.containsKey(illustration.getId())) {
                illustration.setRevisionNb(revisionNb);
                illustrationsMap.put(illustration.getId(), illustration);
            } else if (revisionNb > illustrationsMap.get(illustration.getId()).getRevisionNb()) {
                illustration.setRevisionNb(revisionNb);
                illustrationsMap.put(illustration.getId(), illustration);
            }
        }
    }

    /**
     * Fill categoriesMap with most recent categories from categories
     *
     * @param revisionNb the revision number of categories
     * @param categories the categories
     */
    private void saveCategoriesInMap(Integer revisionNb, List<Category> categories) {
        for (Category category : categories) {
            if (!categoriesMap.containsKey(category.getId())) {
                category.setRevisionNb(revisionNb);
                categoriesMap.put(category.getId(), category);
            } else if (revisionNb > categoriesMap.get(category.getId()).getRevisionNb()) {
                category.setRevisionNb(revisionNb);
                categoriesMap.put(category.getId(), category);
            }
        }
    }

    /**
     * clear all maps
     */
    private void clearMaps() {
        questionsMap.clear();
        equipmentsMap.clear();
        rulesMap.clear();
        illustrationsMap.clear();
        rulesCategoriesMap.clear();
        profileTypesMap.clear();
        impactsMap.clear();
        subjectsMap.clear();
        categoriesMap.clear();
    }
}
