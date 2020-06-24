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
import com.orange.confocara.connector.persistence.model.RulesCategory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RulesCategoryExport {
    private String name;
    private List<String> acceptedImpactList;
    private String defaultImpact;

    public RulesCategoryExport(RulesCategory rulesCategory) {
        name = rulesCategory.getName();
        defaultImpact = new ImpactValueExport(rulesCategory.getDefaultImpact()).getReference();


        acceptedImpactList = rulesCategory
                .getImpactValues()
                .stream()
                .map(ImpactValueExport::new)
                .map(ImpactValueExport::getReference)
                .collect(Collectors.toList());

    }
}
