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

package com.orange.confocara.presentation.view.validator;

import com.orange.confocara.business.service.FileService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AccountValidator implements Validator {

    public static final String CONFIRM_PASS_HASH = "confirmPasswordHash";
    public static final String PASS_HASH = "passwordHash";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "tel";
    public static final String FIRSTNAME_LASTNAME = "name";
    public static final String IMAGE_DATA = "imageData";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_PATTERN = "[0-9]{10}";

    private static final int USERNAME_LENGTH_MIN = 4;
    private static final int USERNAME_LENGTH_MAX = 16;
    private static final int PASSWORD_LENGTH_MIN = 4;
    private static final int PASSWORD_LENGTH_MAX = 16;
    private static final int EMAIL_MAX_LENGTH = 254;

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateAll((User) target, errors, true);
    }

    public void validateForUpdate(Object target, Errors errors) {
        validateAll((User) target, errors, false);
    }

    private void validateAll(User user, Errors errors, boolean creation) {
        String mail = user.getEmail();

        validatePassword(errors, user, creation);

        validateUsername(errors, user, creation);

        validateEmail(errors, mail, user, creation);

        validatePhoneFormat(errors, user);

        validateIcon(errors, user);

        validateFirstNameAndLastName(errors, user);
    }

    private void validateIcon(Errors errors, User user) {
        if (user.getImageData() != null && !fileService.isImageFile(user.getImageData())
                && !user.getImageData().isEmpty()) {
            errors.rejectValue(IMAGE_DATA, "incorrect_icon_format");
        }
    }

    private void validatePassword(Errors errors, User user, boolean creation) {
        if (!creation && user.getConfirmPasswordHash().isEmpty() && user.getPasswordHash().isEmpty()) {
            return;
        }
        if (!user.getConfirmPasswordHash().equals(user.getPasswordHash())) {
            errors.rejectValue(CONFIRM_PASS_HASH, "accounts.password_not_match");
        } else if (user.getPasswordHash().length() < PASSWORD_LENGTH_MIN || user.getPasswordHash().length() > PASSWORD_LENGTH_MAX) {
            errors.rejectValue(PASS_HASH, "accounts.password_invalid_length");
        }
    }

    private void validateUsername(Errors errors, User user, boolean creation) {
        if (creation) {
            if (!userService.isUsernameAvailable(user.getUsername())) {
                errors.rejectValue(USERNAME, "accounts.username_already_exists");
            }
        } else {
            if (!userService.isUsernameAvailable(user.getUsername()) && !user.getUsername().equalsIgnoreCase(userService.withId(user.getId()).getUsername())) {
                errors.rejectValue(USERNAME, "accounts.username_already_exists");
            }
        }

        if (!errors.hasFieldErrors(USERNAME) && (user.getUsername().trim().replaceAll("\\s+", " ").length() < USERNAME_LENGTH_MIN
                || user.getUsername().trim().replaceAll("\\s+", " ").length() > USERNAME_LENGTH_MAX)) {
            errors.rejectValue(USERNAME, "accounts.username_invalid_length");
        }
    }

    private void validateEmail(Errors errors, String mail, User user, boolean creation) {
        String trimmedMail = mail.trim().replaceAll("\\s+", " ");
        if (creation) {
            if (!userService.isEmailAvailable(trimmedMail)) {
                errors.rejectValue(EMAIL, "accounts.mail_already_exists");
            }
        } else {
            if (!userService.isEmailAvailable(trimmedMail) && !trimmedMail.equals(userService.withId(user.getId()).getEmail())) {
                errors.rejectValue(EMAIL, "accounts.mail_already_exists");
            }
        }

        if (!errors.hasFieldErrors(EMAIL)) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(trimmedMail);
            if (!matcher.matches() || trimmedMail.length() > EMAIL_MAX_LENGTH) {
                errors.rejectValue(EMAIL, "accounts.incorrect_email");
            }
        }
    }

    private void validatePhoneFormat(Errors errors, User user) {
        if (!user.getTel().isEmpty()) {
            Pattern pattern = Pattern.compile(PHONE_PATTERN);
            Matcher matcher = pattern.matcher(user.getTel().trim());
            if (!matcher.matches()) {
                errors.rejectValue(PHONE, "accounts.incorrect_phone");
            }
        }
    }

    private void validateFirstNameAndLastName(Errors errors, User user) {
        if (user.getFirstname().matches(".*\\d.*") || user.getName().matches(".*\\d.*")) {
            errors.rejectValue(FIRSTNAME_LASTNAME, "accounts.incorrect_name");
        }
    }
}
