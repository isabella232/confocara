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

package com.orange.confocara.presentation.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orange.confocara.business.service.utils.DateUtils;
import com.orange.confocara.connector.persistence.model.Rule;
import java.util.stream.Collectors;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleExport {
    private String reference;
    private String label;
    private String origin;
    private UserCredentialsWS userCredentials;
    private List<String> illustration;
    private List<RuleImpactExport> ruleImpacts;
    private String date;

    public RuleExport(Rule rule) {
        reference = rule.getReference();
        label = rule.getLabel();
        userCredentials = UserCredentialsWS.newInstance(rule.getUser());
        illustration = rule.getIllustrations().stream().map(IllustrationWS::new).map(IllustrationWS::getReference).collect(
                Collectors.toList());
        ruleImpacts = rule.getRuleImpacts().stream().map(RuleImpactExport::new).collect(Collectors.toList());
        date = DateUtils.format(rule.getDate());
    }
}
