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

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Behaviour of services that can supply with {@link QuestionnaireObject}s
 */
@FunctionalInterface
public interface QuestionnaireObjectPublishingService {

    /**
     * Increments the version of a questionnaire
     *
     * @param questionnaireId an identifier for a {@link QuestionnaireObject}
     */
    void publishOneQuestionnaire(Long questionnaireId);

    /**
     * Retrieves a default implementation
     *
     * @param repository repository for {@link QuestionnaireObject} entities
     *
     * @return an instance of {@link QuestionnaireObjectPublishingService}
     */
    static QuestionnaireObjectPublishingService instance(
            QuestionnaireObjectReadRepository repository,
            QuestionnaireObjectPublisher publishingService) {

        return new QuestionnaireObjectPublishingServiceImpl(repository, publishingService);
    }

    /**
     * Default implementation of {@link QuestionnaireObjectPublishingService}
     */
    @Slf4j
    @RequiredArgsConstructor
    class QuestionnaireObjectPublishingServiceImpl implements QuestionnaireObjectPublishingService {

        private final QuestionnaireObjectReadRepository repository;

        private final QuestionnaireObjectPublisher publisher;

        @Override
        public void publishOneQuestionnaire(Long questionnaireId) {

            log.info("Trying to publish a questionnaire;QuestionnaireId={}", questionnaireId);

            // check questionnaire exists
            if (!repository.exists(questionnaireId)) {
                throw new BizException(ErrorCode.NOT_FOUND);
            }

            publisher.publishQuestionnaire(questionnaireId);

            publisher.draftQuestionnaire(questionnaireId);
        }
    }
}
