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
import com.orange.confocara.connector.persistence.model.Question;
import java.util.stream.Collectors;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionExport {
    private String reference;
    private String label;
    private String state;
    private String origin;
    private SubjectWS subject;
    private List<String> rulesRef = new ArrayList<>();
    private String date;

    public QuestionExport(Question question) {
        reference = question.getReference();
        label = question.getLabel();
        state = question.getState();
        subject = new SubjectWS(question.getSubject().getName());
        rulesRef = question.getRules().stream()
                .map(RuleExport::new)
                .map(RuleExport::getReference)
                .collect(Collectors.toList());
        date = DateUtils.format(question.getDate());
    }
}