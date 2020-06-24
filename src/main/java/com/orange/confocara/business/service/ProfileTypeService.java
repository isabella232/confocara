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
import com.orange.confocara.connector.persistence.repository.ProfileTypeRepository;
import com.orange.confocara.presentation.view.controller.helper.ProfileTypeDeletionError;
import com.orange.confocara.presentation.view.controller.helper.ProfileTypeDeletionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileTypeService {

    private final ProfileTypeRepository profileTypeRepository;

    private final RulesCategoryService rulesCategoryService;

    private final RuleService ruleService;

    private final FileService fileService;

    private final ImageProfileTypeService imageProfileTypeService;

    private final ImageService imageService;

    @Transactional
    public List<ProfileType> all() {
        return (List<ProfileType>) profileTypeRepository.findAll();
    }

    @Transactional
    public ProfileType withId(long id) {
        return profileTypeRepository.findOne(id);
    }

    @Transactional
    public List<ProfileType> withRulesCategory(RulesCategory rulesCategory) {
        return profileTypeRepository.findByRulesCategories(rulesCategory);
    }

    @Transactional
    public List<ProfileType> withIds(List<Long> ids) {
        return (List<ProfileType>) profileTypeRepository.findAll(ids);
    }

    @Transactional
    public List<ProfileType> withName(String name) {
        return profileTypeRepository.filterWithName(name);
    }

    @Transactional
    public ProfileType withReference(String reference) {
        return profileTypeRepository.findByReference(reference);
    }

    @Transactional
    public ProfileType create(ProfileType profileType) {
        return profileTypeRepository.save(profileType);
    }

    @Transactional
    public ProfileType createOrUpdateWithIconInTx(ProfileType profileType, MultipartFile icon, ImageProfileType oldIcon) {
        ProfileType savedProfileType = null;

        ImageProfileType imageWithId = imageProfileTypeService.create(icon.getOriginalFilename());
        FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
        profileType.setIcon(imageWithId);

        if (uploadStatus == FileService.UploadStatus.OK) {
            savedProfileType = profileTypeRepository.save(profileType);
            imageProfileTypeService.updateProfileType(imageWithId, savedProfileType);
        }

        if (oldIcon != null) {
            deleteOldIcon(oldIcon);
        }

        return savedProfileType;
    }

    @Transactional
    public ProfileType createOrUpdateWithPublishedImage(ProfileType profileType, Image publishedImage, Image oldImage) {
        ImageProfileType image = new ImageProfileType();
        image.setPublished(publishedImage.isPublished());
        image.setExtension(publishedImage.getExtension());
        image.setImageName(publishedImage.getImageName());
        image.setUuid(publishedImage.getUuid());
        imageService.delete(publishedImage.getUuid());
        ImageProfileType imageProfileType = imageProfileTypeService.create(image);
        profileType.setIcon(imageProfileType);

        ProfileType savedProfileType = profileTypeRepository.save(profileType);
        imageProfileTypeService.updateProfileType(imageProfileType, savedProfileType);

        if (oldImage != null) {
            deleteOldIcon(oldImage);
        }

        return savedProfileType;
    }

    @Transactional
    public ProfileTypeDeletionHelper delete(long id) {
        ProfileType profileTypeToDelete = profileTypeRepository.findOne(id);

        if (profileTypeToDelete != null) {
            String conflictualRulesCategoryName = getFirstConflictualRulesCategory(profileTypeToDelete);
            if (conflictualRulesCategoryName == null) {
                String conflictualRuleRef = getFirstConflictualRuleReference(profileTypeToDelete);
                if (conflictualRuleRef == null) {
                    removeFromRules(profileTypeToDelete);
                    removeFromRulesCategories(profileTypeToDelete);

                    if (profileTypeToDelete.getIcon().isPublished()) {
                        imageProfileTypeService.updateProfileType(profileTypeToDelete.getIcon(), null);
                        profileTypeToDelete.setIcon(null);
                    } else {
                        fileService.deleteIcon(profileTypeToDelete.getIcon().getFileNameWithExtension());
                    }

                    profileTypeRepository.delete(id);
                    return null;
                } else {
                    return new ProfileTypeDeletionHelper(ProfileTypeDeletionError.RULE, conflictualRuleRef);
                }
            } else {
                // there is 1 RC that only have this profile type => can't have no linked profile type
                return new ProfileTypeDeletionHelper(ProfileTypeDeletionError.RULES_CATEGORY, conflictualRulesCategoryName);
            }
        }
        return null;
    }

    @Transactional
    public ProfileType update(ProfileType profileType) {
        return profileTypeRepository.save(profileType);
    }

    @Transactional
    public boolean isAvailable(String name) {
        ProfileType profileType = profileTypeRepository.findByName(name.trim().replaceAll("\\s+", " "));

        return profileType == null;
    }

    /**
     * A rules category must have at least one profile type to keep rules relevance
     * if profileType is the only profile type associated with a rules category,
     * then it should not be deleted
     *
     * @param profileType the profile type to analyse
     * @return the first rules category that would have empty profile types if profileType is deleted.
     * If there is no conflictual rules category, then return null
     */
    @Transactional
    public String getFirstConflictualRulesCategory(@NonNull ProfileType profileType) {
        for (RulesCategory rulesCategory : profileType.getRulesCategories()) {
            if (rulesCategory.getProfileTypes().size() <= 1) {
                return rulesCategory.getName();
            }
        }
        return null;
    }

    /**
     * A rule must have one impact different than "non concerné"
     * if profileType has the only value that is not "non concerné", then
     * it should not be deleted to keep rule relevance
     *
     * @param profileType the profile type to analyse deletion impact
     * @return the first rule reference that would only have "non concerné" impacts if profileType is deleted.
     * If there is no conflictual rule, then return null
     */
    @Transactional
    public String getFirstConflictualRuleReference(@NonNull ProfileType profileType) {
        for (RulesCategory rulesCategory : profileType.getRulesCategories()) {
            List<Rule> rules = ruleService.withRulesCategory(rulesCategory);
            for (Rule rule : rules) {
                boolean isValid = false;
                for (RuleImpact impact : rule.getRuleImpacts()) {
                    if (!ImpactValueService.NO_IMPACT.equals(impact.getImpact().getName())
                            && !impact.getProfileType().getId().equals(profileType.getId())) {
                        isValid = true;
                        break;
                    }
                }

                if (!isValid) {
                    return rule.getReference();
                }
            }
        }
        return null;
    }

    /**
     * When rulesCategory is updated, add profile types new links
     *
     * @param rulesCategory the updated rules category
     */
    @Transactional
    public void updateRulesCategoriesOnUpdateRulesCategoryProfileTypes(@NonNull RulesCategory rulesCategory) {
        List<ProfileType> profileTypesFromRulesCategory = rulesCategory.getProfileTypes();
        for (ProfileType profileType : profileTypesFromRulesCategory) {
            List<Long> rulesCategoryIdsFromPT = profileType.getRulesCategories().stream().map(RulesCategory::getId).collect(Collectors.toList());
            if (!rulesCategoryIdsFromPT.contains(rulesCategory.getId())) {
                profileType.getRulesCategories().add(rulesCategory);
            }
        }

        profileTypeRepository.save(profileTypesFromRulesCategory);
    }

    /**
     * When rulesCategory is updated, remove it from profile types which are no longer linked with it
     * Then, update also rule impacts
     *
     * @param oldRulesCategoryProfileTypes    old rules category's profiles
     * @param newRulesCategoryProfileTypeList new rules category's profiles
     * @param rulesCategoryId                 the rules category id
     */
    @Transactional
    public void removeRulesCategoryOnEditRulesCategory(@NonNull List<ProfileType> oldRulesCategoryProfileTypes,
                                                       @NonNull List<ProfileType> newRulesCategoryProfileTypeList,
                                                       Long rulesCategoryId) {
        List<ProfileType> profileTypesToUpdate = new ArrayList<>();
        for (ProfileType oldProfileType : oldRulesCategoryProfileTypes) {
            if (!newRulesCategoryProfileTypeList.contains(oldProfileType)) {
                oldProfileType.getRulesCategories().removeIf(obj -> Objects.equals(obj.getId(), rulesCategoryId));
                profileTypesToUpdate.add(oldProfileType);
            }
        }

        if (!profileTypesToUpdate.isEmpty()) {
            profileTypeRepository.save(profileTypesToUpdate);
        }

        ruleService.updateImpactsOnEditRulesCategory(oldRulesCategoryProfileTypes, newRulesCategoryProfileTypeList, rulesCategoryId);
    }

    /**
     * returns profile types having rulesCategory mapped by profile type id
     *
     * @param rulesCategory the rules category
     * @return profile types having rulesCategory mapped by profile type id
     */
    public Map<Long, ProfileType> getProfileTypeMap(RulesCategory rulesCategory) {
        List<ProfileType> allProfileTypes = withRulesCategory(rulesCategory);
        Map<Long, ProfileType> profileTypeMap = new HashMap<>();

        for (ProfileType profileType : allProfileTypes) {
            profileTypeMap.put(profileType.getId(), profileType);
        }

        return profileTypeMap;
    }

    private void removeFromRules(ProfileType profileTypeToDelete) {
        ruleService.removeProfileTypeFromExistingRules(profileTypeToDelete);
    }

    private void removeFromRulesCategories(ProfileType profileTypeToDelete) {
        rulesCategoryService.removeProfileTypeFromRulesCategories(profileTypeToDelete);
    }

    private void deleteOldIcon(Image oldImage) {
        if (!oldImage.isPublished()) {
            String fileNameWithExtension = oldImage.getFileNameWithExtension();
            imageService.delete(oldImage.getUuid());
            fileService.deleteIcon(fileNameWithExtension);
        } else if (oldImage instanceof ImageProfileType) {
            imageProfileTypeService.updateProfileType((ImageProfileType) oldImage, null);
        }
    }
}
