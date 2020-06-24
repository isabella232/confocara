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

package com.orange.confocara.presentation.webservice.subobject;

import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.presentation.view.qo.list.QuestionnaireDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the QuestionnaireObject List view Module except its web layer
 */
@Configuration
public class SubObjectConfig {

    @Autowired
    private QuestionnaireDtoRepository questionnaireDtoRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private QuestionnaireObjectReadRepository questionnaireObjectReadRepository;

    @Bean
    QuestionnaireWithSubObjectsPublisher questionnaireWithSubObjectsPublisher() {
        return new QuestionnaireWithSubObjectsPublisher(questionnaireObjectReadRepository);
    }

    @Bean
    SubObjectsQueryService subObjectsQueryService() {
        return SubObjectsQueryService.instance(
                questionnaireObjectRepository,
                new EquipmentDtoFunction(questionnaireDtoRepository));
    }

    @Bean
    SubObjectsPublishService subObjectsPublishService() {
        return SubObjectsPublishService.instance(
                new QuestionnaireWithSubObjectsPublishingFilter(questionnaireObjectReadRepository),
                questionnaireWithSubObjectsPublisher(),
                subObjectsQueryService());
    }
}
