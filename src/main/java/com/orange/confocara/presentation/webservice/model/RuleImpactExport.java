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
import com.orange.confocara.connector.persistence.model.RuleImpact;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleImpactExport {
    private String reference;
    private String impactValueRef;
    private String profileTypeRef;

    public RuleImpactExport(RuleImpact ruleImpact) {
        impactValueRef = new ImpactValueExport(ruleImpact.getImpact()).getReference();
        profileTypeRef = new ProfileTypeExport(ruleImpact.getProfileType()).getReference();
        reference = String.valueOf(ruleImpact.getId());
    }
}
