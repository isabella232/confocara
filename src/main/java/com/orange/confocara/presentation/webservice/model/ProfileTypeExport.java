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
import com.orange.confocara.connector.persistence.model.ProfileType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ProfileTypeExport {
    private String name;
    private String reference;
    private String icon;
    private List<RulesCategoryExport> rulesCategories;

    public ProfileTypeExport(ProfileType profileType) {
        reference = profileType.getReference();
        name = profileType.getName();
        icon = profileType.getIcon().getFileNameWithExtension();

        rulesCategories = profileType
                .getRulesCategories()
                .stream()
                .map(RulesCategoryExport::new)
                .collect(Collectors.toList());

        rulesCategories = profileType.getRulesCategories().stream().map(RulesCategoryExport::new).collect(Collectors.toList());
    }
}
