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
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Behaviour of services that retrieve a bunch of unpublished {@link QuestionnaireDtoWrapper}s
 */
@FunctionalInterface
interface QuestionnaireDraftListQueryService {

    List<QuestionnaireDtoWrapper> retrieveQuestionnaires();

    static QuestionnaireDraftListQueryService instance(QuestionnaireDtoRepository dtoRepository,
            QuestionnaireObjectRepository entityRepository) {

        return new QuestionnaireDraftListQueryServiceImpl(dtoRepository, entityRepository);
    }

    /**
     * Default implementation of {@link QuestionnaireDraftListQueryService}
     */
    @Slf4j
    final class QuestionnaireDraftListQueryServiceImpl implements
            QuestionnaireDraftListQueryService {

        private final QuestionnaireDtoRepository repository;

        private final Function<QuestionnaireDto, QuestionnaireDtoWrapper> mapper;

        private final Consumer<QuestionnaireDtoWrapper> checkInactive;

        private final Consumer<QuestionnaireDtoWrapper> checkAlmostPublishable;

        private final Consumer<QuestionnaireDtoWrapper> checkDefault = new DefaultQuestionnaireConsumer();

        QuestionnaireDraftListQueryServiceImpl(
                QuestionnaireDtoRepository repository,
                QuestionnaireObjectRepository entityRepository) {
            this.repository = repository;
            mapper = new QuestionnaireDtoFunction(repository);
            checkInactive = new InactiveQuestionnaireConsumer(repository);
            checkAlmostPublishable = new AlmostPublishableQuestionnaireConsumer(entityRepository);
        }

        /**
         * Retrieves all draft questionnaire properties
         *
         * @return draft questionnaire properties
         */
        public List<QuestionnaireDtoWrapper> retrieveQuestionnaires() {

            // create and apply the stream that produces the QuestionnaireDtoWrappers
            return repository.findAll()
                    .stream()
                    // 1- initialize the QuestionnaireDtoWrapper
                    .map(mapper)
                    // 2a- apply INACTIVE / NOT_PUBLISHABLE,
                    // condition : if the questionnaire is in the list of inactive questionnaires
                    .peek(checkInactive)
                    // 2b- apply ACTIVE / ALMOST_PUBLISHABLE,
                    // condition : only if the QO has sub-objects related to multiple questionnaires
                    .peek(checkAlmostPublishable)
                    // 2c- apply ACTIVE / PUBLISHABLE,
                    // condition : by default, ie state not assigned yet
                    .peek(checkDefault)
                    // 3- trigger the stream and put it into a list
                    .collect(Collectors.toList());
        }
    }

}
