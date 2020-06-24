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

import com.orange.confocara.connector.persistence.model.Image;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.ImageIllustration;
import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final FileService fileService;
    private final ImageRepository imageRepository;

    @Transactional
    public List<Image> all() {
        return (List<Image>) imageRepository.findAll();
    }

    @Transactional
    public Image findByImageName(String name) {
        return imageRepository.findByImageName(fileService.formatOriginalFilename(name));
    }

    @Transactional
    public Image create(String originalFilename) {
        Image image = new Image();
        image.setImageName(fileService.formatOriginalFilename(originalFilename));
        image.setExtension(fileService.getFileExtension(originalFilename));

        return imageRepository.save(image);
    }

    @Transactional
    public void delete(String uuid) {
        Image img = imageRepository.findByUuid(uuid);
        if (img != null) {
            imageRepository.delete(img);
        }
    }

    /**
     * If an image has been published but is no longer linked in database with another object,
     * then it can be reused and linked with a new object
     *
     * @param originalFilename the filename of the image as the user uploaded it
     * @return true if the image was published and can be reused
     */
    @Transactional
    public boolean isPublishedAndReusable(String originalFilename) {
        Image img = imageRepository.findByImageName(fileService.formatOriginalFilename(originalFilename));

        if (img != null && img.isPublished()) {
            if (img instanceof ImageEquipment) {
                return ((ImageEquipment) img).getEquipment() == null;
            } else if (img instanceof ImageIllustration) {
                return ((ImageIllustration) img).getIllustration() == null;
            } else if (img instanceof ImageProfileType) {
                return ((ImageProfileType) img).getProfileType() == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
