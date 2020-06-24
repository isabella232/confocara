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

package com.orange.confocara.business.service.operation;

import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operation that transform and save a {@link Ruleset} into a repository dedicated to published
 * versions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SavePublishedRulesetConsumer implements Consumer<Ruleset> {

    @Autowired
    private final BiFunction<Ruleset, BizErrors, PublishedRuleset> function;

    @Autowired
    private final RulesetRepository rulesetRepository;

    @Autowired
    private final RulesetPublishingRepository rulesetPublishingRepository;

    @Autowired
    private final ErrorUtil errorUtil;

    @Transactional
    @Override
    public void accept(Ruleset input) {
        Ruleset ruleset = rulesetRepository.findOne(input.getId());

        BizErrors errors = new BizErrors();
        PublishedRuleset result = function.apply(ruleset, errors);
        errorUtil.checkErrors(errors);

        PublishedRuleset savedEntity = rulesetPublishingRepository.save(result);
        log.info(
                "Message=Saving the published version of the ruleset;PublishedRulesetId={};PublishedRulesetReference={};PublishedRulesetVersion={};",
                savedEntity.getId(), savedEntity.getReference(), savedEntity.getVersion());
    }
}
