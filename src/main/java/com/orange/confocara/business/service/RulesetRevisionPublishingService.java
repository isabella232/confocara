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
import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Behaviour of a service that deals with the publication of {@link Ruleset}s
 *
 * Temporary service that aims to convert existing revisions which have no {@link PublishedRuleset}
 * yet.
 *
 * Should be tagged as deprecated as soon as all published revisions have been converted to {@link
 * PublishedRuleset}s.
 */
@FunctionalInterface
public interface RulesetRevisionPublishingService {

    /**
     * do the publication of a {@link Ruleset}
     *
     * @param reference identifier of a {@link Ruleset}
     */
    void generatePublishedRuleset(String reference, Integer version);

    /**
     * Default implementation of {@link RulesetRevisionPublishingService}
     */
    @Slf4j
    @RequiredArgsConstructor
    @Service
    class RulesetRevisionPublishingServiceImpl implements RulesetRevisionPublishingService {

        @Autowired
        private final BiFunction<Ruleset, BizErrors, PublishedRuleset> function;

        @Autowired
        private final RulesetPublishingRepository rulesetPublishingRepository;

        @Autowired
        private final RulesetService rulesetService;

        @Autowired
        private final ErrorUtil errorUtil;

        /**
         * @param reference identifier of a {@link Ruleset}
         */
        @Transactional
        @Override
        public void generatePublishedRuleset(String reference, Integer version) {
            // first step : validate that the ruleset has not already been published
            if (rulesetPublishingRepository.findOneByReferenceAndVersion(reference, version)
                    != null) {
                log.error(
                        "ErrorMessage=Publishing request has failed, because the ruleset was already published for reference and version;RequestReference={};RequestVersion={};",
                        reference, version);
                throw new BizException(ErrorCode.UNEXPECTED,
                        "Ruleset already published. Could not republish it.");
            }

            log.info(
                    "Message=Requesting publishing for reference and version;RequestReference={};RequestVersion={};",
                    reference, version);

            Ruleset ruleset = rulesetService
                    .findPublishedRulesetByReferenceAndVersion(reference, version);

            BizErrors errors = new BizErrors();
            PublishedRuleset result = function.apply(ruleset, errors);
            errorUtil.checkErrors(errors);

            PublishedRuleset savedEntity = rulesetPublishingRepository.save(result);
            log.info(
                    "Message=Saving the published version of the ruleset;PublishedRulesetId={};PublishedRulesetReference={};PublishedRulesetVersion={};",
                    savedEntity.getId(), savedEntity.getReference(), savedEntity.getVersion());
        }
    }
}
