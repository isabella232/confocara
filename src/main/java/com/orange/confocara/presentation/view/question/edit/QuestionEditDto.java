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

package com.orange.confocara.presentation.view.question.edit;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * a Data-Transfer Object to share with the view
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionEditDto {

    private long id;

    private String reference;

    private String label;

    private List<String> ruleIds;

    private List<String> orderedRuleIds;

    private String state;

    private Subject subject;

    private RulesCategory rulesCategory;

    public QuestionEditDto(Question q) {
        id = q.getId();
        reference = q.getReference();
        label = q.getLabel();
        state = q.getState();
        subject = q.getSubject();
        rulesCategory = q.getRulesCategory();


        if (q.hasRulesOrder()) {
            List<String> sort = Arrays.asList(q.getRulesOrder().split(","));

            orderedRuleIds = sort;

            ruleIds = q
                    .getRules()
                    .stream()
                    .map(Rule::getReference)
                    .sorted(comparingInt(sort::indexOf))
                    .collect(toList());
        } else if (q.hasRules()){
            ruleIds = q
                    .getRules()
                    .stream()
                    .map(Rule::getReference)
                    .collect(toList());

            orderedRuleIds = ruleIds;
        } else {
            ruleIds = Collections.emptyList();
            orderedRuleIds = Collections.emptyList();
        }

    }
}
