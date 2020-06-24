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
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionnaireExport {

    private String reference;
    private Integer version;
    private String name;
    private String objectDescriptionRef;
    private List<ChainExport> chains;
    private List<String> parentObjectRefs;
    private String date;

    public QuestionnaireExport(QuestionnaireObject questionnaireObject) {

        reference = questionnaireObject.getReference();
        objectDescriptionRef = questionnaireObject.getEquipment().getReference();
        chains = questionnaireObject.getChains().stream().map(ChainExport::new).collect(
                Collectors.toList());
        date = DateUtils.format(questionnaireObject.getDate());
        version = questionnaireObject.getVersion();
        parentObjectRefs = questionnaireObject.getParentObjectRefs();
    }
}
