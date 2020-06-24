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

package com.orange.confocara.presentation.view.home;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EquipmentInfoService {

    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public List<EquipmentImage> all() {

        List<Equipment> equipments = equipmentRepository.findAllByOrderByNameAsc();

        return IntStream
                .range(0, equipments.size() - 1)
                .mapToObj(value -> {
                    EquipmentImage output = null;
                    if (equipments.size() > value) {
                        Equipment equipment = equipments.get(value);
                        output = ImmutableEquipmentImage
                                .builder()
                                .index(value)
                                .fileNameWithExtension(equipment.getIcon().getFileNameWithExtension())
                                .build();
                    }
                    return output;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
