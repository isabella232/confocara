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

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that deals with {@link RulesCategory} elements
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RulesCategoryService {

    private final RulesCategoryRepository rulesCategoryRepository;
    private final RuleService ruleService;
    private final QuestionService questionService;
    private final ChainService chainService;
    private final QuestionnaireObjectService questionnaireService;
    private final RulesetRepository rulesetRepository;

    @Transactional
    public List<RulesCategory> all() {
        return rulesCategoryRepository.findAll();
    }

    @Transactional
    public RulesCategory findByName(String name) {
        return rulesCategoryRepository.findByName(name);
    }

    @Transactional
    public RulesCategory withId(long id) {
        return rulesCategoryRepository.findOne(id);
    }

    @Transactional
    public List<RulesCategory> withName(String name) {
        return rulesCategoryRepository.filterWithName(name);
    }

    @Transactional
    public List<RulesCategory> withImpactValue(ImpactValue impact) {
        return rulesCategoryRepository.findByImpactValues(impact);
    }

    @Transactional
    public List<RulesCategory> withProfileType(ProfileType profileType) {
        return rulesCategoryRepository.findByProfileTypes(profileType);
    }

    @Transactional
    public boolean isAvailable(@NonNull String name) {
        RulesCategory rulesCategory = rulesCategoryRepository.findByName(name.trim().replaceAll("\\s+", " "));
        return rulesCategory == null;
    }

    @Transactional
    public RulesCategory create(@NonNull RulesCategory rulesCategory) {
        return rulesCategoryRepository.save(rulesCategory);
    }

    /**
     * Deletes the rules category with id
     * Before calling this method, please make sure that the rules category is not used
     * in rule, question, chain, questionnaire or ruleset to prevent errors
     *
     * @param id the rules category id
     */
    @Transactional
    public void delete(long id) {
        RulesCategory rulesCategory = rulesCategoryRepository.findOne(id);

        List<ProfileType> profileTypes = rulesCategory.getProfileTypes();
        for (ProfileType profileType : profileTypes) {
            profileType.getRulesCategories().remove(rulesCategory);
        }

        rulesCategoryRepository.delete(id);
    }

    @Transactional
    public RulesCategory update(@NonNull RulesCategory rulesCategory) {
        return rulesCategoryRepository.save(rulesCategory);
    }

    @Transactional
    public List<RulesCategory> update(@NonNull List<RulesCategory> rulesCategories) {
        return (List<RulesCategory>) rulesCategoryRepository.save(rulesCategories);
    }

    /**
     * Gets the rulesets names that use rules category
     *
     * @param rulesCategory the rules category
     * @return the rulesets names that use rules category
     */
    @Transactional
    public List<String> getRulesetsNameWithRulesCategory(@NonNull RulesCategory rulesCategory) {
        List<String> conflictualRulesets = new ArrayList<>();
        List<Ruleset> rulesets = rulesetRepository.findByRulesCategory(rulesCategory);

        for (Ruleset ruleset : rulesets) {
            conflictualRulesets.add(ruleset.getType());
        }

        return conflictualRulesets;
    }

    /**
     * Gets the questionnaires names that use rules category
     *
     * @param rulesCategory the rules category
     * @return the questionnaires names that use rules category
     */
    @Transactional
    public List<String> getQuestionnairesObjectNameWithRulesCategory(@NonNull RulesCategory rulesCategory) {
        List<String> conflictualQuestionnaires = new ArrayList<>();
        List<QuestionnaireObject> questionnaireObjects = questionnaireService.withRulesCategory(rulesCategory);

        for (QuestionnaireObject qo : questionnaireObjects) {
            conflictualQuestionnaires.add(qo.getName());
        }

        return conflictualQuestionnaires;
    }

    /**
     * Gets the chains names that use rules category
     *
     * @param rulesCategory the rules category
     * @return the chains names that use rules category
     */
    @Transactional
    public List<String> getChainsNameWithRulesCategory(@NonNull RulesCategory rulesCategory) {
        List<String> conflictualChains = new ArrayList<>();
        List<Chain> chains = chainService.withRulesCategory(rulesCategory);

        for (Chain chain : chains) {
            conflictualChains.add(chain.getName());
        }

        return conflictualChains;
    }

    /**
     * Gets the questions names that use rules category
     *
     * @param rulesCategory the rules category
     * @return the questions names that use rules category
     */
    @Transactional
    public List<String> getQuestionsNameWithRulesCategory(@NonNull RulesCategory rulesCategory) {
        List<String> conflictualQuestions = new ArrayList<>();
        List<Question> questions = questionService.withRulesCategory(rulesCategory);

        for (Question q : questions) {
            conflictualQuestions.add(q.getLabel());
        }

        return conflictualQuestions;
    }

    /**
     * Gets the rules names that use rules category
     *
     * @param rulesCategory the rules category
     * @return the rules names that use rules category
     */
    @Transactional
    public List<String> getRulesLabelWithRulesCategory(@NonNull RulesCategory rulesCategory) {
        List<String> conflictualRules = new ArrayList<>();
        List<Rule> ruleList = ruleService.withRulesCategory(rulesCategory);

        for (Rule rule : ruleList) {
            conflictualRules.add(rule.getLabel());
        }

        return conflictualRules;
    }

    /**
     * returns rulesCategory impact values mapped by id
     *
     * @param rulesCategory the rules Category
     * @return rulesCategory impact values mapped by id
     */
    public Map<Long, ImpactValue> getImpactValuesMap(RulesCategory rulesCategory) {
        List<ImpactValue> allImpactValues = rulesCategory.getImpactValues();
        Map<Long, ImpactValue> impactValuesMap = new HashMap<>();

        for (ImpactValue impactValue : allImpactValues) {
            impactValuesMap.put(impactValue.getId(), impactValue);
        }

        return impactValuesMap;
    }

    /**
     * Remove profileTypeToDelete from all rules categories having it
     *
     * @param profileTypeToDelete the profileType to delete
     */
    @Transactional
    public void removeProfileTypeFromRulesCategories(ProfileType profileTypeToDelete) {
        List<RulesCategory> rulesCategoriesToUpdate = new ArrayList<>();
        for (RulesCategory rulesCategory : withProfileType(profileTypeToDelete)) {
            rulesCategory.getProfileTypes().remove(profileTypeToDelete);
            rulesCategoriesToUpdate.add(rulesCategory);
        }

        rulesCategoryRepository.save(rulesCategoriesToUpdate);
    }

    void updateImpactsOnDeleteImpact(List<RulesCategory> rulesCategoriesToUpdate,
                                     ImpactValue impactValueToDelete,
                                     ImpactValue replacementImpactValue) {
        ruleService.updateImpactsOnDeleteImpact(impactValueToDelete, replacementImpactValue);

        for (RulesCategory rulesCategory : rulesCategoriesToUpdate) {
            if (Objects.equals(rulesCategory.getDefaultImpact().getId(), impactValueToDelete.getId())) {
                // default impact value
                rulesCategory.setDefaultImpact(replacementImpactValue);
            }

            rulesCategory.getImpactValues().remove(impactValueToDelete);

            if (!rulesCategory.getImpactValues().contains(replacementImpactValue)) {
                rulesCategory.getImpactValues().add(replacementImpactValue);
            }

            update(rulesCategory);
        }
    }
}
