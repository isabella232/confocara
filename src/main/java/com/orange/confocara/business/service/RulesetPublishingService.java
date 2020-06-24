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

import com.orange.confocara.business.service.operation.SavePublishedRulesetConsumer;
import com.orange.confocara.business.service.operation.UpdateRulesetAsNewDraftConsumer;
import com.orange.confocara.business.service.operation.UpdateRulesetAsPublishedConsumer;
import com.orange.confocara.business.service.operation.ValidateRulesetConsumer;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Behaviour of a service that deals with the publication of {@link Ruleset}s
 */
@FunctionalInterface
public interface RulesetPublishingService {

    /**
     * do the publication of a {@link Ruleset}
     *
     * @param id unique identifier of a {@link Ruleset}
     */
    void publishRuleset(long id);

    /**
     * Default implementation of {@link RulesetPublishingService}
     */
    @Slf4j
    @RequiredArgsConstructor
    @Service
    class RulesetPublishServiceImpl implements RulesetPublishingService {

        @Autowired
        private final RulesetRepository rulesetRepository;

        @Autowired
        private final ValidateRulesetConsumer validateRulesetConsumer;

        @Autowired
        private final UpdateRulesetAsPublishedConsumer updateRulesetAsPublishedConsumer;

        @Autowired
        private final SavePublishedRulesetConsumer savePublishedRulesetConsumer;

        @Autowired
        private final UpdateRulesetAsNewDraftConsumer updateRulesetAsNewDraftConsumer;

        @Override
        public void publishRuleset(long id) {

            log.info("Message=Starting the publication of a ruleset;RulesetId={}", id);

            Stream.of(id)
                    .filter(rulesetRepository::exists)
                    // first step : retrieving the ruleset defined by the id
                    .map(rulesetRepository::findOne)
                    // second step : validating the retrieved object is definitely a ruleset
                    .peek(validateRulesetConsumer)
                    // third step : publishing the ruleset
                    .peek(updateRulesetAsPublishedConsumer)
                    // forth step : transforming and saving the ruleset in a dedicated repository, for further readings
                    .peek(savePublishedRulesetConsumer)
                    // fifth step : making the ruleset editable again
                    .peek(updateRulesetAsNewDraftConsumer)
                    // last step : triggering the processing of the previous steps. Does nothing but logging.
                    .forEach(ruleset -> log.info(
                            "Message=Ruleset has been successfully published;RulesetId={};RulesetReference={};RulesetVersion={};",
                            ruleset.getId(), ruleset.getReference(), ruleset.getVersion()));
        }
    }
}
