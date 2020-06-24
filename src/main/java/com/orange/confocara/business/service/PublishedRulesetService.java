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

import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.common.binding.PublishedRulesetValidator;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Behaviour of a service that queries for {@link PublishedRuleset}s
 */
@FunctionalInterface
public interface PublishedRulesetService {

    /**
     * Retrieves informations about a ruleset
     *
     * @param reference a {@link com.orange.confocara.connector.persistence.model.Ruleset#reference}
     * @param version a {@link com.orange.confocara.connector.persistence.model.Ruleset#version}
     * @return a {@link PublishedRuleset}
     */
    PublishedRuleset retrieveRuleset(String reference, Integer version);

    /**
     * Default implementation of {@link PublishedRulesetService}
     */
    @Slf4j
    @Service
    @RequiredArgsConstructor
    final class PublishedRulesetServiceImpl implements PublishedRulesetService {

        private final RulesetPublishingRepository rulesetPublishingRepository;

        private final PublishedRulesetValidator validator;

        private final ErrorUtil errorUtil;

        @Override
        public PublishedRuleset retrieveRuleset(String reference, Integer version) {

            PublishedRuleset ruleset = rulesetPublishingRepository
                    .findOneByReferenceAndVersion(reference, version);

            BizErrors errors = new BizErrors();

            validator.validate(ruleset, errors);
            errorUtil.checkErrors(errors);

            log.info(
                    "Message=A query for a published ruleset returned one with id={};RulesetReference={};RulesetVersion={}",
                    ruleset.getId(), reference, version);

            return ruleset;
        }
    }
}
