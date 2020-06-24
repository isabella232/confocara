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
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link PublishingStrategy} that raises an exception when it matches
 */
@Slf4j
@RequiredArgsConstructor
public class InvalidCompositePublishingStrategy implements PublishingStrategy {

    private final QuestionnaireObjectReadRepository repository;

    /**
     * Checks the validity of a questionnaire for the strategy
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     * @return true if the argument has at least one equipment with more than one possible
     * sub-questionnaire
     */
    @Override
    public boolean check(Long id) {

        QuestionnaireObject entity = repository.findOne(id);

        List<Equipment> subEquipments = entity.getEquipment().getSubobjects();
        boolean result = false;
        if (subEquipments != null) {
            result = subEquipments
                    .stream()
                    .anyMatch(equipment -> {
                        List<QuestionnaireObject> list = repository
                                .findByEquipmentName(equipment.getName());
                        return list != null && list.size() > 1;
                    });
        }
        return result;
    }

    /**
     * Throws a {@link BizException}
     *
     * @param id an identifier for a {@link QuestionnaireObject}
     */
    @Override
    public void apply(Long id) {
        log.info("Message=Applying invalid composite strategy...;QuestionnaireId={}", id);

        throw new BizException(ErrorCode.INVALID);
    }
}
