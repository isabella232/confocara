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
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Equipment;
import java.util.stream.Collectors;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectDescriptionExport {
    private String reference;
    private String name;
    private String icon;
    private String definition;
    private String type;

    // the questionnaire linked directly with the ruleset
    private String questionnaireRef;

    protected List<String> illustrationRef;
    private List<CategoryWS> categories;
    private List<String> subObject = new ArrayList<>();
    private String date;

    public ObjectDescriptionExport(Equipment equipment, String questionnaireRef) {
        reference = equipment.getReference();
        name = equipment.getName();
        icon = equipment.getIcon().getFileNameWithExtension();
        definition = equipment.getDefinition();
        type = equipment.getType();

        this.questionnaireRef = questionnaireRef;

        illustrationRef = equipment.getIllustrations().stream().map(IllustrationWS::new)
                .map(IllustrationWS::getReference)
                .collect(Collectors.toList());

        categories = equipment.getCategories().stream().map(Category::getName).map(CategoryWS::new).collect(Collectors.toList());

        subObject = equipment.getSubobjects().stream().map(Equipment::getReference).collect(Collectors.toList());

        date = DateUtils.format(equipment.getDate());
    }
}
