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

import com.orange.confocara.connector.persistence.model.ImageAccount;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.ImageAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageAccountService {

    private final FileService fileService;
    private final ImageAccountRepository imageAccountRepository;

    @Transactional
    public ImageAccount create(String originalFilename) {
        ImageAccount image = new ImageAccount();
        image.setImageName(fileService.formatOriginalFilename(originalFilename));
        image.setExtension(fileService.getFileExtension(originalFilename));

        return imageAccountRepository.save(image);
    }

    @Transactional
    public ImageAccount updateAccount(ImageAccount image, User account) {
        image.setAccount(account);

        return imageAccountRepository.save(image);
    }

    @Transactional
    public void delete(String uuid) {
        ImageAccount img = imageAccountRepository.findByUuid(uuid);
        imageAccountRepository.delete(img);
    }
}
