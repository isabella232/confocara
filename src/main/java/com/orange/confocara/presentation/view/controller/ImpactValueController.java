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

import com.orange.confocara.business.service.ImpactValueService;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.presentation.view.controller.helper.ImpactValueReplacementError;
import com.orange.confocara.presentation.view.controller.helper.ImpactValueReplacementHelper;
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

@Controller
public class ImpactValueController {

    public static final String IMPACT_FILTER_COOKIE = "impactFilter";
    private static final String IMPACT = "impact";
    private static final String IMPACTS = "impacts";
    private static final String IMPACTS_URL = "impactValues";
    private static final String USERNAME = "username";
    private static final String ID = "id";

    private static final String IMPACT_TO_DELETE = "impactToDelete";
    private static final String REPLACEMENT_IMPACT_VALUE = "replacementImpactValue";
    private static final String REPLACE_IMPACT_VALUE_RC_ERROR = "replaceImpactValueRulesCategoryError";
    private static final String REPLACE_IMPACT_VALUE_RULE_ERROR = "replaceImpactValueRuleError";
    private static final String IMPACT_ID = "impactId";

    private static final String ERR_ADD_IMPACT = "err_add_impact";
    private static final String AT_LEAST_ONE_IMPACT_REQUIRED = "oneImpactIsRequired";

    private static final String ADD_IMPACT = "addImpactValue";
    private static final String EDIT_IMPACT = "editImpactValue";

    private static final String REDIRECT_ADMIN_IMPACTS = "redirect:/admin/impacts";
    private static final String REDIRECT_ADMIN_IMPACTS_EDIT = "redirect:/admin/impacts/edit";
    private static final String REDIRECT_ADMIN_IMPACTS_ADD = "redirect:/admin/impacts/add";
    private static final String IMPACT_EDIT_ID_COOKIE = "impactEditCookieId";

    @Autowired
    private ImpactValueService impactValueService;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/impacts")
    public String index(Principal principal, Model model) {
        model.addAttribute(IMPACTS, impactValueService.all());
        model.addAttribute(USERNAME, principal.getName());

        return IMPACTS_URL;
    }

    @GetMapping(value = "/admin/impacts/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/impactValuesHelper";
    }

    @GetMapping(value = "/admin/impacts/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addImpactValueHelper";
    }

    @GetMapping(value = "/admin/impacts/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editImpactValueHelper";
    }

    @GetMapping("/admin/impacts/add")
    public String addImpactValue(Principal principal, Model model) {
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(IMPACT)) {
            model.addAttribute(IMPACT, new ImpactValue());
        }

        return ADD_IMPACT;
    }

    @GetMapping("/admin/impacts/edit")
    public String editImpactValue(Principal principal, @Param(value = ID) Long id, @CookieValue(value = IMPACT_EDIT_ID_COOKIE, required = false) String idCookie, Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(IMPACT_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        if (impactValueService.withId(idFinal).isEditable()) {
            model.addAttribute(USERNAME, principal.getName());

            if (!model.containsAttribute(IMPACT)) {
                ImpactValue impactValue = impactValueService.withId(idFinal);
                model.addAttribute(IMPACT, impactValue);
            }

            model.addAttribute(ID, idFinal);

            return EDIT_IMPACT;
        } else {
            return REDIRECT_ADMIN_IMPACTS;
        }
    }

    @RequestMapping(value = "/admin/impacts/create", method = RequestMethod.POST)
    public String createImpactValue(@ModelAttribute ImpactValue impactValue, RedirectAttributes redirectAttributes) {
        if (!impactValueService.isAvailable(impactValue.getName())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_IMPACT, "");
            ImpactValue invalidImpact = new ImpactValue();
            invalidImpact.setName(impactValue.getName());
            redirectAttributes.addFlashAttribute(IMPACT, invalidImpact);

            return REDIRECT_ADMIN_IMPACTS_ADD;
        } else {
            impactValue.setEditable(true);
            impactValue.setName(impactValue.getName().trim().replaceAll("\\s+", " "));
            impactValueService.create(impactValue);

            return REDIRECT_ADMIN_IMPACTS;
        }
    }

    @RequestMapping(value = "/admin/impacts/update", method = RequestMethod.POST)
    public String updateImpactValue(@ModelAttribute ImpactValue impactValue, @RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (!(impactValue.getName().trim().replaceAll("\\s+", " ").equalsIgnoreCase(impactValueService.withId(id).getName()) || impactValueService.isAvailable(impactValue.getName()))) {
            redirectAttributes.addFlashAttribute(ERR_ADD_IMPACT, "");
            redirectAttributes.addAttribute(ID, id);
            ImpactValue invalidImpact = new ImpactValue();
            invalidImpact.setName(impactValue.getName());
            invalidImpact.setEditable(impactValue.isEditable());
            redirectAttributes.addFlashAttribute(IMPACT, invalidImpact);

            return REDIRECT_ADMIN_IMPACTS_EDIT;
        } else {
            impactValue.setId(id);
            impactValue.setName(impactValue.getName().trim().replaceAll("\\s+", " "));
            impactValueService.update(impactValue);

            return REDIRECT_ADMIN_IMPACTS;
        }
    }

    @RequestMapping(value = "/admin/impacts/delete", method = RequestMethod.GET)
    public String deleteImpactValue(@RequestParam(value = ID) Long id, RedirectAttributes redirectAttributes) {
        if (impactValueService.all().size() == 1) {
            redirectAttributes.addFlashAttribute(AT_LEAST_ONE_IMPACT_REQUIRED, "");
        } else if (impactValueService.withId(id).isEditable()) {
            boolean isDeleted = impactValueService.delete(id);

            if (!isDeleted) {
                redirectAttributes.addFlashAttribute(IMPACT_TO_DELETE, impactValueService.withId(id));
                redirectAttributes.addFlashAttribute(REPLACEMENT_IMPACT_VALUE, new ImpactValue());
            }
        }

        return REDIRECT_ADMIN_IMPACTS;
    }

    @RequestMapping(value = "/admin/impacts/replace", method = RequestMethod.POST)
    public String replaceImpactValueOnDelete(@RequestParam(value = ID) Long id,
                                             @RequestParam(value = IMPACT_ID) Long impactToDeleteId,
                                             RedirectAttributes redirectAttributes) {
        ImpactValueReplacementHelper impactValueReplacementHelper = impactValueService.replaceImpactInRulesCategoryOnDelete(id, impactToDeleteId);

        if (impactValueReplacementHelper != null) {
            if (impactValueReplacementHelper.getErrorType().equals(ImpactValueReplacementError.RULES_CATEGORY)) {
                redirectAttributes.addFlashAttribute(REPLACE_IMPACT_VALUE_RC_ERROR, impactValueReplacementHelper.getValue());
            } else if (impactValueReplacementHelper.getErrorType().equals(ImpactValueReplacementError.RULE)) {
                redirectAttributes.addFlashAttribute(REPLACE_IMPACT_VALUE_RULE_ERROR, impactValueReplacementHelper.getValue());
            }
        }

        return REDIRECT_ADMIN_IMPACTS;
    }
}
