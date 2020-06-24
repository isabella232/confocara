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

package com.orange.confocara.business.service.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.confocara.common.binding.BizErrors;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.business.service.utils.JacksonUtil;
import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.presentation.webservice.model.RuleSetExport;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * a function that transforms a {@link Ruleset} into a {@link PublishedRuleset}, and populates an
 * {@link BizErrors} storage, if it encounters issues
 */
@Service
@Slf4j
public class RulesetToPublishedRulesetFunction implements
        BiFunction<Ruleset, BizErrors, PublishedRuleset> {

    /**
     * Function that converts a {@link Ruleset} into a {@link RuleSetExport}
     */
    private final Function<Ruleset, RuleSetExport> function;

    /**
     * Tool for writing JSON
     */
    private final ObjectMapper objectMapper;

    public RulesetToPublishedRulesetFunction(JacksonUtil jacksonUtil) {
        function = new RulesetToRulesetExportFunction();
        objectMapper = jacksonUtil.buildObjectMapper();
    }

    @Override
    public PublishedRuleset apply(Ruleset ruleset, BizErrors errors) {
        PublishedRuleset output = null;
        try {
            RuleSetExport export = function.apply(ruleset);
            String content = objectMapper.writeValueAsString(export);
            output = PublishedRuleset
                    .builder()
                    .reference(ruleset.getReference())
                    .version(ruleset.getVersion())
                    .content(content)
                    .creationDate(new Date())
                    .build();

        } catch (Exception ex) {
            log.error("Publication failed", ex);
            errors.reject(ErrorCode.UNEXPECTED, ex.getMessage());
        }
        return output;
    }
}
