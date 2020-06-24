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

import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation that marks a {@link QuestionnaireDtoWrapper} as inactive
 */
@Slf4j
@RequiredArgsConstructor
public class InactiveQuestionnaireConsumer implements Consumer<QuestionnaireDtoWrapper> {

    private final QuestionnaireDtoRepository repository;

    @Override
    public void accept(QuestionnaireDtoWrapper q) {

        boolean questionnaireIsInactive = repository
                .findAllInactiveQuestionnaire()
                .stream()
                .anyMatch(inactive -> inactive.getId() == q.getDtoId());

        if (questionnaireIsInactive) {
            q.setState(State.INACTIVE);
            q.setPublishingState(PublishingState.NOT_PUBLISHABLE);
        }
    }
}
