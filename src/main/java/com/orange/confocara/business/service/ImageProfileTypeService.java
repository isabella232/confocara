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

import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.repository.ImageProfileTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageProfileTypeService {

    private final FileService fileService;
    private final ImageProfileTypeRepository imageProfileTypeRepository;

    @Transactional
    public ImageProfileType create(ImageProfileType image) {
        return imageProfileTypeRepository.save(image);
    }

    @Transactional
    public ImageProfileType create(String originalFilename) {
        ImageProfileType image = new ImageProfileType();
        image.setImageName(fileService.formatOriginalFilename(originalFilename));
        image.setExtension(fileService.getFileExtension(originalFilename));

        return imageProfileTypeRepository.save(image);
    }

    @Transactional
    public ImageProfileType updateProfileType(ImageProfileType image, ProfileType profileType) {
        image.setProfileType(profileType);

        return imageProfileTypeRepository.save(image);
    }

    @Transactional
    public void delete(String uuid) {
        ImageProfileType img = imageProfileTypeRepository.findByUuid(uuid);
        imageProfileTypeRepository.delete(img);
    }
}
