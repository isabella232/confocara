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

package com.orange.confocara.presentation.view.controller;

import com.orange.confocara.business.service.FileService;
import com.orange.confocara.business.service.ImageService;
import com.orange.confocara.business.service.ProfileTypeService;
import com.orange.confocara.connector.persistence.model.Image;
import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.presentation.view.controller.helper.ProfileTypeDeletionError;
import com.orange.confocara.presentation.view.controller.helper.ProfileTypeDeletionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Locale;

@Controller
public class ProfileTypeController {

    public static final String PROFILE_TYPE_NAME_FILTER_COOKIE = "profileTypeNameFilter";
    private static final String PROFILE_TYPES = "profileTypes";
    private static final String USERNAME = "username";
    private static final String PROFILE_TYPE = "profileType";
    private static final String PROFILE_TYPE_LIST = "profileTypeList";
    private static final String REFERENCE = "reference";
    private static final String ADD_PROFILE_TYPE = "addProfileType";
    private static final String ERR_ADD_PROFILE_TYPE_NAME = "err_add_profile_type_name";
    private static final String ERR_IMG_FORMAT = "err_img_format";
    private static final String REDIRECT_ADMIN_PROFILE_TYPES = "redirect:/admin/profile-types";
    private static final String ERR_ADD_PROFILE_TYPE = "err_add_profile_type";
    private static final String REDIRECT_ADMIN_PROFILE_TYPES_ADD = "redirect:/admin/profile-types/add";

    private static final String ICON = "icon";
    private static final String ID = "id";
    private static final String ERR_EDIT_PROFILE_TYPE_NAME = "err_edit_profile_type_name";
    private static final String NAME = "name";
    private static final String ERR_EDIT_PROFILE_TYPE = "err_edit_profile_type";
    private static final String REDIRECT_ADMIN_PROFILE_TYPES_EDIT = "redirect:/admin/profile-types/edit";

    private static final String PROFILE_TYPE_EDIT_ID_COOKIE = "ProfTypeEditCookieId";
    private static final String DELETE_PROFILE_TYPE_RC_ERROR = "delete_profileType_rules_category_error";
    private static final String DELETE_PROFILE_TYPE_RULE_ERROR = "delete_profileType_rule_error";

    @Autowired
    private ProfileTypeService profileTypeService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/profile-types")
    public String index(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());
        model.addAttribute(PROFILE_TYPE_LIST, profileTypeService.all());

