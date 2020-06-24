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

package com.orange.confocara.presentation.view.qo.view;

import static com.google.common.collect.Lists.newArrayList;
import static com.orange.confocara.presentation.view.qo.view.QuestionnaireViewDto.QuestionnaireView.defaultQuestionnaireView;

import com.orange.confocara.connector.persistence.model.Chapter;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.presentation.view.qo.view.QuestionnaireViewDto.ChainView;
import com.orange.confocara.presentation.view.qo.view.QuestionnaireViewDto.QuestionnaireView;
import com.orange.confocara.presentation.view.util.TemplateConfig;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the QuestionnaireObject view Module except its web layer
 */
@Import(TemplateConfig.class)
@Configuration
public class QuestionnaireQueryConfig {

    @Autowired
    private QuestionnaireObjectQueryRepository questionnaireObjectQueryRepository;

    @Autowired
    private RulesetQueryRepository rulesetQueryRepository;

    @Autowired
    private TemplateConfig templateConfig;

    @Bean
    QuestionnaireObjectQueryService questionnaireObjectQueryService() {
        return QuestionnaireObjectQueryService.instance(
                questionnaireObjectQueryRepository,
                (Function<QuestionnaireObject, QuestionnaireViewDto>) this::mapper);
    }

    private QuestionnaireViewDto mapper(QuestionnaireObject input) {

            List<String> rulesetNames = newArrayList(rulesetQueryRepository.findAll(
                    new RulesetQueryRepository.RulesetCriteriaBuilder()
                            .withQuestionnaireId(input.getId()).buildPredicate()))
                    .stream()
                    .map(Ruleset::getType)
                    .collect(Collectors.toList());

            // Helper functions, that sorts chains and questions.... Ugly, isn't it ?
            input.orderChains();
            input.getChains().forEach(chain -> chain.orderQuestions());

            List<ChainView> chains = input
                    .getSubChapters()
                    .stream()
                    .map(ChainView::from)
                    .collect(Collectors.toList());

            Function<Chapter, QuestionnaireView> questionnaireFunction = qo -> ImmutableQuestionnaireView
                    .builder()
                    .id(qo.getId())
                    .name(qo.getName())
                    .reference(qo.getReference())
                    .version(qo.getVersion())
                    .categoryName(qo.getRulesCategory().getName())
                    .equipmentName(qo.getEquipment().getName())
                    .authorName(qo.getUser().getUsername())
                    .lastUpdateDate(qo.getLastUpdateDate() == null ? new Date() : qo.getLastUpdateDate())
                    .published(qo.isPublished())
                    .state(templateConfig.translateState(qo))
                    .questionsNb(input.questionsCount())
                    .rulesNb(input.rulesCount())
                    .build();

            return ImmutableQuestionnaireViewDto
                    .builder()
                    .questionnaire(Optional
                            .of(input)
                            .map(questionnaireFunction)
                            .orElse(defaultQuestionnaireView()))
                    .chains(chains)
                    .rulesets(rulesetNames)
                    .build();
    }
}
