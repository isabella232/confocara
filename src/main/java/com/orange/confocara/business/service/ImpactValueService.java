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

import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.presentation.view.controller.helper.ImpactValueReplacementError;
import com.orange.confocara.presentation.view.controller.helper.ImpactValueReplacementHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImpactValueService {

    private final ImpactValueRepository impactValueRepository;
    private final RulesCategoryService rulesCategoryService;
    private final RuleService ruleService;

    public static final String NO_IMPACT = "Non concerné";
    public static final String NO_IMPACT_ID = "1";
    public static final String EMBARRASSING = "Gênant";
    public static final String BLOCKING = "Bloquant";

    @PostConstruct
    void init() {
        createAndPersistImpacts();
    }

    /**
     * init database with noImpact, embarrassing and blocking values
     * (not editable values)
     */
    @Transactional
    public void createAndPersistImpacts() {
        if (all().isEmpty()) {
            ImpactValue noImpact = new ImpactValue();
            ImpactValue blocking = new ImpactValue();
            ImpactValue embarrassing = new ImpactValue();

            noImpact.setName(NO_IMPACT);
            noImpact.setEditable(false);
            impactValueRepository.save(noImpact);

            embarrassing.setName(EMBARRASSING);
            embarrassing.setEditable(false);
            impactValueRepository.save(embarrassing);

            blocking.setName(BLOCKING);
            blocking.setEditable(false);
            impactValueRepository.save(blocking);
        }
    }

    @Transactional
    public List<ImpactValue> all() {
        return (List<ImpactValue>) impactValueRepository.findAll();
    }

    @Transactional
    public ImpactValue withId(long id) {
        return impactValueRepository.findOne(id);
    }

    @Transactional
    public List<ImpactValue> withIds(List<Long> ids) {
        return (List<ImpactValue>) impactValueRepository.findAll(ids);
    }

    @Transactional
    public List<ImpactValue> withName(String name) {
        return impactValueRepository.filterWithName(name.trim().replaceAll("\\s+", " "));
    }

    @Transactional
    public ImpactValue findByName(String name) {
        return impactValueRepository.findByName(name);
    }

    @Transactional
    public boolean isAvailable(String name) {
        ImpactValue impactValue = impactValueRepository.findByName(name.trim().replaceAll("\\s+", " "));
        return impactValue == null;
    }

    @Transactional
    public ImpactValue create(@NonNull ImpactValue impactValue) {
        ImpactValue byName = impactValueRepository.findByName(impactValue.getName());
        if (byName == null) {
            return impactValueRepository.save(impactValue);
        }

        return byName;
    }

    @Transactional
    public ImpactValue update(@NonNull ImpactValue impactValue) {
        return impactValueRepository.save(impactValue);
    }

    @Transactional
    public boolean delete(long id) {
        boolean isDeleted = false;
        ImpactValue impactValueToDelete = withId(id);
        List<RulesCategory> conflictualRuleCategories = conflictualRuleCategories(impactValueToDelete);

        if (conflictualRuleCategories.isEmpty()) {
            impactValueRepository.delete(id);
            isDeleted = true;
        }

        return isDeleted;
    }

    /**
     * replaces impact value with impactToDeleteId with impact value with replacementImpactId
     *
     * if replacementImpactId == noImpactId and if there is at least one rules category
     * that only have impactToDelete and "no impact" in its impact value list,
     * then the impact can not be replaced
     *
     * @param replacementImpactId the replacement impact value id
     * @param impactToDeleteId    the impact to delete id
     * @return the first conflictual rules category name if there is at least 1 conflictual rules category
     * null if impact is deleted and replaced
     */
    @Transactional
    public ImpactValueReplacementHelper replaceImpactInRulesCategoryOnDelete(Long replacementImpactId, Long impactToDeleteId) {
        ImpactValue impactToDelete = withId(impactToDeleteId);
        ImpactValue replacementImpact = withId(replacementImpactId);

        List<RulesCategory> conflictualRuleCategories = conflictualRuleCategories(impactToDelete);

        if (replacementImpactId.equals(findByName(NO_IMPACT).getId())) {
            for (RulesCategory rulesCategory : conflictualRuleCategories) {
                // check if RC is valid
                if (rulesCategory.getImpactValues().size() == 2) {
                    return new ImpactValueReplacementHelper(ImpactValueReplacementError.RULES_CATEGORY, rulesCategory.getName());
                }

                // check if there is one invalid rule for this RC
                for (Rule rule : ruleService.withRulesCategory(rulesCategory)) {
                    boolean isRuleValid = false;
                    for (RuleImpact impact : rule.getRuleImpacts()) {
                        if (!impact.getImpact().getId().equals(impactToDeleteId)
                                && !impact.getImpact().getName().equals(NO_IMPACT)) {
                            isRuleValid = true;
                            break;
                        }
                    }
                    if (!isRuleValid) {
                        return new ImpactValueReplacementHelper(ImpactValueReplacementError.RULE, rule.getReference());
                    }
                }
            }
        }

        rulesCategoryService.updateImpactsOnDeleteImpact(conflictualRuleCategories, impactToDelete, replacementImpact);

        impactValueRepository.delete(impactToDeleteId);
        return null;
    }

    /**
     * gets the list of rules category that contains impactValueToDelete in their impact value list
     *
     * @param impactValueToDelete the impact value
     * @return the list of rules category that contains impactValueToDelete in their impact value list
     */
    private List<RulesCategory> conflictualRuleCategories(ImpactValue impactValueToDelete) {
        List<RulesCategory> conflictualRuleCategories = new ArrayList<>();
        conflictualRuleCategories.addAll(rulesCategoryService.withImpactValue(impactValueToDelete));

        return conflictualRuleCategories;
    }
}
