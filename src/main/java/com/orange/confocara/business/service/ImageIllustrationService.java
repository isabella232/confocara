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


import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.ImageIllustration;
import com.orange.confocara.connector.persistence.repository.ImageIllustrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageIllustrationService {

    private final FileService fileService;
    private final ImageIllustrationRepository imageIllustrationRepository;

    @Transactional
    public ImageIllustration create(ImageIllustration image) {
        return imageIllustrationRepository.save(image);
    }

    @Transactional
    public ImageIllustration create(String originalFilename) {
        ImageIllustration image = new ImageIllustration();
        image.setImageName(fileService.formatOriginalFilename(originalFilename));
        image.setExtension(fileService.getFileExtension(originalFilename));

        return imageIllustrationRepository.save(image);
    }

    @Transactional
    public ImageIllustration updateIllustration(ImageIllustration image, Illustration illustration) {
        image.setIllustration(illustration);

        return imageIllustrationRepository.save(image);
    }

    @Transactional
    public void delete(String uuid) {
        ImageIllustration img = imageIllustrationRepository.findByUuid(uuid);
        imageIllustrationRepository.delete(img);
    }
}
