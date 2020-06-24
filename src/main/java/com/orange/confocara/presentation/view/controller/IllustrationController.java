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
import com.orange.confocara.business.service.IllustrationService;
import com.orange.confocara.business.service.ImageService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.Image;
import com.orange.confocara.connector.persistence.model.ImageIllustration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
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
import java.util.Date;
import java.util.Locale;

@Controller
public class IllustrationController {
    public static final String ILLUSTRATION_FILTER_COOKIE = "illustrationFilter";
    private static final String ILLUSTRATION = "illustration";
    private static final String ILLUSTRATIONS = "illustrations";
    private static final String USERNAME = "username";
    private static final String REDIRECT_ILLUSTRATIONS = "redirect:/illustrations";
    private static final String COMMENT = "comment";
    private static final String TITLE = "title";
    private static final String ORIGIN = "origin";
    private static final String IMAGE = "image";
    private static final String SHOULD_DELETE_ICON = "shouldDeleteIcon";
    private static final String ID = "id";
    private static final String REDIRECT_ILLUSTRATIONS_EDIT = "redirect:/illustrations/edit";
    private static final String REDIRECT_ILLUSTRATIONS_ADD = "redirect:/illustrations/add";
    private static final String EDIT_ILLUSTRATION = "editIllustration";
    private static final String ADD_ILLUSTRATION = "addIllustration";
    private static final String ILLUSTRATION_EDIT_ID_COOKIE = "editCookieId";

    private static final String ERR_ADD_ILLUSTRATION = "err_add_illustration";
    private static final String ERR_ADD_ILLUSTRATION_NAME = "err_add_illustration_name";
    private static final String ERR_IMG_FORMAT = "err_img_format";
    private static final String ERR_REQUIRED_FIELD = "err_required_field";

    @Autowired
    private IllustrationService illustrationService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ImageService imageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/illustrations")
    public String index(Principal principal, Model model) {
        model.addAttribute(ILLUSTRATIONS, illustrationService.all());
        model.addAttribute(USERNAME, principal.getName());

        return ILLUSTRATIONS;
    }

    @GetMapping(value = "/illustrations/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/illustrationsHelper";
    }

    @GetMapping(value = "/illustrations/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addIllustrationHelper";
    }

    @GetMapping(value = "/illustrations/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editIllustrationHelper";
    }

    @GetMapping("/illustrations/add")
    public String addIllustration(Principal principal, Model model) {
        Illustration illustration = new Illustration();
        model.addAttribute(ILLUSTRATION, illustration);
        model.addAttribute(USERNAME, principal.getName());

        return ADD_ILLUSTRATION;
    }

    @GetMapping("/illustrations/edit")
    public String editIllustration(Principal principal, @Param(value = ID) Long id,
                                   @CookieValue(value = ILLUSTRATION_EDIT_ID_COOKIE, required = false) String idCookie,
                                   Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(ILLUSTRATION_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        if (!model.containsAttribute(ILLUSTRATION)) {
            Illustration illustration = illustrationService.withId(idFinal);

            model.addAttribute(ILLUSTRATION, illustration);
            model.addAttribute(IMAGE, illustration.getImage());
        }

        model.addAttribute(ID, idFinal);
        model.addAttribute(USERNAME, principal.getName());

        return EDIT_ILLUSTRATION;
    }

    @RequestMapping(value = "/illustrations/create", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnAddPage(@RequestParam(COMMENT) String comment,
                                                   @RequestParam(TITLE) String title,
                                                   @RequestParam(ORIGIN) String origin,
                                                   @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName,
                                                   Principal principal) {

        Image publishedImage = imageService.findByImageName(iconName);
        Illustration illustration = getIllustration(comment, title, origin);
        illustration.setUser(userService.getUserByUsername(principal.getName()));
        illustration.setReference("");

        illustrationService.createOrUpdateWithPublishedImage(illustration, publishedImage, null);

        return REDIRECT_ILLUSTRATIONS;
    }

    @RequestMapping(value = "/illustrations/create", method = RequestMethod.POST, params = "action=create")
    public String createIllustration(@RequestParam(COMMENT) String comment,
                                     @RequestParam(TITLE) String title,
                                     @RequestParam(ORIGIN) String origin,
                                     @RequestParam(IMAGE) MultipartFile image,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal,
                                     Locale locale) {

        boolean isDataValid = true;

        if (!illustrationService.isAvailable(title)) {
            redirectAttributes.addFlashAttribute(ERR_ADD_ILLUSTRATION_NAME, "");
            isDataValid = false;
        }

        isDataValid = areCommentAndImageDataValid(comment, image, redirectAttributes, isDataValid, false);

        if (isDataValid) {
            Illustration illustration = getIllustration(comment, title, origin);
            illustration.setUser(userService.getUserByUsername(principal.getName()));
            illustration.setReference("");

            if (!image.isEmpty()) {
                Image imageDbWithSameName = imageService.findByImageName(image.getOriginalFilename());

                if (imageDbWithSameName != null) {
                    FileService.UploadStatus status = imageService.isPublishedAndReusable(image.getOriginalFilename()) ? FileService.UploadStatus.ALREADY_PUBLISHED : FileService.UploadStatus.ALREADY_EXISTS;

                    if (status == FileService.UploadStatus.ALREADY_PUBLISHED) {
                        redirectAttributes.addFlashAttribute(FileService.ICON_IS_PUBLISHED, fileService.formatOriginalFilename(image.getOriginalFilename()));
                        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, FileService.UploadStatus.ALREADY_PUBLISHED.getMessage());
                    } else {
                        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(FileService.MESSAGE_FILE_ALREADY_ADDED, null, locale));
                    }

                    addRedirectedAttributes(comment, title, origin, redirectAttributes);
                    return REDIRECT_ILLUSTRATIONS_ADD;
                } else {
                    illustrationService.createOrUpdateWithIconInTx(illustration, image, null);
                }
            } else {
                illustrationService.create(illustration);
            }

            return REDIRECT_ILLUSTRATIONS;
        } else {
            addRedirectedAttributes(comment, title, origin, redirectAttributes);

            return REDIRECT_ILLUSTRATIONS_ADD;
        }
    }

