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
import com.orange.confocara.connector.persistence.repository.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final RuleRepository ruleRepository;
    private final QuestionRepository questionRepository;
    private final QuestionnaireObjectRepository questionnaireObjectRepository;
    private final RulesetRepository rulesetRepository;
    private final IllustrationRepository illustrationRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final ImageAccountService imageAccountService;
    private final ImageService imageService;

    @Transactional
    public List<User> all() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional
    public User withId(long id) {
        return userRepository.findOne(id);
    }

    @Transactional
    public User lastOne() {
        return userRepository.findTopByOrderByIdDesc();
    }

    @Transactional
    public User getUserByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public List<User> withEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isUsernameAvailable(@NonNull String name) {
        return getUserByUsername(name.trim().replaceAll("\\s+", " ")) == null;
    }

    public boolean isEmailAvailable(@NonNull String email) {
        return withEmail(email.trim()).isEmpty();
    }

    @Transactional
    public User create(@NonNull User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Transactional
    public User createUserWithIconInTx(User user, MultipartFile icon) {
        User savedUser = null;

        ImageAccount imageWithId = imageAccountService.create(icon.getOriginalFilename());
        FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setImage(imageWithId);

        if (uploadStatus == FileService.UploadStatus.OK) {
            savedUser = userRepository.save(user);
            imageAccountService.updateAccount(imageWithId, savedUser);
        }

        return savedUser;
    }

    @Transactional
    public List<User> create(@NonNull List<User> users) {
        for (User user : users) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        return (List<User>) userRepository.save(users);
    }

    @Transactional
    public User updateUserWithIconInTx(User user, MultipartFile icon, Image oldIcon, boolean encodePassword) {
        User savedUser = null;

        if (encodePassword) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        if (icon != null) {
            ImageAccount imageWithId = imageAccountService.create(icon.getOriginalFilename());
            FileService.UploadStatus uploadStatus = fileService.uploadIcon(icon, imageWithId);
            user.setImage(imageWithId);

            if (uploadStatus == FileService.UploadStatus.OK) {
                savedUser = userRepository.save(user);
                imageAccountService.updateAccount(imageWithId, savedUser);
            }

            if (oldIcon != null) {
                deleteOldIcon(oldIcon);
            }
        } else if (user.isShouldDeleteIcon() && oldIcon != null) {
            user.setImage(null);
            deleteOldIcon(oldIcon);
            savedUser = userRepository.save(user);
        } else {
            user.setImage(oldIcon);
            savedUser = userRepository.save(user);
        }

        return savedUser;
    }

    @Transactional
    public void delete(long id) {
        User user = userRepository.findOne(id);
        List<Equipment> equipmentsWithUser = equipmentRepository.findByUser(user);

        for (Equipment equipmentWithUser : equipmentsWithUser) {
            equipmentWithUser.setUser(null);
        }

        List<Rule> rulesWithUser = ruleRepository.findByUser(user);

        for (Rule ruleWithUser : rulesWithUser) {
            ruleWithUser.setUser(null);
        }

        List<Question> questionsWithUser = questionRepository.findByUser(user);

        for (Question questionWithUser : questionsWithUser) {
            questionWithUser.setUser(null);
        }

        List<QuestionnaireObject> questionnairesWithUser = questionnaireObjectRepository.findByUser(user);

        for (QuestionnaireObject questionnaireWithUser : questionnairesWithUser) {
            questionnaireWithUser.setUser(null);
        }

        List<Ruleset> rulesetsWithUser = rulesetRepository.findByUser(user);

        for (Ruleset rulesetWithUser : rulesetsWithUser) {
            rulesetWithUser.setUser(null);
        }

        List<Illustration> illustrationsWithUser = illustrationRepository.findByUser(user);

        for (Illustration illustrationWithUser : illustrationsWithUser) {
            illustrationWithUser.setUser(null);
        }

        if (user.getImage() != null) {
            fileService.deleteIcon(user.getImage().getFileNameWithExtension());
        }

        userRepository.delete(id);
    }

    private void deleteOldIcon(Image oldImage) {
        if (!oldImage.isPublished()) {
            String fileNameWithExtension = oldImage.getFileNameWithExtension();
            imageService.delete(oldImage.getUuid());
            fileService.deleteIcon(fileNameWithExtension);
        } else if (oldImage instanceof ImageAccount) {
            imageAccountService.updateAccount((ImageAccount) oldImage, null);
        }
    }
}
