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

import com.orange.confocara.connector.persistence.model.*;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RuleImpactService ruleImpactService;
    private final QuestionnaireObjectService questionnaireObjectService;
    private final QuestionRepository questionRepository;

    public List<Rule> all() {
        return ruleRepository.findAllByOrderByIdDesc();
    }

    public Rule withId(Long id) {
        return ruleRepository.findOne(id);
    }

    public List<Rule> withIds(List<Long> ids) {
        return (List<Rule>) ruleRepository.findAll(ids);
    }

    public List<Rule> withRulesCategory(RulesCategory rulesCategory) {
        return ruleRepository.findByRulesCategory(rulesCategory);
    }

    public Rule withReference(String label) {
        return ruleRepository.findByReference(label);
    }

    @Transactional
    public Rule create(Rule rule) {
        if (rule.getRuleImpacts() == null) {
            rule.setRuleImpacts(new ArrayList<>());
        }

        return ruleRepository.save(rule);
    }

    @Transactional
    public void delete(long id) {
        Rule rule = ruleRepository.findOne(id);
        List<Question> byRules = questionRepository.findByRules(rule);
        for (Question question : byRules) {
            question.getRules().remove(rule);
            if (question.getRules().isEmpty()) {
                String state = State.INACTIVE.toString().toLowerCase();
                question.setState(state);
            }
        }

        if (rule.getRuleImpacts() != null) {
            for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                ruleImpactService.delete(ruleImpact.getId());
            }
        }

        ruleRepository.delete(id);
    }

    @Transactional
    public Rule update(Rule rule) {
        if (rule.getRuleImpacts() == null) {
            rule.setRuleImpacts(new ArrayList<>());
        }
        deleteRemovedRuleImpacts(rule);
        return ruleRepository.save(rule);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeProfileTypeFromExistingRules(ProfileType profileType) {
        List<Rule> rules = all();
        List<Rule> ruleDbsToUpdate = getRuleDBsToUpdateOnRemoveProfileType(profileType, rules);

        ruleRepository.save(ruleDbsToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateImpactsOnDeleteImpact(ImpactValue impactValueToDelete, ImpactValue replacementImpactValue) {
        List<Rule> rules = all();
        for (Rule rule : rules) {
            for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                if (Objects.equals(ruleImpact.getImpact().getId(), impactValueToDelete.getId())) {
                    ruleImpact.setImpact(replacementImpactValue);
                }
            }
        }
        ruleRepository.save(rules);
    }

    @NonNull
    public List<QuestionnaireObject> getAssociatedQuestionnaires(@NonNull String ref) {
        return questionnaireObjectService.withRule(withReference(ref));
    }

    @NonNull
    public Map<String, List<Rule>> getRulesByRulesCategoryNameMap(@NonNull List<Rule> associatedRules) {
        Map<String, List<Rule>> associatedRulesMap = new HashMap<>();

        for (Rule rule : associatedRules) {
            if (!associatedRulesMap.containsKey(rule.getRulesCategory().getName())) {
                List<Rule> rulesByRulesCategory = new ArrayList<>();
                rulesByRulesCategory.add(rule);
                associatedRulesMap.put(rule.getRulesCategory().getName(), rulesByRulesCategory);
            } else {
                associatedRulesMap.get(rule.getRulesCategory().getName()).add(rule);
            }
        }

        return associatedRulesMap;
    }

    /**
     * Validates if a rule contains at least one impact that is not "no impact"
     *
     * @param rule the rule to validate
     * @return true if the rule has one impact different than "no impact"
     */
    public boolean containsPertinentImpact(@NonNull Rule rule) {
        boolean result = false;
        String noImpactName = ImpactValueService.NO_IMPACT;

        if (rule.getRuleImpacts() != null) {
            for (RuleImpact impact : rule.getRuleImpacts()) {
                if (!impact.getImpact().getName().equals(noImpactName)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * called when profile type list has changed in rules category
     * the rule impacts associated to removed profile types must be deleted
     * the rule impacts associated to new profile types must be created
     *
     * @param oldProfileTypes the old profile types of edited rules category
     * @param profileTypeList the new profile types of edited rules category
     * @param rulesCategoryId the edited rules category id
     */
    void updateImpactsOnEditRulesCategory(List<ProfileType> oldProfileTypes, List<ProfileType> profileTypeList, Long rulesCategoryId) {
        List<ProfileType> commonProfileTypes = new ArrayList<>(profileTypeList);
        commonProfileTypes.retainAll(oldProfileTypes);

        List<Rule> rules = all();
        List<Rule> rulesToUpdate = new ArrayList<>();

        for (Rule rule : rules) {
            if (rule.getRulesCategory().getId().equals(rulesCategoryId)) {
                // remove rule impact associated to removed profile type
                boolean isModified = false;

                Iterator<RuleImpact> iterator = rule.getRuleImpacts().iterator();

                while (iterator.hasNext()) {
                    RuleImpact ruleImpact = iterator.next();
                    if (!commonProfileTypes.contains(ruleImpact.getProfileType())) {
                        iterator.remove();
                        isModified = true;
                    }
                }

                for (ProfileType profileType : profileTypeList) {
                    if (!commonProfileTypes.contains(profileType)) {
                        // new profile type
                        rule.getRuleImpacts().add(getRuleImpactWithDefaultImpactValue(profileType, rule.getRulesCategory().getDefaultImpact()));
                        isModified = true;
                    }
                }

                if (isModified) {
                    rulesToUpdate.add(rule);
                }
            }
        }
        ruleRepository.save(rulesToUpdate);
    }

    /**
     * the rule list that contains the rules having impact values that were removed from Rules Category's
     * accepted impacts, and these impact values are used in rule impacts in which the profile types has this rules category.
     *
     * @param oldRulesCategory   the rules category stored in database (before update)
     * @param newImpactValueList the impact values list that would be applied to oldRulesCategory
     *                           if there is no conflict (no removed impact value is used in rule impacts
     *                           in which the profile types has this rules category)
     * @param newProfileTypeList the profile types list that would be applied to oldRulesCategory
     *                           if there is no conflict (no removed impact value is used in rule impacts
     *                           in which the profile types has this rules category)
     * @return the rule list that contains the rules having impact values that were removed from Rules Category's
     * accepted impacts, and these impact values are used in rule impacts in which the profile types has this rules category.
     */
    public List<Rule> getRulesWithAffectedRemovedImpactValue(RulesCategory oldRulesCategory,
                                                             List<ImpactValue> newImpactValueList,
                                                             List<ProfileType> newProfileTypeList) {
        List<Rule> conflictualRules = new ArrayList<>();
        List<ImpactValue> removedImpactValues = getRemovedImpactValues(oldRulesCategory, newImpactValueList);
        List<ProfileType> removedProfileTypes = getRemovedProfileTypes(oldRulesCategory, newProfileTypeList);

        if (!removedImpactValues.isEmpty()) {
            conflictualRules = getRulesUsingRemovedImpactValues(oldRulesCategory, removedImpactValues, removedProfileTypes);
        }
        return conflictualRules;
    }


    public List<Rule> getRulesWithRemovedProfileType(RulesCategory oldRulesCategory,
                                                     List<ProfileType> newProfileTypeList) {
        List<Rule> conflictualRules = new ArrayList<>();
        List<ProfileType> removedProfileTypes = getRemovedProfileTypes(oldRulesCategory, newProfileTypeList);

        if (!removedProfileTypes.isEmpty()) {
            conflictualRules = getConflictualRulesUsingRemovedProfileType(oldRulesCategory, removedProfileTypes);
        }
        return conflictualRules;
    }

    private List<ImpactValue> getRemovedImpactValues(RulesCategory oldRulesCategory, List<ImpactValue> newImpactValueList) {
        List<ImpactValue> removedImpactValues = new ArrayList<>();
        List<ImpactValue> oldImpactValues = oldRulesCategory.getImpactValues();
        for (ImpactValue oldImpactValue : oldImpactValues) {
            if (!newImpactValueList.contains(oldImpactValue)) {
                removedImpactValues.add(oldImpactValue);
            }
        }
        return removedImpactValues;
    }

    private List<ProfileType> getRemovedProfileTypes(RulesCategory oldRulesCategory, List<ProfileType> newProfileTypeList) {
        List<ProfileType> removedProfileTypes = new ArrayList<>();
        List<ProfileType> oldProfileTypes = oldRulesCategory.getProfileTypes();
        for (ProfileType oldProfileType : oldProfileTypes) {
            if (!newProfileTypeList.contains(oldProfileType)) {
                removedProfileTypes.add(oldProfileType);
            }
        }
        return removedProfileTypes;
    }

    private List<Rule> getRulesUsingRemovedImpactValues(RulesCategory oldRulesCategory,
                                                        List<ImpactValue> removedImpactValues,
                                                        List<ProfileType> removedProfileTypes) {
        List<Rule> conflictualRules = new ArrayList<>();
        for (ImpactValue removedImpactValue : removedImpactValues) {
            for (Rule rule : withRulesCategory(oldRulesCategory)) {
                for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                    if (ruleImpact.getImpact().getId().equals(removedImpactValue.getId())
                            && !removedProfileTypes.contains(ruleImpact.getProfileType())
                            && !conflictualRules.contains(rule)) {
                        conflictualRules.add(rule);
                        break;
                    }
                }
            }
        }

        return conflictualRules;
    }

    private List<Rule> getConflictualRulesUsingRemovedProfileType(RulesCategory oldRulesCategory,
                                                                  List<ProfileType> removedProfileTypes) {

        List<Rule> conflictualRules = new ArrayList<>();
        List<Rule> rules = withRulesCategory(oldRulesCategory);

        for (ProfileType removedProfileType : removedProfileTypes) {
            for (Rule rule : rules) {
                if (!conflictualRules.contains(rule)) {
                    boolean hasOnlyNoImpactValue = true;
                    for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                        if (!ruleImpact.getProfileType().getId().equals(removedProfileType.getId())
                                && !ruleImpact.getImpact().getName().equals(ImpactValueService.NO_IMPACT)) {
                            hasOnlyNoImpactValue = false;
                            break;
                        }
                    }

                    if (hasOnlyNoImpactValue) {
                        conflictualRules.add(rule);
                    }
                }
            }
        }

        return conflictualRules;
    }

    private RuleImpact getRuleImpactWithDefaultImpactValue(ProfileType profileType, ImpactValue defaultImpact) {
        RuleImpact ruleImpact = new RuleImpact();
        ruleImpact.setProfileType(profileType);
        ruleImpact.setImpact(defaultImpact);

        return ruleImpact;
    }

    private List<Rule> getRuleDBsToUpdateOnRemoveProfileType(ProfileType profileType, List<Rule> rules) {
        List<Rule> ruleDbsToUpdate = new ArrayList<>();
        for (Rule rule : rules) {
            List<RuleImpact> ruleImpacts = rule.getRuleImpacts();
            for (RuleImpact ruleImpact : ruleImpacts) {
                if (Objects.equals(ruleImpact.getProfileType().getId(), profileType.getId())) {
                    ruleImpacts.remove(ruleImpact);
                    ruleDbsToUpdate.add(rule);
                    ruleImpactService.delete(ruleImpact.getId());
                    break;
                }
            }
        }

        return ruleDbsToUpdate;
    }

    private void deleteRemovedRuleImpacts(Rule rule) {
        List<RuleImpact> impactsToDelete = new ArrayList<>();
        List<Long> ruleImpactIds = rule.getRuleImpacts().stream().map(RuleImpact::getId).collect(Collectors.toList());
        Rule oldRule = withId(rule.getId());

        for (RuleImpact impact : oldRule.getRuleImpacts()) {
            if (!ruleImpactIds.contains(impact.getId())) {
                impactsToDelete.add(impact);
            }
        }

        if (!impactsToDelete.isEmpty()) {
            ruleImpactService.delete(impactsToDelete);
        }
    }
}