    @RequestMapping(value = "/illustrations/update", method = RequestMethod.POST, params = "action=update")
    public String updateIllustration(@RequestParam(COMMENT) String comment,
                                     @RequestParam(TITLE) String title,
                                     @RequestParam(ORIGIN) String origin,
                                     @RequestParam(IMAGE) MultipartFile image,
                                     @RequestParam(SHOULD_DELETE_ICON) boolean shouldDeleteIcon,
                                     @RequestParam Long id, RedirectAttributes redirectAttributes,
                                     Principal principal,
                                     Locale locale) {
        boolean isDataValid = true;

        Illustration illustrationBeforeUpdate = illustrationService.withId(id);

        if (!illustrationService.isAvailable(title) && !illustrationBeforeUpdate.getTitle().equalsIgnoreCase(title)) {
            redirectAttributes.addFlashAttribute(ERR_ADD_ILLUSTRATION_NAME, "");
            isDataValid = false;
        }

        Image oldImage = illustrationBeforeUpdate.getImage();
        boolean reuseOldImage = !shouldDeleteIcon && oldImage != null;
        isDataValid = areCommentAndImageDataValid(comment, image, redirectAttributes, isDataValid, reuseOldImage);

        Illustration illustration = getIllustration(comment, title, origin);
        String imageToDeleteId = null;

        if (isDataValid) {
            String imageName = fileService.formatOriginalFilename(image.getOriginalFilename());
            illustration.setId(id);
            illustration.setReference(illustrationBeforeUpdate.getReference());
            illustration.setUser(userService.getUserByUsername(principal.getName()));

            if (!imageName.isEmpty()) {
                Image imageDbWithSameName = imageService.findByImageName(image.getOriginalFilename());
                if (imageDbWithSameName != null) {
                    FileService.UploadStatus status = imageService.isPublishedAndReusable(image.getOriginalFilename()) ? FileService.UploadStatus.ALREADY_PUBLISHED : FileService.UploadStatus.ALREADY_EXISTS;
                    String message;

                    if (status == FileService.UploadStatus.ALREADY_PUBLISHED) {
                        redirectAttributes.addFlashAttribute(FileService.ICON_IS_PUBLISHED, fileService.formatOriginalFilename(image.getOriginalFilename()));
                        message = FileService.UploadStatus.ALREADY_PUBLISHED.getMessage();
                    } else {
                        message = messageSource.getMessage(FileService.MESSAGE_FILE_ALREADY_ADDED, null, locale);
                    }

                    redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, message);
                    addIllustrationRedirectedAttributes(id, comment, title, origin, redirectAttributes);
                    return REDIRECT_ILLUSTRATIONS_EDIT;
                } else {
                    if (oldImage != null) {
                        imageToDeleteId = oldImage.getUuid();
                    }

                    Illustration updatedIllustration = illustrationService.createOrUpdateWithIconInTx(illustration, image, imageToDeleteId != null ? oldImage : null);

                    if (updatedIllustration == null) {
                        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, FileService.UploadStatus.NOK);
                        addIllustrationRedirectedAttributes(id, comment, title, origin, redirectAttributes);
                        return REDIRECT_ILLUSTRATIONS_EDIT;
                    }
                }
            } else {
                if (shouldDeleteIcon && oldImage != null) {
                    imageToDeleteId = oldImage.getUuid();
                    illustration.setImage(null);
                } else if (oldImage != null) {
                    illustration.setImage((ImageIllustration) oldImage);
                }

                illustrationService.update(illustration, imageToDeleteId != null ? oldImage : null);
            }

