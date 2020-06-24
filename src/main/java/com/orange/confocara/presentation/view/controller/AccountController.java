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
import com.orange.confocara.business.service.UserRoleService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Image;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.model.UserRole;
import com.orange.confocara.connector.persistence.model.utils.Role;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import com.orange.confocara.presentation.view.validator.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

@Controller
public class AccountController {

    public static final String ACCOUNT_FILTER_COOKIE = "accountFilter";
    private static final String ACCOUNT_EDIT_ID_COOKIE = "idAccountCookie";

    private static final String USERNAME = "username";
    private static final String ID = "id";

    private static final String USERS = "users";
    private static final String USER = "user";
    private static final String IS_SUPER_ADMIN = "isSuperAdmin";
    private static final String ROLES_IDS = "rolesIds";
    private static final String ROLES = "roles";

    private static final String REDIRECT_ADMIN_ACCOUNTS_ADD = "redirect:/admin/accounts/add";
    private static final String REDIRECT_ADMIN_ACCOUNTS = "redirect:/admin/accounts";
    public static final String REDIRECT_ADMIN_ACCOUNTS_EDIT = "redirect:/admin/accounts/edit";

    private static final String ADD_USER_ERROR_KEY = "err_add_user";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ImageService imageService;

    @Autowired
    AccountValidator validator;
    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/accounts")
    public String index(Principal principal, Model model) {
        model.addAttribute(USERS, userService.all());
        model.addAttribute(USERNAME, principal.getName());
        model.addAttribute(IS_SUPER_ADMIN, isSuperAdmin(principal));
        return "accounts";
    }

    @GetMapping(value = "/admin/accounts/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/accountsHelper";
    }

