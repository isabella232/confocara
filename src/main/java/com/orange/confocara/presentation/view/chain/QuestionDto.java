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

package com.orange.confocara.presentation.view.chain;

import com.orange.confocara.connector.persistence.model.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestionDto {

    private final long id;

    private final String reference;

    private final String label;

    private final RulesCategoryDto rulesCategory;

    private final String state;

    public QuestionDto(Question q) {
        id = q.getId();
        reference = q.getReference();
        label = q.getLabel();
        rulesCategory = new RulesCategoryDto(q.getRulesCategory());
        state = q.getState();
    }
}
