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

import com.orange.confocara.connector.persistence.model.ByReference;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.presentation.view.qo.list.QuestionnaireDtoRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * Function that transform a {@link Equipment} into a {@link EquipmentDto}
 */
@RequiredArgsConstructor
public class EquipmentDtoFunction implements Function<Equipment, EquipmentDto> {

    private final QuestionnaireDtoRepository repository;

    @Override
    public EquipmentDto apply(Equipment equipment) {
        String name = equipment.getName();

        List<String> references = repository
                .findByEquipmentName(name)
                .stream()
                .map(ByReference::getReference)
                .distinct()
                .collect(Collectors.toList());

        return EquipmentDto.newInstance(name, references);
    }
}