    @GetMapping(value = "/admin/accounts/add/help")
    public String showAddAccountHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addAccountHelper";
    }

    @GetMapping(value = "/admin/accounts/edit/help")
    public String showEditAccountHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editAccountHelper";
    }

    @GetMapping("/admin/accounts/add")
    public String addUser(Principal principal, Model model) {
        if (!model.containsAttribute(USER)) {
            User user = new User();
            ArrayList<String> defaultRole = new ArrayList<>();
            UserRole userRole = userRoleService.findByRole(Role.ROLE_USER.toString());
            defaultRole.add(userRole.getId().toString());
            user.setRolesIds(defaultRole);
            model.addAttribute(USER, user);
        }

        // only the superadmin can create other admin
        addManagedRolesToModel(principal, model);

        model.addAttribute(USERNAME, principal.getName());
        return "addAccount";
    }

    @RequestMapping(value = "/admin/accounts/create", method = RequestMethod.POST)
    public String createUser(@ModelAttribute User user,
                             RedirectAttributes redirectAttributes,
                             BindingResult result, HttpServletRequest request, Locale locale) {
        //Validation code
        validator.validate(user, result);

        //Check validation errors
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(USER, user);
            addErrorsToRedirectAttributes(locale, result, redirectAttributes);

            return REDIRECT_ADMIN_ACCOUNTS_ADD;
        }

        setUser(user, request);

        if (!result.hasFieldErrors(AccountValidator.IMAGE_DATA) && !user.getImageData().isEmpty()) {
            Image imageDbWithSameName = imageService.findByImageName(user.getImageData().getOriginalFilename());

            if (imageDbWithSameName != null) {
                redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(FileService.UploadStatus.ALREADY_EXISTS.getMessage(), null, locale));
                redirectAttributes.addFlashAttribute(ADD_USER_ERROR_KEY, "");
                redirectAttributes.addFlashAttribute(USER, user);
                return REDIRECT_ADMIN_ACCOUNTS_ADD;
            } else {
                User userWithIcon = userService.createUserWithIconInTx(user, user.getImageData());

                if (userWithIcon == null) {
                    redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(FileService.UploadStatus.NOK.getMessage(), null, locale));
                    redirectAttributes.addFlashAttribute(ADD_USER_ERROR_KEY, "");
                    redirectAttributes.addFlashAttribute(USER, user);
                    return REDIRECT_ADMIN_ACCOUNTS_ADD;
                }
            }
        } else {
            userService.create(user);
        }

        return REDIRECT_ADMIN_ACCOUNTS;
    }

    @GetMapping("/admin/accounts/edit")
    public String editUser(Principal principal, @Param(value = ID) Long id,
                           @CookieValue(value = ACCOUNT_EDIT_ID_COOKIE, required = false)
                                   String idCookie, Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(ACCOUNT_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        if (hasAdminRole(idFinal)) {
            return editAdminAccount(principal, idFinal, model);
        } else {
            return editAccount(principal, idFinal, model);
        }
    }

    @RequestMapping(value = "/admin/accounts/update", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute User user,
                             @RequestParam Long id,
                             Locale locale,
                             BindingResult result,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {

        // Validation code
        validator.validateForUpdate(user, result);

        User userBeforeUpdate = userService.withId(id);
        Image oldImage = userBeforeUpdate.getImage();
        user.setId(id);

        boolean encodePassword = !user.getPasswordHash().isEmpty();

        if (user.getPasswordHash().isEmpty()) {
            // keep old password if unchanged
            user.setPasswordHash(userBeforeUpdate.getPasswordHash());
            user.setConfirmPasswordHash(userBeforeUpdate.getPasswordHash());
        }

        if (result.hasErrors()) {
            addRedirectedUser(user, id, request, redirectAttributes);
            addErrorsToRedirectAttributes(locale, result, redirectAttributes);
            return REDIRECT_ADMIN_ACCOUNTS_EDIT;
        }

        setUser(user, request);

        if (!result.hasFieldErrors(AccountValidator.IMAGE_DATA) && !user.getImageData().isEmpty()) {
            Image imageDbWithSameName = imageService.findByImageName(user.getImageData().getOriginalFilename());
            if (imageDbWithSameName != null) {
                redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(FileService.UploadStatus.ALREADY_EXISTS.getMessage(), null, locale));
                addRedirectedUser(user, id, request, redirectAttributes);
                return REDIRECT_ADMIN_ACCOUNTS_EDIT;
            } else {
                User savedUser = userService.updateUserWithIconInTx(user, user.getImageData(), oldImage, encodePassword);

                if (savedUser == null) {
                    redirectAttributes.addFlashAttribute(FileService.ERR_UPLOAD_MESSAGE, messageSource.getMessage(FileService.UploadStatus.NOK.getMessage(), null, locale));
                    addRedirectedUser(user, id, request, redirectAttributes);
                    return REDIRECT_ADMIN_ACCOUNTS_EDIT;
                }
            }
        } else {
            userService.updateUserWithIconInTx(user, null, oldImage, encodePassword);
        }

        return REDIRECT_ADMIN_ACCOUNTS;
    }

    @RequestMapping(value = "/admin/accounts/delete", method = RequestMethod.GET)
    public String deleteUser(@RequestParam(value = ID) Long id, Principal principal) {
        if (!hasAdminRole(id) || isSuperAdmin(principal)) {
            userService.delete(id);
        }

        return REDIRECT_ADMIN_ACCOUNTS;
    }

    private void addErrorsToRedirectAttributes(Locale locale, BindingResult result, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ADD_USER_ERROR_KEY, "");
        if (result.hasFieldErrors(AccountValidator.CONFIRM_PASS_HASH)) {
            redirectAttributes.addFlashAttribute("passwordDoNotMatch", "");
        }
        if (result.hasFieldErrors(AccountValidator.USERNAME)) {
            redirectAttributes.addFlashAttribute("usernameValidationError", messageSource.getMessage(result.getFieldError(AccountValidator.USERNAME).getCode(), null, locale));
        }
        if (result.hasFieldErrors(AccountValidator.EMAIL)) {
            redirectAttributes.addFlashAttribute("mailValidationError", messageSource.getMessage(result.getFieldError(AccountValidator.EMAIL).getCode(), null, locale));
        }
        if (result.hasFieldErrors(AccountValidator.PASS_HASH)) {
            redirectAttributes.addFlashAttribute("passwordValidationError", "");
        }
        if (result.hasFieldErrors(AccountValidator.PHONE)) {
            redirectAttributes.addFlashAttribute("phoneValidationError", "");
        }
        if (result.hasFieldErrors(AccountValidator.IMAGE_DATA)) {
            redirectAttributes.addFlashAttribute("iconValidationError", "");
        }
        if (result.hasFieldErrors(AccountValidator.FIRSTNAME_LASTNAME)) {
            redirectAttributes.addFlashAttribute("firstNameOrLastNameValidationError", "");
        }
    }

    private void addRoleToUser(HttpServletRequest request, User user) {
        List<String> rolesIds = null;
        if (request.getParameterValues(ROLES_IDS) != null) {
            rolesIds = Arrays.asList(request.getParameterValues(ROLES_IDS));
        }
        if (rolesIds != null) {
            Set<UserRole> userRoles = userRoleService.withIds(GenericUtils.convertToLongList(rolesIds));
            user.setUserRoles(userRoles);
        }
    }

    private void setRolesIdsToUser(User user) {
        ArrayList<String> defaultRole = new ArrayList<>();
        for (UserRole role : user.getUserRoles()) {
            defaultRole.add(role.getId().toString());
        }
        user.setRolesIds(defaultRole);
    }

    private void addManagedRolesToModel(Principal principal, Model model) {
        if (isSuperAdmin(principal)) {
            UserRole superAdminRole = userRoleService.findByRole(Role.ROLE_SUPERADMIN.toString());
            model.addAttribute(ROLES, userRoleService.findManagedRolesWithUserRole(superAdminRole.getRole()));
        } else {
            User sessionUser = userService.getUserByUsername(principal.getName());
            UserRole adminRole = userRoleService.findByRole(Role.ROLE_ADMIN.toString());
            if (sessionUser.getUserRoles().contains(adminRole)) {
                model.addAttribute(ROLES, userRoleService.findManagedRolesWithUserRole(adminRole.getRole()));
            }
        }
    }

    private boolean isSuperAdmin(Principal principal) {
        UserRole superAdminRole = userRoleService.findByRole(Role.ROLE_SUPERADMIN.toString());
        User userFromDb = userService.getUserByUsername(principal.getName());

        if (userFromDb == null) {
            return false;
        }

        return userFromDb.getUserRoles().contains(superAdminRole);
    }

    private boolean hasAdminRole(@RequestParam Long id) {
        UserRole adminRole = userRoleService.findByRole(Role.ROLE_ADMIN.toString());
        return userService.withId(id).getUserRoles().contains(adminRole);
    }

    private String editAdminAccount(Principal principal, @RequestParam Long id, Model model) {
        if (isSuperAdmin(principal)) {
            return editAccount(principal, id, model);
        } else {
            return index(principal, model);
        }
    }

    private String editAccount(Principal principal, @RequestParam Long id, Model model) {
        // user is persisted when an error occurs
        if (!model.containsAttribute(USER)) {
            User user = userService.withId(id);
            setRolesIdsToUser(user);
            model.addAttribute(USER, user);
        }

        addManagedRolesToModel(principal, model);

        model.addAttribute(USERNAME, principal.getName());
        model.addAttribute(ID, id);

        return "editAccount";
    }

    private void addRedirectedUser(@ModelAttribute User user, @RequestParam Long id,
                                   HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ADD_USER_ERROR_KEY, "");
        redirectAttributes.addAttribute(ID, id);
        addRoleToUser(request, user);
        redirectAttributes.addFlashAttribute(USER, user);
    }

    private void setUser(@ModelAttribute User user, HttpServletRequest request) {
        user.setUsername(user.getUsername().trim().replaceAll("\\s+", " "));
        user.setPasswordHash(user.getPasswordHash());
        user.setName(user.getName().trim().replaceAll("\\s+", " "));
        user.setFirstname(user.getFirstname().trim().replaceAll("\\s+", " "));
        user.setEmail(user.getEmail().trim().replaceAll("\\s+", " "));
        user.setFunction(user.getFunction().trim().replaceAll("\\s+", " "));
        user.setTel(user.getTel().trim().replaceAll("\\s+", " "));
        addRoleToUser(request, user);
    }
}
