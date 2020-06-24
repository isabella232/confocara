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

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.model.ByReference;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Behaviour of services that retrieve a bunch of {@link QuestionnaireDtoWrapper}s
 */
@FunctionalInterface
interface SubObjectsPublishService {

    /**
     * Increments the version of a questionnaire and the questionnaires related to its equipments
     *
     * @param questionnaireId an identifier for a {@link QuestionnaireObject}
     * @param associatedSubObjects a list of {@link Equipment}s
     */
    void publishOneQuestionnaire(Long questionnaireId,
            List<ByReference> associatedSubObjects);


    static SubObjectsPublishService instance(
            Predicate<Long> filter,
            QuestionnaireWithSubObjectsPublisher publisher,
            SubObjectsQueryService service) {

        return new SubObjectsQueryServiceImpl(filter, publisher, service);
    }

    /**
     * Default implementation of {@link SubObjectsPublishService}
     */
    @Slf4j
    @RequiredArgsConstructor
    class SubObjectsQueryServiceImpl implements SubObjectsPublishService {

        private final Predicate<Long> preConditions;

        private final QuestionnaireWithSubObjectsPublisher publisher;

        private final SubObjectsQueryService service;

        @Override
        public void publishOneQuestionnaire(Long questionnaireId,
                List<ByReference> associatedSubObjects) {

            // check questionnaire is valid
            if (!preConditions.test(questionnaireId)) {
                throw new BizException(ErrorCode.NOT_FOUND);
            }

            // assign the sub-objects to the current questionnaire
            service.assignSubObjects(questionnaireId, associatedSubObjects);

            // publish the current questionnaire with the possible sub-objects
            publisher.publishQuestionnaire(questionnaireId);

            // unpublish the current questionnaire and increment its version
            publisher.draftQuestionnaire(questionnaireId);
        }
    }
}
