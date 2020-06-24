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

import static com.google.common.collect.Lists.newArrayList;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
public class QuestionnairePublishableValidator {

    private final QuestionnaireObjectRepository repository;

    public void validate(Long questionnaireId, Errors errors, Errors warnings) {

        QuestionnaireObject qoToPublish = repository.findOne(questionnaireId);

        Equipment equipment = qoToPublish.getEquipment();

        newArrayList(equipment.getSubobjects())
                .stream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    Object[] errorArgs = new String[]{equipment.getReference(), e.getReference()};
                    List<QuestionnaireObject> questionnaireObjects = repository
                            .findByEquipmentName(e.getName());
                    if (questionnaireObjects == null || questionnaireObjects.isEmpty()) {
                        errors.rejectValue(
                                "equipment",
                                "notEmpty",
                                errorArgs,
                                "Sub-Equipment should have at least one questionnaire.");
                    } else if (questionnaireObjects.size() > 1) {
                        warnings.rejectValue(
                                "equipment",
                                "incomplete",
                                errorArgs,
                                "Sub-Equipment is linked to more than 1 questionnaire.");
                    }
                });

    }
}
