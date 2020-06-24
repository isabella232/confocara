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

package com.orange.confocara.presentation.view.question.list;

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuestionDto {

    private long id;

    private String reference;

    private String label;

    private String state;

    private String username;

    private String subjectLabel;

    private String rulesCategoryName;

    private List<RuleDto> rules;

    private Date date;

    public QuestionDto(Question q) {

        id = q.getId();
        reference = q.getReference();
        label = q.getLabel();
        username = q.getUser().getUsername();
        subjectLabel = q.getSubject().getName();
        rulesCategoryName = q.getRulesCategory().getName();
        date = q.getDate();
        state = q.getState();

        List<Rule> entities = q.getRules();

        List<String> orderedRulesIds = q.hasRulesOrder() ? Arrays
                .asList(q.getRulesOrder().split(",")) : entities.stream().map(Rule::getId).map(Object::toString).collect(Collectors.toList());

        rules = entities
                .stream()
                .map(RuleDto::new)
                .sorted((o1, o2) -> {
                    int index1 = orderedRulesIds.indexOf(o1.getReference());
                    int index2 = orderedRulesIds.indexOf(o2.getReference());
                    return Integer.compare(index2, index1);
                }).collect(Collectors.toList());
    }
}
