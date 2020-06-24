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
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of {@link PublishingStrategy} for questionnaires that have subQuestionnaires
 */
@Slf4j
@RequiredArgsConstructor
public class CompositePublishingStrategy implements PublishingStrategy {

    private final QuestionnaireObjectReadRepository repository;

    /**
     * Checks the validity of a questionnaire for the strategy
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     * @return true if the given questionnaire has at leat one equipment with only one
     * sub-questionnaire AND no equipment with more than 1 sub-questionnaire
     */
    @Override
    public boolean check(Long id) {

        QuestionnaireObject entity = repository.findOne(id);

        List<Equipment> subEquipments = entity.getEquipment().getSubobjects();

        boolean result = false;
        if (subEquipments != null) {
            boolean hasNoEquipmentWithMultipleSubQuestionnaires = subEquipments
                    .stream()
                    .allMatch(equipment -> {
                        List<QuestionnaireObject> list = repository
                                .findByEquipmentName(equipment.getName());
                        return list != null && list.size() <= 1;
                    });

            boolean hasAtLeastOneEquipmentWithAUniqueSubQuestionnaire = subEquipments
                    .stream()
                    .anyMatch(equipment -> {
                        List<QuestionnaireObject> list = repository
                                .findByEquipmentName(equipment.getName());
                        return list != null && list.size() == 1;
                    });

            result = hasNoEquipmentWithMultipleSubQuestionnaires
                    && hasAtLeastOneEquipmentWithAUniqueSubQuestionnaire;
        }
        return result;
    }

    /**
     * Assigns sub-questionnaire to the given questionnaire, and then marks the given questionnaire
     * as published
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     */
    @Transactional
    @Override
    public void apply(Long id) {

        QuestionnaireObject entity = repository.findOne(id);

        log.info("Message=Applying composite strategy...;QuestionnaireRef={};QuestionnaireVersion={}", entity.getReference(), entity.getVersion());

        List<QuestionnaireObject> subObjects = entity
                .getEquipment()
                .getSubobjects()
                .stream()
                .filter(equipment -> repository
                        .findByEquipmentName(equipment.getName()) != null)
                .flatMap(equipment -> repository
                        .findByEquipmentName(equipment.getName())
                        .stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        entity.setQuestionnaireSubObjects(subObjects);

        entity.markAsPublished();
    }
}
