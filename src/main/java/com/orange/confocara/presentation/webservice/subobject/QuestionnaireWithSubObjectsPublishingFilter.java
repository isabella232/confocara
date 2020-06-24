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

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import com.orange.confocara.presentation.view.qo.publish.PublishingStrategy;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link PublishingStrategy} that raises an exception when it matches
 */
@Slf4j
@RequiredArgsConstructor
public class QuestionnaireWithSubObjectsPublishingFilter implements Predicate<Long> {

    private final QuestionnaireObjectReadRepository repository;

    /**
     * Checks the validity of a questionnaire
     *
     * @param questionnaireId a identifier for a {@link QuestionnaireObject}
     *
     * @return true if the argument has at least one equipment with more than one possible
     * sub-questionnaire
     */
    @Override
    public boolean test(Long questionnaireId) {
        boolean result = false;

        if (repository.exists(questionnaireId)) {
            QuestionnaireObject entity = repository.findOne(questionnaireId);
            List<Equipment> subEquipments = entity.getEquipment().getSubobjects();
            if (subEquipments != null) {
                result = subEquipments
                        .stream()
                        .anyMatch(equipment -> {
                            List<QuestionnaireObject> list = repository
                                    .findByEquipmentName(equipment.getName());
                            return list != null && list.size() > 1;
                        });
            }
            log.info("Message=Checking pre-requisites;QuestionnaireId={};Result={}", questionnaireId, result);
        } else {
            log.warn("Message=Invalid identifier;QuestionnaireId={}", questionnaireId);
        }

        return result;
    }
}
