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
import com.orange.confocara.connector.persistence.model.Chain;
import java.util.stream.Collectors;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChainExport {

    private String reference;
    private String name;
    private List<String> questionsRef;
    private String date;

    public ChainExport(Chain chain) {
        reference = chain.getReference();
        name = chain.getName();
        questionsRef = chain.getQuestions()
                .stream()
                .map(QuestionExport::new)
                .map(QuestionExport::getReference)
                .collect(Collectors.toList());
        date = DateUtils.format(chain.getDate());
    }
}
