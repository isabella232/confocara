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

package com.orange.confocara.presentation.view.qo.list;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.QuestionnaireQuestionsDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireRulesDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireSubObjectsDto;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Function that transforms a {@link QuestionnaireDto} into a {@link QuestionnaireDtoWrapper}
 */
@Slf4j
@RequiredArgsConstructor
public class QuestionnaireDtoFunction implements Function<QuestionnaireDto, QuestionnaireDtoWrapper> {

    private final QuestionnaireDtoRepository repository;

    @Override
    public QuestionnaireDtoWrapper apply(QuestionnaireDto dto) {

        // prepare all the required data
        Map<Long, QuestionnaireQuestionsDto> allQuestions = repository
                .findAllQuestionnaireQuestionDto()
                .stream()
                .collect(Collectors.toMap(QuestionnaireQuestionsDto::getId, Function.identity()));

        Map<Long, QuestionnaireRulesDto> allRules = repository
                .findAllQuestionnaireRulesDto()
                .stream()
                .collect(Collectors.toMap(QuestionnaireRulesDto::getId, Function.identity()));

        Map<Long, QuestionnaireSubObjectsDto> allSubObjects = repository
                .findAllQuestionnaireSubObjectDto()
                .stream()
                .collect(Collectors.toMap(QuestionnaireSubObjectsDto::getId, Function.identity()));

        return QuestionnaireDtoWrapper
                .builder()
                .dto(dto)
                .questionsNb(allQuestions.containsKey(dto.getId()) ? allQuestions.get(dto.getId()).getQuestionsNb() : 0)
                .rulesNb(allRules.containsKey(dto.getId()) ? allRules.get(dto.getId()).getRulesNb() : 0)
                .subObjectsNb(allSubObjects.containsKey(dto.getId()) ? allSubObjects.get(dto.getId()).getSubObjectsNb() : 0)
                .build();
    }
}