        return PROFILE_TYPES;
    }

    @GetMapping(value = "/admin/profile-types/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/profileTypesHelper";
    }

    @GetMapping(value = "/admin/profile-types/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addProfileTypeHelper";
    }

    @GetMapping(value = "/admin/profile-types/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editProfileTypeHelper";
    }

    @GetMapping("/admin/profile-types/add")
    public String addProfileType(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(PROFILE_TYPE)) {
            final ProfileType profileType = ProfileType.builder()
                    .id(-1L)
                    .reference("")
                    .build();

            model.addAttribute(PROFILE_TYPE, profileType);
        }

        return ADD_PROFILE_TYPE;
    }

    @RequestMapping(value = "/admin/profile-types/create", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnAddPage(@RequestParam(NAME) String name,
                                                   @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName) {

        ProfileType profileType = getProfileType("", name);
        Image publishedImage = imageService.findByImageName(iconName);
        profileTypeService.createOrUpdateWithPublishedImage(profileType, publishedImage, null);

        return REDIRECT_ADMIN_PROFILE_TYPES;
    }

    @RequestMapping(value = "/admin/profile-types/create", method = RequestMethod.POST, params = "action=create")
    public String createProfileType(@RequestParam(NAME) String name,
                                    @RequestParam(ICON) MultipartFile icon,
                                    RedirectAttributes redirectAttributes, Locale locale) {
        boolean isDataValid = true;
        if (!profileTypeService.isAvailable(name)) {
            redirectAttributes.addFlashAttribute(ERR_ADD_PROFILE_TYPE_NAME, "");
            isDataValid = false;
        }
        ProfileType profileType = getProfileType("", name);

        if (!fileService.isImageFile(icon)) {
            redirectAttributes.addFlashAttribute(ERR_IMG_FORMAT, "");
            isDataValid = false;
        }
        if (isDataValid) {
            Image imageDbWithSameName = imageService.findByImageName(icon.getOriginalFilename());

            if (imageDbWithSameName != null) {
                FileService.UploadStatus status = imageService.isPublishedAndReusable(icon.getOriginalFilename()) ? FileService.UploadStatus.ALREADY_PUBLISHED : FileService.UploadStatus.ALREADY_EXISTS;

                if (status == FileService.UploadStatus.ALREADY_PUBLISHED) {
                    redirectAttributes.addFlashAttribute(FileService.ICON_IS_PUBLISHED, fileService.formatOriginalFilename(icon.getOriginalFilename()));
                }

                addProfileTypeAndUploadErrorMessageToRedirectAttributes(redirectAttributes, locale, profileType, status);
                return REDIRECT_ADMIN_PROFILE_TYPES_ADD;
            } else {
                ProfileType savedProfileType = profileTypeService.createOrUpdateWithIconInTx(profileType, icon, null);

                if (savedProfileType == null) {
                    addProfileTypeAndUploadErrorMessageToRedirectAttributes(redirectAttributes, locale, profileType, FileService.UploadStatus.NOK);
                    return REDIRECT_ADMIN_PROFILE_TYPES_ADD;
                } else {
                    return REDIRECT_ADMIN_PROFILE_TYPES;
                }
            }
        } else {
            redirectAttributes.addFlashAttribute(ERR_ADD_PROFILE_TYPE, "");
            redirectAttributes.addFlashAttribute(PROFILE_TYPE, profileType);
            return REDIRECT_ADMIN_PROFILE_TYPES_ADD;
        }
    }

    @GetMapping("/admin/profile-types/edit")
    public String editProfileType(Principal principal, @Param(value = ID) Long id,
                                  @CookieValue(value = PROFILE_TYPE_EDIT_ID_COOKIE, required = false) String idCookie,
                                  Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(PROFILE_TYPE_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        model.addAttribute(USERNAME, principal.getName());
        ProfileType profileType = profileTypeService.withId(idFinal);
        model.addAttribute(PROFILE_TYPE, profileType);
        model.addAttribute(ICON, profileType.getIcon());
        model.addAttribute(ID, idFinal);

        return "editProfileType";
    }

    @RequestMapping(value = "/admin/profile-types/update", method = RequestMethod.POST, params = "action=update")
    public String updateProfileType(@RequestParam(REFERENCE) String reference,
                                    @RequestParam(NAME) String name,
                                    @RequestParam(ICON) MultipartFile icon, @RequestParam long id, Locale locale, RedirectAttributes redirectAttributes) {
        boolean isDataValid = true;

        if (!profileTypeService.isAvailable(name) && !name.trim().replaceAll("\\s+", " ").equalsIgnoreCase(profileTypeService.withId(id).getName())) {
            redirectAttributes.addFlashAttribute(ERR_EDIT_PROFILE_TYPE_NAME, "");
            isDataValid = false;
        }
        if (!icon.isEmpty() && !fileService.isImageFile(icon)) {
            redirectAttributes.addFlashAttribute(ERR_IMG_FORMAT, "");
            isDataValid = false;
        }

        if (isDataValid) {
            ImageProfileType oldIcon = profileTypeService.withId(id).getIcon();

            if (icon.isEmpty()) {
                final ProfileType profileType = ProfileType.builder().id(id)
                        .reference(reference)
                        .name(name.trim().replaceAll("\\s+", " "))
                        .icon(oldIcon)
                        .build();
                profileTypeService.update(profileType);
            } else {
                Image imageDbWithSameName = imageService.findByImageName(icon.getOriginalFilename());

                if (imageDbWithSameName != null) {
                    FileService.UploadStatus status = imageService.isPublishedAndReusable(icon.getOriginalFilename()) ? FileService.UploadStatus.ALREADY_PUBLISHED : FileService.UploadStatus.ALREADY_EXISTS;

                    if (status == FileService.UploadStatus.ALREADY_PUBLISHED) {
                        redirectAttributes.addFlashAttribute(FileService.ICON_IS_PUBLISHED, fileService.formatOriginalFilename(icon.getOriginalFilename()));
                    }
                    addUploadErrorAndIdToRedirectedAttributes(id, locale, redirectAttributes, status);
                    return REDIRECT_ADMIN_PROFILE_TYPES_EDIT;
                } else {
                    final ProfileType profileType = ProfileType.builder().id(id)
                            .reference(reference)
                            .name(name.trim().replaceAll("\\s+", " "))
                            .build();

                    profileTypeService.createOrUpdateWithIconInTx(profileType, icon, oldIcon);
                }
            }
            return REDIRECT_ADMIN_PROFILE_TYPES;
        } else {
            addErrorAndIdToRedirectedAttributes(id, redirectAttributes);
            return REDIRECT_ADMIN_PROFILE_TYPES_EDIT;
        }
    }

    @RequestMapping(value = "/admin/profile-types/update", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnEditPage(@RequestParam(REFERENCE) String reference,
                                                    @RequestParam(NAME) String name,
                                                    @RequestParam long id,
                                                    @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName) {


        Image oldImage = profileTypeService.withId(id).getIcon();

        final ProfileType profileType = ProfileType.builder().id(id)
                .reference(reference)
                .name(name.trim().replaceAll("\\s+", " "))
                .build();

        Image publishedImage = imageService.findByImageName(iconName);
        profileTypeService.createOrUpdateWithPublishedImage(profileType, publishedImage, oldImage);

        return REDIRECT_ADMIN_PROFILE_TYPES;
    }

    @RequestMapping(value = "/admin/profile-types/delete", method = RequestMethod.GET)
    public String deleteProfileType(@RequestParam(value = ID) Long id, RedirectAttributes redirectAttributes) {
        ProfileTypeDeletionHelper deletionError = profileTypeService.delete(id);

        if (deletionError != null) {
            if (deletionError.getErrorType().equals(ProfileTypeDeletionError.RULES_CATEGORY)) {
                redirectAttributes.addFlashAttribute(DELETE_PROFILE_TYPE_RC_ERROR, deletionError.getValue());
            } else if (deletionError.getErrorType().equals(ProfileTypeDeletionError.RULE)) {
                redirectAttributes.addFlashAttribute(DELETE_PROFILE_TYPE_RULE_ERROR, deletionError.getValue());
            }
        }

        return REDIRECT_ADMIN_PROFILE_TYPES;
    }

    private ProfileType getProfileType(@RequestParam(REFERENCE) String reference,
                                       @RequestParam(NAME) String name) {

        ProfileType profileType = ProfileType.builder().build();
        profileType.setReference(reference);
        profileType.setName(name.trim().replaceAll("\\s+", " "));

        return profileType;
    }

    private void addProfileTypeAndUploadErrorMessageToRedirectAttributes(RedirectAttributes redirectAttributes, Locale locale, ProfileType profileType, FileService.UploadStatus uploadStatus) {
        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(uploadStatus.getMessage(), null, locale));
        redirectAttributes.addFlashAttribute(ERR_ADD_PROFILE_TYPE, "");
        redirectAttributes.addFlashAttribute(PROFILE_TYPE, profileType);
    }

    private void addErrorAndIdToRedirectedAttributes(@RequestParam long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERR_EDIT_PROFILE_TYPE, "");
        redirectAttributes.addAttribute(ID, id);
    }

    private void addUploadErrorAndIdToRedirectedAttributes(@RequestParam long id, Locale locale, RedirectAttributes redirectAttributes, FileService.UploadStatus uploadStatus) {
        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(uploadStatus.getMessage(), null, locale));
        addErrorAndIdToRedirectedAttributes(id, redirectAttributes);
    }
}
