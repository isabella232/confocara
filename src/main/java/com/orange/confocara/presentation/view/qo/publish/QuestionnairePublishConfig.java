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

package com.orange.confocara.presentation.view.qo.publish;

import com.google.common.collect.ImmutableList;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the QuestionnaireObject publishing Module except its web layer
 */
@Configuration
public class QuestionnairePublishConfig {

    @Autowired
    private QuestionnaireObjectReadRepository questionnaireObjectReadRepository;

    @Bean
    QuestionnaireObjectPublishingService questionnaireObjectPublishService() {
        return QuestionnaireObjectPublishingService.instance(
                questionnaireObjectReadRepository,
                questionnaireObjectPublisher());
    }

    @Bean
    QuestionnaireObjectPublisher questionnaireObjectPublisher() {
        return new QuestionnaireObjectPublisher(questionnaireObjectReadRepository, publishingStrategies());
    }

    List<PublishingStrategy> publishingStrategies() {
        return ImmutableList.<PublishingStrategy>builder()
                .add(new InvalidCompositePublishingStrategy(questionnaireObjectReadRepository))
                .add(compositePublishingStrategy())
                .add(basicPublishingStrategy())
                .build();
    }

    @Bean
    PublishingStrategy basicPublishingStrategy() {
        return new BasicPublishingStrategy(questionnaireObjectReadRepository);
    }

    @Bean
    PublishingStrategy compositePublishingStrategy() {
        return new CompositePublishingStrategy(questionnaireObjectReadRepository);
    }
}
