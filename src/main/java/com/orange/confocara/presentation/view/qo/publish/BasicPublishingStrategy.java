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

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link PublishingStrategy}
 *
 */
@Slf4j
@RequiredArgsConstructor
public class BasicPublishingStrategy implements PublishingStrategy {

    private final QuestionnaireObjectReadRepository repository;

    /**
     * Checks the validity of a questionnaire for the strategy
     *
     * @param  id an identifier for a {@link QuestionnaireObject}
     * @return true if the argument has zero sub-questionnaires
     */
    @Override
    public boolean check(Long id) {

        QuestionnaireObject entity = repository.findOne(id);

        List<Equipment> subEquipments = entity.getEquipment().getSubobjects();

        boolean result = true;
        if (subEquipments != null) {
            result = subEquipments
                    .stream()
                    .allMatch(equipment -> {
                        List<QuestionnaireObject> list = repository
                                .findByEquipmentName(equipment.getName());
                        return list == null || list.isEmpty();
                    });
        }
        return result;
    }

    /**
     * Basically, only marks the given questionnaire as published
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     */
    @Transactional
    @Override
    public void apply(Long id) {
        QuestionnaireObject entity = repository.findOne(id);

        log.info("Message=Applying basic strategy...;QuestionnaireRef={};QuestionnaireVersion={}", entity.getReference(), entity.getVersion());

        entity.markAsPublished();
    }
}
