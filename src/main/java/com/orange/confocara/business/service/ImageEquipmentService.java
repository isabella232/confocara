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

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.repository.ImageEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageEquipmentService {

    private final FileService fileService;
    private final ImageEquipmentRepository imageEquipmentRepository;

    @Transactional
    public ImageEquipment create(ImageEquipment image) {
        return imageEquipmentRepository.save(image);
    }

    @Transactional
    public ImageEquipment create(String originalFilename) {
        ImageEquipment image = new ImageEquipment();
        image.setImageName(fileService.formatOriginalFilename(originalFilename));
        image.setExtension(fileService.getFileExtension(originalFilename));

        return imageEquipmentRepository.save(image);
    }

    @Transactional
    public ImageEquipment updateEquipment(ImageEquipment image, Equipment equipment) {
        image.setEquipment(equipment);

        return imageEquipmentRepository.save(image);
    }

    @Transactional
    public void delete(String uuid) {
        ImageEquipment img = imageEquipmentRepository.findByUuid(uuid);
        imageEquipmentRepository.delete(img);
    }
}
