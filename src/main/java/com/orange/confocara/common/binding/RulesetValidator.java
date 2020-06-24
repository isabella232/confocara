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

package com.orange.confocara.common.binding;

import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetPublishingRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RulesetValidator implements BizValidator {

    @Autowired
    private final RulesetRepository rulesetRepository;

    @Autowired
    private final RulesetPublishingRepository rulesetPublishingRepository;

    @Transactional(readOnly = true)
    @Override
    public void validate(Object target, BizErrors errors) {
        Ruleset input = (Ruleset) target;

        if (input == null) {
            log.warn("WarnMessage=the ruleset does not exist");
            errors.reject(ErrorCode.NOT_FOUND, "the ruleset does not exist");
        } else {

            Ruleset ruleset = rulesetRepository.findOne(input.getId());
            if (ruleset == null) {
                log.warn(
                        "WarnMessage=the ruleset does not exist;RulesetId={};", input.getId());
                errors.reject(ErrorCode.NOT_FOUND, "the ruleset is missing");
            } else if (ruleset.getQuestionnaireObjects() == null
                    || ruleset.getQuestionnaireObjects().isEmpty()) {
                log.warn(
                        "WarnMessage=the ruleset is not well formatted;RulesetReference={};RulesetVersion={};",
                        input.getReference(), input.getVersion());
                errors.reject(ErrorCode.INVALID, "the ruleset is not well formatted");
            } else if (rulesetPublishingRepository
                    .findOneByReferenceAndVersion(ruleset.getReference(), ruleset.getVersion())
                    != null) {
                log.warn(
                        "WarnMessage=the ruleset has already been published;RulesetReference={};RulesetVersion={};",
                        ruleset.getReference(), ruleset.getVersion());
                errors.reject(ErrorCode.CONFLICT, "the ruleset has already been published");
            }
        }
    }
}
