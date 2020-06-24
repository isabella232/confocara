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

import com.orange.confocara.connector.persistence.model.*;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.IllustrationRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllustrationService {

    private final IllustrationRepository illustrationRepository;

    private final EquipmentRepository equipmentRepository;

    private final RuleRepository ruleRepository;

    private final FileService fileService;

    private final ImageService imageService;

    private final ImageIllustrationService imageIllustrationService;

    @Transactional
    public List<Illustration> all() {
        return illustrationRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Illustration withId(long id) {
        return illustrationRepository.findOne(id);
    }

    @Transactional
    public List<Illustration> withIds(List<Long> ids) {
        return (List<Illustration>) illustrationRepository.findAll(ids);
    }

    @Transactional
    public Illustration withReference(String reference) {
        return illustrationRepository.findByReference(reference);
    }

    @Transactional
    public boolean isAvailable(String name) {
        List<Illustration> illustrations = illustrationRepository.findByTitle(name.trim().replaceAll("\\s+", " "));
        return illustrations.isEmpty();
    }

    @Transactional
    public Illustration create(Illustration illustration) {
        return illustrationRepository.save(illustration);
    }

    @Transactional
    public Illustration createOrUpdateWithIconInTx(Illustration illustration, MultipartFile icon, Image oldImageToDelete) {
        Illustration savedIllustration = null;

        ImageIllustration imageWithId = imageIllustrationService.create(icon.getOriginalFilename());
        FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
        illustration.setImage(imageWithId);

        if (uploadStatus == FileService.UploadStatus.OK) {
            savedIllustration = illustrationRepository.save(illustration);
            imageIllustrationService.updateIllustration(imageWithId, savedIllustration);
        }

        if (oldImageToDelete != null) {
            deleteOldIcon(oldImageToDelete);
        }

        return savedIllustration;
    }

    @Transactional
    public Illustration createOrUpdateWithPublishedImage(Illustration illustration, Image publishedImage, Image oldImage) {
        ImageIllustration image = new ImageIllustration();
        image.setPublished(publishedImage.isPublished());
        image.setExtension(publishedImage.getExtension());
        image.setImageName(publishedImage.getImageName());
        image.setUuid(publishedImage.getUuid());
        imageService.delete(publishedImage.getUuid());
        ImageIllustration imageIllustration = imageIllustrationService.create(image);
        illustration.setImage(imageIllustration);

        Illustration savedIllustration = illustrationRepository.save(illustration);
        imageIllustrationService.updateIllustration(imageIllustration, savedIllustration);

        if (oldImage != null) {
            deleteOldIcon(oldImage);
        }

        return savedIllustration;
    }

    /**
     * remove illustration from associated rules and equipments
     * delete image (if exists) from server
     * and finally delete illustration from db
     *
     * @param id the id of the illustration to delete
     */
    @Transactional
    public void delete(long id) {
        Illustration illustration = illustrationRepository.findOne(id);

        List<Rule> rules = ruleRepository.findByIllustrations(illustration);
        for (Rule rule : rules) {
            List<Illustration> illustrationList = rule.getIllustrations();
            if (illustrationList.contains(illustration)) {
                illustrationList.remove(illustration);
            }
        }

        List<Equipment> byIllustrations = equipmentRepository.findByIllustrations(illustration);
        for (Equipment equipment : byIllustrations) {
            final List<Illustration> illustrations = equipment.getIllustrations();
            if (illustrations.contains(illustration)) {
                illustrations.remove(illustration);
            }
        }

        if (illustration.getImage() != null) {
            if (illustration.getImage().isPublished()) {
                imageIllustrationService.updateIllustration(illustration.getImage(), null);
                illustration.setImage(null);
            } else {
                fileService.deleteIcon(illustration.getImage().getFileNameWithExtension());
            }
        }

        illustrationRepository.delete(id);
    }

    @Transactional
    public Illustration update(@NonNull Illustration illustration, Image imageToDelete) {
        Illustration savedIllustration = illustrationRepository.save(illustration);

        if (imageToDelete != null) {
            deleteOldIcon(imageToDelete);
        }

        return savedIllustration;
    }

    private void deleteOldIcon(Image oldImage) {
        if (!oldImage.isPublished()) {
            String fileNameWithExtension = oldImage.getFileNameWithExtension();
            imageService.delete(oldImage.getUuid());
            fileService.deleteIcon(fileNameWithExtension);
        } else if (oldImage instanceof ImageIllustration) {
            imageIllustrationService.updateIllustration((ImageIllustration) oldImage, null);
        }
    }

}
