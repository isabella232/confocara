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

import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operation that update a {@link Ruleset} into its published state
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateRulesetAsPublishedConsumer implements Consumer<Ruleset> {

    @Autowired
    private final RulesetRepository rulesetRepository;

    @Transactional
    @Override
    public void accept(Ruleset input) {
        Ruleset ruleset = rulesetRepository.findOne(input.getId());
        log.info(
                "Message=Publishing the ruleset;RulesetId={};RulesetReference={};RulesetVersion={};",
                ruleset.getId(), ruleset.getReference(), ruleset.getVersion());
        ruleset.markAsPublished();
    }
}
