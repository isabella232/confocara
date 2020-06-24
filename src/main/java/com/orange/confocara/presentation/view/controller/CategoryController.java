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
import com.orange.confocara.connector.persistence.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Controller
public class CategoryController {

    public static final String CATEGORY_FILTER_COOKIE = "categoryFilter";
    private static final String CATEGORY = "category";
    private static final String CATEGORIES = "categories";
    private static final String USERNAME = "username";
    private static final String ADD_CATEGORY = "addCategory";
    private static final String ID = "id";
    private static final String ERR_ADD_CATEGORY = "err_add_category";
    private static final String EDIT_CATEGORY = "editCategory";
    private static final String REDIRECT_ADMIN_CATEGORIES = "redirect:/admin/categories";
    private static final String REDIRECT_ADMIN_CATEGORIES_EDIT = "redirect:/admin/categories/edit";
    private static final String REDIRECT_ADMIN_CATEGORIES_ADD = "redirect:/admin/categories/add";
    private static final String WARNING_EQUIPMENTS_WITH_ONLY_DEFAULT_CATEGORY = "equipmentsWithOnlyDefaultCategory";
    private static final String CATEGORY_EDIT_ID_COOKIE = "CategoryEditCookie";

    @Autowired
    private CategoryService categoryService;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/categories")
    public String index(Principal principal, Model model) {
        model.addAttribute(CATEGORIES, categoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        return CATEGORIES;
    }

    @GetMapping(value = "/admin/categories/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/categoriesHelper";
    }

    @GetMapping(value = "/admin/categories/add/help")
    public String showAddCategoryHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addCategoryHelper";
    }

    @GetMapping(value = "/admin/categories/edit/help")
    public String showEditCategoryHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editCategoryHelper";
    }

    @GetMapping("/admin/categories/add")
    public String addCategory(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(CATEGORY)) {
            model.addAttribute(CATEGORY, new Category());
        }

        return ADD_CATEGORY;
    }

    @GetMapping("/admin/categories/edit")
    public String editCategory(Principal principal, @Param(value = ID) Long id, @CookieValue(value = CATEGORY_EDIT_ID_COOKIE, required = false) String idCookie, Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(CATEGORY_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        if (categoryService.withId(idFinal).isEditable()) {
            model.addAttribute(USERNAME, principal.getName());

            if (!model.containsAttribute(CATEGORY)) {
                Category category = categoryService.withId(idFinal);
                model.addAttribute(CATEGORY, category);
            }

            model.addAttribute(ID, idFinal);

            return EDIT_CATEGORY;
        } else {
            return REDIRECT_ADMIN_CATEGORIES;
        }
    }

    @RequestMapping(value = "/admin/categories/create", method = RequestMethod.POST)
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (!categoryService.isAvailable(category.getName())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_CATEGORY, "");
            Category invalidCategory = new Category();
            invalidCategory.setName(category.getName());
            redirectAttributes.addFlashAttribute(CATEGORY, invalidCategory);
            return REDIRECT_ADMIN_CATEGORIES_ADD;
        } else {
            category.setName(category.getName().trim().replaceAll("\\s+", " "));
            categoryService.create(category);
            return REDIRECT_ADMIN_CATEGORIES;
        }
    }

    @RequestMapping(value = "/admin/categories/update", method = RequestMethod.POST)
    public String updateCategory(@ModelAttribute Category category, @RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (!(category.getName().trim().replaceAll("\\s+", " ").equalsIgnoreCase(categoryService.withId(id).getName()) || categoryService.isAvailable(category.getName()))) {
            redirectAttributes.addFlashAttribute(ERR_ADD_CATEGORY, "");
            redirectAttributes.addAttribute(ID, id);
            Category invalidCategory = new Category();
            invalidCategory.setName(category.getName());
            redirectAttributes.addFlashAttribute(CATEGORY, invalidCategory);
            return REDIRECT_ADMIN_CATEGORIES_EDIT;
        } else {
            category.setId(id);
            category.setName(category.getName().trim().replaceAll("\\s+", " "));
            categoryService.update(category);

            return REDIRECT_ADMIN_CATEGORIES;
        }
    }

    @RequestMapping(value = "/admin/categories/delete", method = RequestMethod.GET)
    public String deleteCategory(@RequestParam(value = ID) Long id, RedirectAttributes redirectAttributes) {
        if (categoryService.withId(id).isEditable()) {
            List<String> equipmentsWithOnlyDefaultCategory = categoryService.deleteCategoryIfNoWarningRaised(id);

            if (!equipmentsWithOnlyDefaultCategory.isEmpty()) {
                redirectAttributes.addFlashAttribute(ID, id);
                redirectAttributes.addFlashAttribute(WARNING_EQUIPMENTS_WITH_ONLY_DEFAULT_CATEGORY, equipmentsWithOnlyDefaultCategory);
            }
        }

        return REDIRECT_ADMIN_CATEGORIES;
    }

    @RequestMapping(value = "/admin/categories/confirmDelete", method = RequestMethod.POST)
    public String confirmDeleteCategory(@RequestParam(value = ID) Long id) {
        categoryService.deleteCategory(id);

        return REDIRECT_ADMIN_CATEGORIES;
    }
}
