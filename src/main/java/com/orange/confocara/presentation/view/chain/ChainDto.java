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

import com.google.common.collect.Lists;
import com.orange.confocara.connector.persistence.model.Chain;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChainDto {

    private final long id;

    private final String reference;

    private final String name;

    private final RulesCategoryDto rulesCategory;

    private final List<QuestionDto> questions;

    private final List<String> questionIds;

    private final List<String> orderedQuestionIds;

    public ChainDto(Chain c) {
        id = c.getId();
        reference = c.getReference();
        name = c.getName();
        rulesCategory = new RulesCategoryDto(c.getRulesCategory());
        questions = c.getQuestions().stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());

        questionIds = questions.stream()
                .map(q -> Long.toString(q.getId()))
                .collect(Collectors.toList());

        orderedQuestionIds = Lists.newArrayList(questionIds);
    }
}
