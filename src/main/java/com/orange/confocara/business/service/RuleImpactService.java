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

import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.repository.RuleImpactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleImpactService {

    private final RuleImpactRepository ruleImpactRepository;

    @Transactional
    public List<RuleImpact> all() {
        return (List<RuleImpact>) ruleImpactRepository.findAll();
    }

    @Transactional
    public RuleImpact findById(Long id) {
        return ruleImpactRepository.findById(id);
    }

    @Transactional
    public RuleImpact create(RuleImpact ruleImpact) {
        return ruleImpactRepository.save(ruleImpact);
    }

    @Transactional
    public void delete(long id) {
        ruleImpactRepository.delete(id);
    }

    @Transactional
    public void delete(List<RuleImpact> ruleImpacts) {
        ruleImpactRepository.delete(ruleImpacts);
    }

    @Transactional
    public RuleImpact update(RuleImpact ruleImpact) {
        return ruleImpactRepository.save(ruleImpact);
    }

    /**
     * Find all rules impacts having given profile types
     *
     * @param profileTypes the profile types
     * @return all rules impacts having given profile types
     */
    public List<RuleImpact> generateRuleImpactsFromProfileTypes(List<ProfileType> profileTypes) {
        List<RuleImpact> ruleImpactList = new ArrayList<>();
        for (ProfileType profileType : profileTypes) {
            for (RulesCategory rulesCategory : profileType.getRulesCategories()) {
                RuleImpact ruleImpact = getRuleImpact(profileType, rulesCategory);

                ruleImpactList.add(ruleImpact);
            }
        }

        return ruleImpactList;
    }

    /**
     * Generates ruleImpacts for all profile types that do not have the same Rules category as rule
     * The rules impacts defined in the rule are keep
     *
     * @param rule the rule
     * @return all rule impacts
     */
    public List<RuleImpact> generateRuleImpactsFromProfileTypesWithRule(List<ProfileType> allProfileTypes, Rule rule) {
        List<RuleImpact> ruleImpactList = setRulesCategoryToRuleImpacts(rule);
        RulesCategory rulesCategory = rule.getRulesCategory();

        for (ProfileType profileType : allProfileTypes) {
            for (RulesCategory rulesCategoryFromProfileType : profileType.getRulesCategories()) {
                if (!rulesCategoryFromProfileType.getId().equals(rulesCategory.getId())) {
                    // if profile type has not the same rules category as the rule, then
                    // we should create the rule impact
                    RuleImpact ruleImpact = getRuleImpact(profileType, rulesCategoryFromProfileType);
                    ruleImpactList.add(ruleImpact);
                }
            }
        }

        return ruleImpactList;
    }

    public List<RuleImpact> setRulesCategoryToRuleImpacts(Rule rule) {
        List<RuleImpact> ruleImpacts = rule.getRuleImpacts();
        for (RuleImpact impact : ruleImpacts) {
            impact.setRulesCategory(rule.getRulesCategory());
        }

        return ruleImpacts;
    }

    private RuleImpact getRuleImpact(ProfileType profileType, RulesCategory rulesCategory) {
        RuleImpact ruleImpact = new RuleImpact();
        ruleImpact.setProfileType(profileType);
        ruleImpact.setImpact(rulesCategory.getDefaultImpact());
        ruleImpact.setRulesCategory(rulesCategory);
        return ruleImpact;
    }
}
