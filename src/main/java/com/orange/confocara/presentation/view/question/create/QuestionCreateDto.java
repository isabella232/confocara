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

package com.orange.confocara.presentation.view.question.create;

import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** a DTO */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class QuestionCreateDto {

    private String reference;

    private String label;

    private List<String> ruleIds;

    private List<String> orderedRuleIds;

    private String state;

    private Subject subject = new Subject();

    private RulesCategory rulesCategory = new RulesCategory();
}
