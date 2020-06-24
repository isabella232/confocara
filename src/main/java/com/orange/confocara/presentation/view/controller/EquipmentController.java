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

import com.orange.confocara.business.service.CategoryService;
import com.orange.confocara.business.service.EquipmentService;
import com.orange.confocara.business.service.FileService;
import com.orange.confocara.business.service.IllustrationService;
import com.orange.confocara.business.service.ImageService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.Image;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EquipmentController {

    public static final String OBJECT_CATEGORY_FILTER_COOKIE = "objectCategoryFilter";
    private static final String OBJECTS = "objects";
    private static final String EQUIPMENT = "equipment";
    private static final String CATEGORIES = "categories";
    private static final String EQUIPMENTS = "equipments";
    private static final String USERNAME = "username";
    private static final String REDIRECT_OBJECTS = "redirect:/objects";
    private static final String ILLUSTRATION = "illustration";
    private static final String ILLUSTRATS = "illustrats";
    private static final String SUBOBJECTS = "subobjects";
    private static final String SUBOBJECT = "subobject";
    private static final String ADD_OBJECT = "addObject";
    private static final String TYPE = "type";
    private static final String CATEGORY_IDS = "categoryIds";
    private static final String ICON = "icon";
    private static final String EDIT_OBJECT = "editObject";
    private static final String SUBOBJECT_IDS = "subobjectIds";
    private static final String ILLUSTRATION_IDS = "illustrationIds";
    private static final String REDIRECT_OBJECTS_ADD = "redirect:/objects/add";
    private static final String REDIRECT_OBJECTS_EDIT = "redirect:/objects/edit";

    private static final String ERR_IMG_FORMAT = "err_img_format";
    private static final String ERR_ADD_EQUIPMENT = "err_add_equipment";
    private static final String ERR_ADD_EQUIPMENT_NAME = "err_add_equipment_name";

    private static final String ID = "id";
    private static final String EQUIPMENT_EDIT_COOKIE_ID = "equipmentCookieID";

    @Autowired
    private UserService userService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IllustrationService illustrationService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/objects")
    public String index(Principal principal, Model model) {
        Equipment equipment = new Equipment();
        model.addAttribute(EQUIPMENT, equipment);
        model.addAttribute(CATEGORIES, categoryService.all());
        model.addAttribute(EQUIPMENTS, equipmentService.all());
        model.addAttribute(USERNAME, principal.getName());

        return OBJECTS;
    }

    @RequestMapping(value = "/objects/details/{reference}", method = RequestMethod.GET)
    public String showAssociatedRules(@PathVariable("reference") String reference, Model model) {
        model.addAttribute("equipmentAssociatedRulesMap", equipmentService.getAssociatedRulesMap(reference));

        return OBJECTS + " :: resultsList";
    }

    @GetMapping(value = "/objects/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/objectsHelper";
    }

    @GetMapping(value = "/objects/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addObjectHelper";
    }

    @GetMapping(value = "/objects/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editObjectHelper";
    }

    @GetMapping("/objects/add")
    public String addObject(Principal principal, Model model) {

        if (!model.containsAttribute(EQUIPMENT)) {
            Equipment equipment = new Equipment();
            Category allObjectsCategory = categoryService.getCategoryByName(CategoryService.ALL_OBJECTS);
            if (allObjectsCategory != null) {
                List<String> defaultCategoryToAdd = new ArrayList<>();
                defaultCategoryToAdd.add(String.valueOf(allObjectsCategory.getId()));
                equipment.setCategoryIds(defaultCategoryToAdd);
            }
            model.addAttribute(EQUIPMENT, equipment);
        }

        model.addAttribute(ILLUSTRATION, new Illustration());
        model.addAttribute(CATEGORIES, categoryService.all());
        model.addAttribute(ILLUSTRATS, illustrationService.all());
        model.addAttribute(SUBOBJECTS, equipmentService.withType(SUBOBJECT));
        model.addAttribute(USERNAME, principal.getName());

        return ADD_OBJECT;
    }

    @RequestMapping(value = "/objects/edit", method = RequestMethod.GET)
    public String editObject(Principal principal,
                             @Param(value = ID) Long id,
                             @CookieValue(value = EQUIPMENT_EDIT_COOKIE_ID, required = false) String idCookie,
                             Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(EQUIPMENT_EDIT_COOKIE_ID, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        if (!model.containsAttribute(EQUIPMENT)) {
            Equipment equipment = equipmentService.withId(idFinal);
            List<String> illustrationIds = equipment.getIllustrations().stream().map(Illustration::getId).map(String::valueOf).collect(Collectors.toList());
            equipment.setIllustrationIds(illustrationIds);
            List<String> subObjectsIds = equipment.getSubobjects().stream().map(Equipment::getId).map(String::valueOf).collect(Collectors.toList());
            equipment.setSubobjectIds(subObjectsIds);
            List<String> categoryIds = equipment.getCategories().stream().map(Category::getId).map(String::valueOf).collect(Collectors.toList());
            equipment.setCategoryIds(categoryIds);

            model.addAttribute(EQUIPMENT, equipment);
            model.addAttribute(TYPE, equipment.getType());
            model.addAttribute(ICON, equipment.getIcon());

            List<Equipment> allSubObjectsList = equipmentService.withType(SUBOBJECT);
            if (equipment.getType().equals(SUBOBJECT)) {
                // remove equipment so it is not proposed if type changes to object
                allSubObjectsList.remove(equipment);
            }

            model.addAttribute(SUBOBJECTS, allSubObjectsList);
        }
        model.addAttribute(ILLUSTRATION, new Illustration());
        model.addAttribute(ILLUSTRATS, illustrationService.all());
        model.addAttribute(CATEGORIES, categoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        return EDIT_OBJECT;
    }

    @RequestMapping(value = "/objects/create", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnAddPage(@RequestParam("name") String name,
                                                   @RequestParam(value = CATEGORY_IDS, required = false) List<String> categoryIds,
                                                   @RequestParam(TYPE) String type,
                                                   @RequestParam("definition") String definition,
                                                   @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName,
                                                   HttpServletRequest request,
                                                   Principal principal) {

        List<Category> categories = getCategories(categoryIds);
        Equipment equipment = getEquipment(name, categories, type, definition, request, principal, null);
        Image publishedImage = imageService.findByImageName(iconName);
        equipment.setReference("");
        equipmentService.createOrUpdateWithPublishedImage(equipment, publishedImage, null);

        return REDIRECT_OBJECTS;
    }

    @RequestMapping(value = "/objects/create", method = RequestMethod.POST, params = "action=create")
    public String createObject(@RequestParam("name") String name,
                               @RequestParam(value = CATEGORY_IDS, required = false) List<String> categoryIds,
                               @RequestParam(TYPE) String type,
                               @RequestParam("definition") String definition,
                               @RequestParam(ICON) MultipartFile icon,
                               HttpServletRequest request,
                               Locale locale,
                               RedirectAttributes redirectAttributes, Principal principal) {

        boolean isDataValid = true;

        List<Category> categories = getCategories(categoryIds);

        Equipment equipment = getEquipment(name, categories, type, definition, request, principal, null);

        if (!equipmentService.isAvailable(name)) {
            redirectAttributes.addFlashAttribute(ERR_ADD_EQUIPMENT_NAME, "");
            isDataValid = false;
        }

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

                addEquipmentAndUploadErrorMessageToRedirectAttributes(locale, redirectAttributes, equipment, status, request);
                return REDIRECT_OBJECTS_ADD;
            } else {
                Equipment equipmentWithIcon = equipmentService.createEquipmentWithIconInTx(equipment, icon);

                if (equipmentWithIcon == null) {
                    addEquipmentAndUploadErrorMessageToRedirectAttributes(locale, redirectAttributes, equipment, FileService.UploadStatus.NOK, request);
                    return REDIRECT_OBJECTS_ADD;
                } else {
                    return REDIRECT_OBJECTS;
                }
            }
        } else {
            addEquipmentToRedirectAttributes(redirectAttributes, equipment, request);
            return REDIRECT_OBJECTS_ADD;
        }
    }

    @RequestMapping(value = "/objects/update", method = RequestMethod.POST, params = "action=update")
    public String updateObject(@RequestParam("name") String name,
                               @RequestParam(value = CATEGORY_IDS, required = false) List<String> categoryIds,
                               @RequestParam(TYPE) String type,
                               @RequestParam("definition") String definition,
                               @RequestParam(ICON) MultipartFile icon, @RequestParam Long id,
                               Principal principal, RedirectAttributes redirectAttributes, Locale locale,
                               HttpServletRequest request) {

        boolean isDataValid = true;

        Equipment equipmentFromDb = equipmentService.withId(id);
        if (!equipmentService.isAvailable(name)
                && id != equipmentFromDb.getId()
                && !name.equalsIgnoreCase(equipmentFromDb.getName())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_EQUIPMENT_NAME, "");
            isDataValid = false;
        }

        if (!icon.isEmpty() && !fileService.isImageFile(icon)) {
            redirectAttributes.addFlashAttribute(ERR_IMG_FORMAT, "");
            isDataValid = false;
        }

        List<Category> categories = getCategories(categoryIds);
        Equipment equipment = getEquipment(name, categories, type, definition, request, principal, id);

        if (isDataValid) {
            String iconName = fileService.formatOriginalFilename(icon.getOriginalFilename());
            Image oldIcon = equipmentFromDb.getIcon();

            if (iconName.isEmpty()) {
                equipment.setIcon(equipmentFromDb.getIcon());
                equipmentService.update(equipment);
            } else {
                Image imageDbWithSameName = imageService.findByImageName(icon.getOriginalFilename());

                if (imageDbWithSameName != null) {
                    FileService.UploadStatus status = imageService.isPublishedAndReusable(icon.getOriginalFilename()) ? FileService.UploadStatus.ALREADY_PUBLISHED : FileService.UploadStatus.ALREADY_EXISTS;

                    if (status == FileService.UploadStatus.ALREADY_PUBLISHED) {
                        redirectAttributes.addFlashAttribute(FileService.ICON_IS_PUBLISHED, fileService.formatOriginalFilename(icon.getOriginalFilename()));
                    }
                    equipment.setIcon(equipmentFromDb.getIcon());
                    addEquipmentAndUploadErrorMessageToRedirectAttributes(locale, redirectAttributes, equipment, status, request);
                    addEquipmentPropertiesToRedirectAttributes(equipment, redirectAttributes);
                    redirectAttributes.addAttribute("id", id);
                    return REDIRECT_OBJECTS_EDIT;
                } else {
                    equipmentService.updateEquipmentWithIconInTx(equipment, icon, oldIcon);
                }
            }

            return REDIRECT_OBJECTS;
        } else {
            equipment.setIcon(equipmentFromDb.getIcon());
            addEquipmentToRedirectAttributes(redirectAttributes, equipment, request);
            addEquipmentPropertiesToRedirectAttributes(equipment, redirectAttributes);
            redirectAttributes.addAttribute("id", id);
            return REDIRECT_OBJECTS_EDIT;
        }
    }

    @RequestMapping(value = "/objects/update", method = RequestMethod.POST, params = "action=with-icon")
    public String showPublishedIconIsUsedOnEditPage(@RequestParam("name") String name,
                                                    @RequestParam(value = CATEGORY_IDS, required = false) List<String> categoryIds,
                                                    @RequestParam(TYPE) String type,
                                                    @RequestParam("definition") String definition,
                                                    @RequestParam Long id,
                                                    @RequestParam(value = FileService.ICON_IS_PUBLISHED) String iconName,
                                                    Principal principal,
                                                    HttpServletRequest request) {
        Image oldImage = equipmentService.withId(id).getIcon();

        List<Category> categories = getCategories(categoryIds);
        Equipment equipment = getEquipment(name, categories, type, definition, request, principal, id);

        Image publishedImage = imageService.findByImageName(iconName);
        equipmentService.createOrUpdateWithPublishedImage(equipment, publishedImage, oldImage);

        return REDIRECT_OBJECTS;
    }

    @RequestMapping(value = "/objects/delete", method = RequestMethod.GET)
    public String deleteObject(@RequestParam(value = "id") Long id, RedirectAttributes redirectAttributes) {
        List<String> conflictualQuestionnaires = equipmentService.delete(id);

        if (!conflictualQuestionnaires.isEmpty()) {
            redirectAttributes.addFlashAttribute("conflictualQuestionnaires", conflictualQuestionnaires);
        }

        return REDIRECT_OBJECTS;
    }

    private Equipment getEquipment(@RequestParam("name") String name,
                                   List<Category> categories,
                                   @RequestParam(TYPE) String type,
                                   @RequestParam("definition") String definition,
                                   HttpServletRequest request,
                                   Principal principal,
                                   Long id) {

        List<Illustration> illustrations = new ArrayList<>();
        List<Equipment> subobjects = new ArrayList<>();
        List<String> illustrationIds = null;
        List<String> subobjectIds = null;

        User user = userService.getUserByUsername(principal.getName());

        if (request.getParameterValues(ILLUSTRATION_IDS) != null) {
            illustrationIds = Arrays.asList(request.getParameterValues(ILLUSTRATION_IDS));
        }

        if (illustrationIds != null) {
            illustrations = illustrationService.withIds(GenericUtils.convertToLongList(illustrationIds));
        }

        if (request.getParameterValues(SUBOBJECT_IDS) != null) {
            subobjectIds = Arrays.asList(request.getParameterValues(SUBOBJECT_IDS));
        }

        if (subobjectIds != null) {
            subobjects = equipmentService.withIds(GenericUtils.convertToLongList(subobjectIds));
        }

        Equipment equipment = new Equipment();
        if (id == null) {
            equipment.setReference("");
        } else {
            equipment.setId(id);
            equipment.setReference(equipmentService.withId(id).getReference());
        }
        equipment.setName(name.trim().replaceAll("\\s+", " "));
        equipment.setDefinition(definition.trim().replaceAll("\\s+", " "));
        equipment.setType(type);
        equipment.setUser(user);
        equipment.setCategories(categories);
        equipment.setIllustrations(illustrations);
        if (equipment.getType().equals(SUBOBJECT)) {
            equipment.setSubobjects(new ArrayList<>());
        } else {
            if (request.getParameterValues(SUBOBJECT_IDS) != null) {
                subobjectIds = Arrays.asList(request.getParameterValues(SUBOBJECT_IDS));
            }

            if (subobjectIds != null) {
                subobjects = equipmentService.withIds(GenericUtils.convertToLongList(subobjectIds));
            }
            equipment.setSubobjects(subobjects);
        }
        equipment.setDate(new Date());
        return equipment;
    }

    private void addEquipmentAndUploadErrorMessageToRedirectAttributes(Locale locale,
                                                                       RedirectAttributes redirectAttributes,
                                                                       Equipment equipment,
                                                                       FileService.UploadStatus isUploaded,
                                                                       HttpServletRequest request) {
        addEquipmentToRedirectAttributes(redirectAttributes, equipment, request);
        addUploadErrorMessageToRedirectAttributes(locale, redirectAttributes, isUploaded);
    }

    private void addEquipmentToRedirectAttributes(RedirectAttributes redirectAttributes,
                                                  Equipment equipment, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute(ERR_ADD_EQUIPMENT, "");

        if (request.getParameterValues(ILLUSTRATION_IDS) != null) {
            equipment.setIllustrationIds(Arrays.asList(request.getParameterValues(ILLUSTRATION_IDS)));
        }
        if (request.getParameterValues(SUBOBJECT_IDS) != null) {
            equipment.setSubobjectIds(Arrays.asList(request.getParameterValues(SUBOBJECT_IDS)));
        }
        if (request.getParameterValues(CATEGORY_IDS) != null) {
            equipment.setCategoryIds(Arrays.asList(request.getParameterValues(CATEGORY_IDS)));
        } else {
            equipment.setCategoryIds(new ArrayList<>());
        }
        addAllCategoriesIdToCategoryIds(equipment);

        redirectAttributes.addFlashAttribute(EQUIPMENT, equipment);
    }

    private void addEquipmentPropertiesToRedirectAttributes(Equipment equipment, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ICON, equipment.getIcon());
        redirectAttributes.addFlashAttribute(TYPE, equipment.getType());

        List<Equipment> allSubObjectsList = equipmentService.withType(SUBOBJECT);
        if (equipment.getType().equals(SUBOBJECT)) {
            // remove equipment so it is not proposed if type changes to object
            allSubObjectsList.remove(equipment);
        }

        redirectAttributes.addFlashAttribute(SUBOBJECTS, allSubObjectsList);
    }

    private void addUploadErrorMessageToRedirectAttributes(Locale locale,
                                                           RedirectAttributes redirectAttributes,
                                                           FileService.UploadStatus isUploaded) {

        redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE,
                messageSource.getMessage(isUploaded.getMessage(), null, locale));
    }

    private void addAllCategoriesIdToCategoryIds(@ModelAttribute Equipment equipment) {
        if (!equipment.getCategoryIds().contains(CategoryService.ALL_OBJECTS_ID)) {
            List<String> categoryIds = new ArrayList<>(equipment.getCategoryIds());
            categoryIds.add(0, CategoryService.ALL_OBJECTS_ID);
            equipment.setCategoryIds(categoryIds);
        }
    }

    private List<Category> getCategories(List<String> categoryIds) {
        List<Category> categories = new ArrayList<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            categories = categoryService.withIds(GenericUtils.convertToLongList(categoryIds));
        }

        categories.add(0, categoryService.getCategoryByName(CategoryService.ALL_OBJECTS));

        return categories;
    }
}
