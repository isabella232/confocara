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

import com.orange.confocara.common.logging.Logged;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tool for publishing/drafting questionnaires
 */
@Slf4j
@RequiredArgsConstructor
public class QuestionnaireWithSubObjectsPublisher {

    private final QuestionnaireObjectReadRepository repository;

    /**
     * Mark a {@link QuestionnaireObject} as published.
     *
     * @param questionnaireId an identifier for a entity
     */
    @Logged(message = "Publishing a questionnaire")
    @Transactional
    public void publishQuestionnaire(Long questionnaireId) {
        QuestionnaireObject entity = repository.findOne(questionnaireId);
        entity.markAsPublished();
    }

    /**
     * Mark a {@link QuestionnaireObject} as new draft.
     *
     * @param questionnaireId an identifier for a entity
     */
    @Logged(message = "Drafting a questionnaire")
    @Transactional
    public void draftQuestionnaire(Long questionnaireId) {
        QuestionnaireObject entity = repository.findOne(questionnaireId);
        entity.markAsNewDraft();
    }
}
