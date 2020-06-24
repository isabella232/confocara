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
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation that marks a {@link QuestionnaireDtoWrapper} as "Almost Publishable". It means that
 * it can published under extra circulstances, such as checking and publishing sub-objects.
 */
@Slf4j
@RequiredArgsConstructor
public class AlmostPublishableQuestionnaireConsumer implements Consumer<QuestionnaireDtoWrapper> {

    private final QuestionnaireObjectRepository repository;

    @Override
    public void accept(QuestionnaireDtoWrapper q) {

        if (q.getSubObjectsNb() > 0) {
            QuestionnaireObject qoToPublish = repository.findOne(q.getDtoId());

            boolean hasMultipleSubObjects = qoToPublish.getEquipment().getSubobjects()
                    .stream()
                    .anyMatch(equipment -> {
                        List<QuestionnaireObject> list = repository
                                .findByEquipmentName(equipment.getName());
                        return list != null && list.size() > 1;
                    });

            if (hasMultipleSubObjects) {
                q.setPublishingState(PublishingState.ALMOST_PUBLISHABLE);
                q.setState(State.ACTIVE);
            }
        }
    }
}
