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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final QuestionnaireObjectService questionnaireObjectService;
    private final ImageEquipmentService imageEquipmentService;
    private final ImageService imageService;
    private final RuleService ruleService;
    private final FileService fileService;

    @Transactional
    public List<Equipment> all() {
        return equipmentRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Equipment withId(long id) {
        return equipmentRepository.findOne(id);
    }

    @Transactional
    public List<Equipment> withIds(List<Long> ids) {
        return (List<Equipment>) equipmentRepository.findAll(ids);
    }

    @Transactional
    public Equipment withReference(String reference) {
        return equipmentRepository.findByReference(reference);
    }

    @Transactional
    public Equipment withName(String name) {
        return equipmentRepository.findByName(name);
    }

    @Transactional
    public List<Equipment> withType(String type) {
        return equipmentRepository.findByType(type);
    }

    @Transactional
    public boolean isAvailable(String name) {
        Equipment object = equipmentRepository.findByName(name.trim().replaceAll("\\s+", " "));

        return object == null;
    }

    @Transactional
    public Equipment createEquipmentWithIconInTx(Equipment equipment, MultipartFile icon) {
        Equipment savedEquipment = null;

        ImageEquipment imageWithId = imageEquipmentService.create(icon.getOriginalFilename());
        FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
        equipment.setIcon(imageWithId);

        if (uploadStatus == FileService.UploadStatus.OK) {
            equipment.setReference("");
            savedEquipment = equipmentRepository.save(equipment);
            imageEquipmentService.updateEquipment(imageWithId, savedEquipment);
        }

        return savedEquipment;
    }

    @Transactional
    public Equipment create(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public Equipment createOrUpdateWithPublishedImage(Equipment equipment, Image publishedImage, Image oldImage) {
        ImageEquipment image = new ImageEquipment();
        image.setPublished(publishedImage.isPublished());
        image.setExtension(publishedImage.getExtension());
        image.setImageName(publishedImage.getImageName());
        image.setUuid(publishedImage.getUuid());
        imageService.delete(publishedImage.getUuid());
        ImageEquipment imageEquipment = imageEquipmentService.create(image);
        equipment.setIcon(imageEquipment);

        Equipment savedEquipment = equipmentRepository.save(equipment);
        imageEquipmentService.updateEquipment(imageEquipment, savedEquipment);

        if (oldImage != null) {
            deleteOldIcon(oldImage);
        }

        return savedEquipment;
    }

    @Transactional
    public List<String> delete(long id) {
        List<String> conflictualQuestionnaires = new ArrayList<>();
        Equipment equipment = equipmentRepository.findOne(id);

        List<QuestionnaireObject> byEquipmentDB = questionnaireObjectService.findByEquipment(equipment);
        for (QuestionnaireObject questionnaireObject : byEquipmentDB) {
            conflictualQuestionnaires.add(questionnaireObject.getReference());
        }

        if (conflictualQuestionnaires.isEmpty()) {
            removeEquipmentInSubObjects(equipment);

            if (equipment.getIcon().isPublished()) {
                imageEquipmentService.updateEquipment(equipment.getIcon(), null);
                equipment.setIcon(null);
            } else {
                fileService.deleteIcon(equipment.getIcon().getFileNameWithExtension());
            }

            equipmentRepository.delete(id);
        }

        return conflictualQuestionnaires;
    }

    @Transactional
    public Equipment update(Equipment equipment) {
        if (equipment == null) {
            return null;
        } else {
            Equipment equipmentBeforeUpdate = withId(equipment.getId());
            if (equipmentBeforeUpdate.getType().equals(Equipment.SUBOBJECT_TYPE) &&
                    equipment.getType().equals(Equipment.OBJECT_TYPE)) {
                removeEquipmentInSubObjects(equipment);
            }
            return equipmentRepository.save(equipment);
        }
    }

    @Transactional
    public Equipment updateEquipmentWithIconInTx(Equipment equipment, MultipartFile icon, Image oldIcon) {
        Equipment equipmentBeforeUpdate = withId(equipment.getId());

        if (equipmentBeforeUpdate.getType().equals(Equipment.SUBOBJECT_TYPE) &&
                equipment.getType().equals(Equipment.OBJECT_TYPE)) {
            removeEquipmentInSubObjects(equipment);
        }

        Equipment savedEquipment = null;

        ImageEquipment imageWithId = imageEquipmentService.create(icon.getOriginalFilename());
        FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
        equipment.setIcon(imageWithId);

        if (uploadStatus == FileService.UploadStatus.OK) {
            savedEquipment = equipmentRepository.save(equipment);
            imageEquipmentService.updateEquipment(imageWithId, savedEquipment);
        }

        if (oldIcon != null) {
            deleteOldIcon(oldIcon);
        }

        return savedEquipment;
    }

    /**
     * Remove given equipment from other equipments having this equipment in their subObjects
     *
     * @param equipment the equipment to remove from lists
     */
    private void removeEquipmentInSubObjects(@NonNull Equipment equipment) {
        for (Equipment equipment1 : equipmentRepository.findBySubobjects(equipment)) {
            List<Equipment> subObjects = equipment1.getSubobjects();
            List<Long> subObjectIds = subObjects.stream().map(Equipment::getId).collect(Collectors.toList());
            if (subObjectIds.contains(equipment.getId())) {
                subObjects.removeIf(obj -> obj.getId() == equipment.getId());
            }
        }
    }

    /**
     * get the rules linked with given equipment through questionnaires
     *
     * @param equipmentReference the equipment reference
     * @return the rules linked with given equipment through questionnaires, mapped by rules category name
     */
    public Map<String, List<Rule>> getAssociatedRulesMap(@NonNull String equipmentReference) {
        List<Rule> associatedRules = questionnaireObjectService.getAssociatedRulesWithEquipment(equipmentReference);

        return ruleService.getRulesByRulesCategoryNameMap(associatedRules);
    }

    private void deleteOldIcon(Image oldImage) {
        if (!oldImage.isPublished()) {
            String fileNameWithExtension = oldImage.getFileNameWithExtension();
            imageService.delete(oldImage.getUuid());
            fileService.deleteIcon(fileNameWithExtension);
        } else if (oldImage instanceof ImageEquipment) {
            imageEquipmentService.updateEquipment((ImageEquipment) oldImage, null);
        }
    }
}
