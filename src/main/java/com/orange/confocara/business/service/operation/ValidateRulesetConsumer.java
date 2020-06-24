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
import com.orange.confocara.common.binding.RulesetValidator;
import com.orange.confocara.business.service.utils.ErrorUtil;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Operation that validate and check errors from a {@link Ruleset}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ValidateRulesetConsumer implements Consumer<Ruleset> {

    @Autowired
    private final RulesetRepository rulesetRepository;

    @Autowired
    private final RulesetValidator validator;

    @Autowired
    private final ErrorUtil errorUtil;

    @Override
    public void accept(Ruleset input) {
        Ruleset ruleset = rulesetRepository.findOne(input.getId());

        BizErrors errors = new BizErrors();
        validator.validate(ruleset, errors);
        errorUtil.checkErrors(errors);
        log.info(
                "Message=Starting publication of a ruleset;RulesetId={};RulesetReference={};RulesetVersion={};",
                ruleset.getId(), ruleset.getReference(), ruleset.getVersion());
    }
}