            return REDIRECT_ILLUSTRATIONS;
        } else {
            addIllustrationRedirectedAttributes(id, comment, title, origin, redirectAttributes);

            return REDIRECT_ILLUSTRATIONS_EDIT;
        }
    }

    @RequestMapping(value = "/illustrations/update", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnEditPage(@RequestParam(COMMENT) String comment,
                                                    @RequestParam(TITLE) String title,
                                                    @RequestParam(ORIGIN) String origin,
                                                    @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName,
                                                    @RequestParam Long id,
                                                    Principal principal) {
        Image oldImage = illustrationService.withId(id).getImage();

        Illustration illustration = getIllustration(comment, title, origin);
        illustration.setId(id);
        Illustration illustration1 = illustrationService.withId(id);
        illustration.setReference(illustration1.getReference());
        illustration.setUser(userService.getUserByUsername(principal.getName()));

        Image publishedImage = imageService.findByImageName(iconName);
        illustrationService.createOrUpdateWithPublishedImage(illustration, publishedImage, oldImage);

        return REDIRECT_ILLUSTRATIONS;
    }

    @RequestMapping(value = "/illustrations/delete", method = RequestMethod.GET)
    public String deleteIllustration(@RequestParam(value = ID) Long id, Model model) {
        illustrationService.delete(id);
        model.addAttribute(ILLUSTRATIONS, illustrationService.all());

        return REDIRECT_ILLUSTRATIONS;
    }

    private void addRedirectedAttributes(@RequestParam(COMMENT) String comment,
                                         @RequestParam(TITLE) String title,
                                         @RequestParam(ORIGIN) String origin,
                                         RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(COMMENT, comment);
        redirectAttributes.addFlashAttribute(TITLE, title);
        redirectAttributes.addFlashAttribute(ORIGIN, origin);
        redirectAttributes.addFlashAttribute(ERR_ADD_ILLUSTRATION, "");
    }

    private void addIllustrationRedirectedAttributes(Long id, String comment, String title, String origin, RedirectAttributes redirectAttributes) {
        Illustration invalidIllustration = new Illustration();
        invalidIllustration.setComment(comment);
        invalidIllustration.setTitle(title);
        invalidIllustration.setOrigin(origin);
        invalidIllustration.setImage(illustrationService.withId(id).getImage());

        redirectAttributes.addFlashAttribute(ILLUSTRATION, invalidIllustration);
        redirectAttributes.addFlashAttribute(IMAGE, invalidIllustration.getImage());
        redirectAttributes.addAttribute(ID, id);
        redirectAttributes.addFlashAttribute(ERR_ADD_ILLUSTRATION, "");
    }

    private Illustration getIllustration(@RequestParam(COMMENT) String comment,
                                         @RequestParam(TITLE) String title,
                                         @RequestParam(ORIGIN) String origin) {
        Illustration illustration = new Illustration();
        illustration.setTitle(title.trim().replaceAll("\\s+", " "));
        illustration.setComment(comment.trim().replaceAll("\\s+", " "));
        illustration.setOrigin(origin.trim().replaceAll("\\s+", " "));
        illustration.setDate(new Date());
        return illustration;
    }

    private boolean areCommentAndImageDataValid(@RequestParam(COMMENT) String comment,
                                                @RequestParam(IMAGE) MultipartFile image,
                                                RedirectAttributes redirectAttributes,
                                                boolean isDataValid,
                                                boolean reuseOldImage) {
        boolean isDataValidFinal = isDataValid;
        if (!reuseOldImage && comment.isEmpty() && image.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERR_REQUIRED_FIELD, "");
            isDataValidFinal = false;
        }

        if (!image.isEmpty() && !fileService.isImageFile(image)) {
            redirectAttributes.addFlashAttribute(ERR_IMG_FORMAT, "");
            isDataValidFinal = false;
        }
        return isDataValidFinal;
    }
}
