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

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Behaviour of a service that manages the import of {@link Ruleset}s
 */
@FunctionalInterface
public interface RulesetImportService {

    Ruleset createRuleset(Ruleset ruleset);

    /**
     * Default implementation of {@link RulesetImportService}
     */
    @Slf4j
    @Service
    class RulesetImportServiceImpl implements RulesetImportService {

        @Autowired
        private RulesetRepository rulesetRepository;

        @Override
        @Transactional
        public Ruleset createRuleset(Ruleset ruleset) {

            if (rulesetRepository.exists(ruleset.getId())) {
                throw new BizException(ErrorCode.UNEXPECTED, "Ruleset already exists.");
            }
            return rulesetRepository.save(ruleset);
        }
    }
}
