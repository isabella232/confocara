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

import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the QuestionnaireObject List view Module except its web layer
 */
@Configuration
public class QuestionnairesListConfig {

    @Autowired
    private QuestionnaireDtoRepository questionnaireDtoRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private EntityManager entityManager;

    @Bean
    QuestionnaireDraftListQueryService questionnaireListQueryService() {
        return QuestionnaireDraftListQueryService.instance(
                questionnaireDtoRepository,
                questionnaireObjectRepository);
    }

    @Bean
    QuestionnairePublishedListQueryService questionnairePublishedListQueryService() {
        return QuestionnairePublishedListQueryService.instance(entityManager);
    }
}
