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
import com.orange.confocara.business.service.ProfileTypeService;
import com.orange.confocara.business.service.RuleService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RulesCategoryController {

    public static final String RULES_CATEGORY_FILTER_COOKIE = "rulesCategoryFilter";
    private static final String RULES_CATEGORY = "rulesCategory";
    private static final String RULES_CATEGORY_LIST = "rulesCategoryList";
    private static final String ALL_PROFILE_TYPE_LIST = "allProfileTypeList";
    private static final String ID = "id";
    private static final String USERNAME = "username";

    private static final String CONFLICTUAL_RULES = "conflictualRules";
    private static final String CONFLICTUAL_QUESTIONS = "conflictualQuestions";
    private static final String CONFLICTUAL_CHAINS = "conflictualChains";
    private static final String CONFLICTUAL_QUESTIONNAIRES = "conflictualQuestionnaires";
    private static final String CONFLICTUAL_RULESETS = "conflictualRulesets";
    private static final String CONFLICTUAL_RULES_LIST = "conflictualRulesList";

    private static final String IMPACT_IDS = "acceptedImpactIds";
    private static final String PROFILE_TYPES_IDS = "profileTypeIds";
    private static final String DEFAULT_IMPACT_ID = "defaultImpactId";
    private static final String ALL_IMPACT_LIST = "allImpactList";

    private static final String ADD_RULES_CATEGORY = "addRulesCategory";
    private static final String REDIRECT_ADMIN_RULES_CATEGORIES_ADD = "redirect:/admin/rules-categories/add";
    private static final String REDIRECT_ADMIN_RULES_CATEGORIES = "redirect:/admin/rules-categories";
    private static final String REDIRECT_ADMIN_RULES_CATEGORIES_EDIT = "redirect:/admin/rules-categories/edit";

    private static final String ERR_ADD_RULES_CATEGORY = "err_add_rules_category";
    private static final String ERR_ADD_RULES_CATEGORY_NAME = "err_add_rules_category_name";
    private static final String ERR_ADD_RULES_CATEGORY_IMPACTS = "err_add_rules_category_impacts";
    private static final String ERR_EMPTY_IMPACT_LIST = "err_empty_impact_list";
    private static final String ERR_ADD_RULES_CATEGORY_DEFAULT_IMPACTS = "err_add_rules_category_default_impacts";
    private static final String ERR_CONFLICTUAL_RULES_ON_REMOVE_IMPACT = "err_conflictual_rules_on_remove_impact";
    private static final String ERR_CONFLICTUAL_RULES_ON_REMOVE_PROFILE_TYPE = "err_conflictual_rules_on_remove_profile_type";
    private static final String ERR_EMPTY_PROFILE_TYPE_LIST = "err_empty_profile_type_list";
    private static final String RULES_CATEGORY_EDIT_ID_COOKIE = "ruleCatEditCookieId";
    private static final String EDIT_RULES_CATEGORY = "editRulesCategory";

    @Autowired
    private RulesCategoryService rulesCategoryService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ImpactValueService impactValueService;
    @Autowired
    private ProfileTypeService profileTypeService;

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @GetMapping("/admin/rules-categories")
    public String index(Principal principal, Model model) {
        model.addAttribute(RULES_CATEGORY_LIST, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        return "rulesCategories";
    }

    @GetMapping(value = "/admin/rules-categories/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/rulesCategoriesHelper";
    }

    @GetMapping(value = "/admin/rules-categories/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addRulesCategoryHelper";
    }

    @GetMapping(value = "/admin/rules-categories/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editRulesCategoryHelper";
    }

    @GetMapping("/admin/rules-categories/add")
    public String addRulesCategory(Principal principal, Model model) {
        RulesCategory rulesCategory = new RulesCategory();
        List<ImpactValue> allImpacts = impactValueService.all();
        List<ProfileType> allProfileTypes = profileTypeService.all();
        List<String> defaultImpactIdsToAdd = new ArrayList<>();

        if (!model.containsAttribute(RULES_CATEGORY) && !allImpacts.isEmpty()) {
            ImpactValue noImpact = impactValueService.findByName(ImpactValueService.NO_IMPACT);
            if (noImpact != null) {
                rulesCategory.setDefaultImpactId(Long.toString(noImpact.getId()));
                defaultImpactIdsToAdd.add(String.valueOf(noImpact.getId()));
            }
            ImpactValue embarrassingImpact = impactValueService.findByName(ImpactValueService.EMBARRASSING);
            if (embarrassingImpact != null) {
                defaultImpactIdsToAdd.add(String.valueOf(embarrassingImpact.getId()));
            }
            ImpactValue blockingImpact = impactValueService.findByName(ImpactValueService.BLOCKING);
            if (blockingImpact != null) {
                defaultImpactIdsToAdd.add(String.valueOf(blockingImpact.getId()));
            }
            rulesCategory.setAcceptedImpactIds(defaultImpactIdsToAdd);
            model.addAttribute(RULES_CATEGORY, rulesCategory);
        }

        model.addAttribute(USERNAME, principal.getName());
        model.addAttribute(ALL_IMPACT_LIST, allImpacts);
        model.addAttribute(ALL_PROFILE_TYPE_LIST, allProfileTypes);

        return ADD_RULES_CATEGORY;
    }

    @RequestMapping(value = "/admin/rules-categories/create", method = RequestMethod.POST)
    public String createRulesCategory(@ModelAttribute RulesCategory rulesCategory,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        boolean isDataValid = rulesCategoryValidation(rulesCategory, request, redirectAttributes, true);

        if (!isDataValid) {
            addNoImpactIdToRulesCategoryImpactIds(rulesCategory);
            redirectAttributes.addFlashAttribute(RULES_CATEGORY, rulesCategory);
            redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY, "");
            return REDIRECT_ADMIN_RULES_CATEGORIES_ADD;
        }

        List<String> impactValuesIds = getImpactValuesIds(request);
        List<ImpactValue> impactValueList = impactValueService.withIds(GenericUtils.convertToLongList(impactValuesIds));

        rulesCategory.setAcceptedImpactIds(impactValuesIds);
        rulesCategory.setImpactValues(impactValueList);

        String defaultImpactValueId = request.getParameter(DEFAULT_IMPACT_ID);
        Long defaultImpactId = Long.parseLong(defaultImpactValueId);
        rulesCategory.setDefaultImpact(impactValueService.withId(defaultImpactId));

        List<ProfileType> profileTypeList = initProfileTypeList(request);

        rulesCategory.setProfileTypes(profileTypeList);

        rulesCategory.setName(rulesCategory.getName().trim().replaceAll("\\s+", " "));

        rulesCategoryService.create(rulesCategory);
        profileTypeService.updateRulesCategoriesOnUpdateRulesCategoryProfileTypes(rulesCategory);

        return REDIRECT_ADMIN_RULES_CATEGORIES;
    }

    @GetMapping("/admin/rules-categories/edit")
    public String editRulesCategory(Principal principal, @Param(value = ID) Long id,
                                    @CookieValue(value = RULES_CATEGORY_EDIT_ID_COOKIE, required = false) String idCookie,
                                    Model model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(RULES_CATEGORY_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);
        model.addAttribute(USERNAME, principal.getName());

        model.addAttribute(ID, idFinal);

        if (!model.containsAttribute(RULES_CATEGORY)) {
            RulesCategory rulesCategory = rulesCategoryService.withId(idFinal);

            List<String> ids = new ArrayList<>();
            for (ImpactValue impact : rulesCategory.getImpactValues()) {
                ids.add(String.valueOf(impact.getId()));
            }

            List<String> profileTypeIds = new ArrayList<>();
            for (ProfileType profileType : rulesCategory.getProfileTypes()) {
                profileTypeIds.add(String.valueOf(profileType.getId()));
            }

            rulesCategory.setAcceptedImpactIds(ids);
            rulesCategory.setProfileTypeIds(profileTypeIds);
            rulesCategory.setDefaultImpactId(Long.toString(rulesCategory.getDefaultImpact().getId()));
            model.addAttribute(RULES_CATEGORY, rulesCategory);
        }
        model.addAttribute(ALL_IMPACT_LIST, impactValueService.all());
        List<ProfileType> allProfileTypes = profileTypeService.all();
        model.addAttribute(ALL_PROFILE_TYPE_LIST, allProfileTypes);

        return EDIT_RULES_CATEGORY;
    }

    @RequestMapping(value = "/admin/rules-categories/update", method = RequestMethod.POST)
    public String updateRulesCategory(@ModelAttribute RulesCategory rulesCategory,
                                      @RequestParam Long id,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {

        boolean isDataValid = rulesCategoryValidation(rulesCategory, request, redirectAttributes, false);
        rulesCategory.setId(id);

        List<String> impactValuesIds = new ArrayList<>();
        List<ImpactValue> impactValueList = new ArrayList<>();

        if (request.getParameterValues(IMPACT_IDS) != null) {
            impactValuesIds = getImpactValuesIds(request);
            impactValueList = impactValueService.withIds(GenericUtils.convertToLongList(impactValuesIds));
        }

        rulesCategory.setAcceptedImpactIds(impactValuesIds);
        rulesCategory.setImpactValues(impactValueList);

        if (!isDataValid) {
            addNoImpactIdToRulesCategoryImpactIds(rulesCategory);

            redirectAttributes.addFlashAttribute(RULES_CATEGORY, rulesCategory);
            redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY, "");
            redirectAttributes.addAttribute(ID, id);

            return REDIRECT_ADMIN_RULES_CATEGORIES_EDIT;
        } else {
            initRulesCategoryDefaultImpact(rulesCategory, request);
            List<ProfileType> profileTypeList = initProfileTypeList(request);

            List<Rule> conflictualRulesWithAffectedRemovedImpactValue = ruleService.getRulesWithAffectedRemovedImpactValue(rulesCategoryService.withId(id), impactValueList, profileTypeList);
            if (conflictualRulesWithAffectedRemovedImpactValue.isEmpty()) {
                List<Rule> conflictualRulesWithRemovedProfileType = ruleService.getRulesWithRemovedProfileType(rulesCategoryService.withId(id), profileTypeList);
                if (conflictualRulesWithRemovedProfileType.isEmpty()) {
                    processUpdateRulesCategory(rulesCategory, id, profileTypeList);

                    return REDIRECT_ADMIN_RULES_CATEGORIES;
                } else {
                    processConflictualRulesAttributes(rulesCategory, id, redirectAttributes, conflictualRulesWithRemovedProfileType);
                    redirectAttributes.addFlashAttribute(ERR_CONFLICTUAL_RULES_ON_REMOVE_PROFILE_TYPE, "");

                    return REDIRECT_ADMIN_RULES_CATEGORIES_EDIT;
                }
            } else {
                processConflictualRulesAttributes(rulesCategory, id, redirectAttributes, conflictualRulesWithAffectedRemovedImpactValue);
                redirectAttributes.addFlashAttribute(ERR_CONFLICTUAL_RULES_ON_REMOVE_IMPACT, "");

                return REDIRECT_ADMIN_RULES_CATEGORIES_EDIT;
            }
        }
    }

    @RequestMapping(value = "/admin/rules-categories/delete", method = RequestMethod.GET)
    public String deleteRulesCategory(@RequestParam(value = ID) Long id, RedirectAttributes redirectAttributes) {
        RulesCategory rulesCategory = rulesCategoryService.withId(id);

        List<String> conflictualRules = rulesCategoryService.getRulesLabelWithRulesCategory(rulesCategory);

        if (!conflictualRules.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_RULES, conflictualRules);
            return REDIRECT_ADMIN_RULES_CATEGORIES;
        }

        List<String> conflictualQuestions = rulesCategoryService.getQuestionsNameWithRulesCategory(rulesCategory);

        if (!conflictualQuestions.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_QUESTIONS, conflictualQuestions);
            return REDIRECT_ADMIN_RULES_CATEGORIES;
        }

        List<String> conflictualChains = rulesCategoryService.getChainsNameWithRulesCategory(rulesCategory);

        if (!conflictualChains.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_CHAINS, conflictualChains);
            return REDIRECT_ADMIN_RULES_CATEGORIES;
        }

        List<String> conflictualQuestionnaires = rulesCategoryService.getQuestionnairesObjectNameWithRulesCategory(rulesCategory);

        if (!conflictualQuestionnaires.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_QUESTIONNAIRES, conflictualQuestionnaires);
            return REDIRECT_ADMIN_RULES_CATEGORIES;
        }

        List<String> conflictualRulesets = rulesCategoryService.getRulesetsNameWithRulesCategory(rulesCategory);

        if (!conflictualRulesets.isEmpty()) {
            redirectAttributes.addFlashAttribute(CONFLICTUAL_RULESETS, conflictualRulesets);
            return REDIRECT_ADMIN_RULES_CATEGORIES;
        }

        rulesCategoryService.delete(id);

        return REDIRECT_ADMIN_RULES_CATEGORIES;
    }

    private boolean rulesCategoryValidation(@ModelAttribute RulesCategory rulesCategory,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes,
                                            boolean creation) {
        boolean isDataValid = true;
        List<String> impactValuesIds = new ArrayList<>();

        if (creation) {
            if (!rulesCategoryService.isAvailable(rulesCategory.getName())) {
                isDataValid = false;
                redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY_NAME, "");
            }
        } else {
            if (!rulesCategoryService.isAvailable(rulesCategory.getName()) &&
                    !rulesCategory.getName().trim().replaceAll("\\s+", " ").equalsIgnoreCase(rulesCategoryService.withId(rulesCategory.getId()).getName())) {
                isDataValid = false;
                redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY_NAME, "");
            }
        }

        String defaultImpactValueId = request.getParameter(DEFAULT_IMPACT_ID);
        if (defaultImpactValueId == null) {
            isDataValid = false;
            redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY_IMPACTS, "");
        }

        if (request.getParameterValues(IMPACT_IDS) != null) {
            impactValuesIds = getImpactValuesIds(request);
        } else {
            isDataValid = false;
            redirectAttributes.addFlashAttribute(ERR_EMPTY_IMPACT_LIST, "");
        }

        if (!impactValuesIds.contains(defaultImpactValueId)) {
            isDataValid = false;
            redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY_DEFAULT_IMPACTS, "");
        } else if (impactValuesIds.size() == 1) {
            isDataValid = false;
            redirectAttributes.addFlashAttribute(ERR_EMPTY_IMPACT_LIST, "");
        }

        if (request.getParameterValues(PROFILE_TYPES_IDS) == null
                || Arrays.asList(request.getParameterValues(PROFILE_TYPES_IDS)).isEmpty()) {
            isDataValid = false;
            redirectAttributes.addFlashAttribute(ERR_EMPTY_PROFILE_TYPE_LIST, "");
        }

        return isDataValid;
    }

    private List<ProfileType> initProfileTypeList(HttpServletRequest request) {
        List<String> profileTypeIds = new ArrayList<>();
        List<ProfileType> profileTypes = new ArrayList<>();
        if (request.getParameterValues(PROFILE_TYPES_IDS) != null) {
            profileTypeIds = Arrays.asList(request.getParameterValues(PROFILE_TYPES_IDS));
        }

        if (!profileTypeIds.isEmpty()) {
            profileTypes = profileTypeService.withIds(GenericUtils.convertToLongList(profileTypeIds));
        }

        return profileTypes;
    }

    private void initRulesCategoryDefaultImpact(@ModelAttribute RulesCategory rulesCategory, HttpServletRequest request) {
        String defaultImpactValueId = request.getParameter(DEFAULT_IMPACT_ID);
        Long defaultImpactId = Long.parseLong(defaultImpactValueId);
        ImpactValue defaultImpact = impactValueService.withId(defaultImpactId);
        rulesCategory.setDefaultImpact(defaultImpact);
        rulesCategory.setDefaultImpactId(defaultImpactValueId);
    }

    private void processConflictualRulesAttributes(@ModelAttribute RulesCategory rulesCategory,
                                                   @RequestParam Long id,
                                                   RedirectAttributes redirectAttributes,
                                                   List<Rule> conflictualRules) {

        redirectAttributes.addFlashAttribute(RULES_CATEGORY, rulesCategory);
        redirectAttributes.addFlashAttribute(CONFLICTUAL_RULES_LIST, conflictualRules.stream().map(Rule::getLabel).collect(Collectors.toList()));
        redirectAttributes.addFlashAttribute(ERR_ADD_RULES_CATEGORY, "");
        redirectAttributes.addAttribute(ID, id);
    }

    private void processUpdateRulesCategory(@ModelAttribute RulesCategory rulesCategory, @RequestParam Long id, List<ProfileType> profileTypeList) {
        profileTypeService.removeRulesCategoryOnEditRulesCategory(rulesCategoryService.withId(id).getProfileTypes(), profileTypeList, id);
        rulesCategory.setProfileTypes(profileTypeList);
        rulesCategory.setName(rulesCategory.getName().trim().replaceAll("\\s+", " "));

        rulesCategoryService.update(rulesCategory);
        profileTypeService.updateRulesCategoriesOnUpdateRulesCategoryProfileTypes(rulesCategory);
    }

    private List<String> getImpactValuesIds(HttpServletRequest request) {
        List<String> impactValuesIds = new ArrayList<>(Arrays.asList(request.getParameterValues(IMPACT_IDS)));
        impactValuesIds.add(0, ImpactValueService.NO_IMPACT_ID);
        return impactValuesIds;
    }

    private void addNoImpactIdToRulesCategoryImpactIds(@ModelAttribute RulesCategory rulesCategory) {
        if (!rulesCategory.getAcceptedImpactIds().contains(ImpactValueService.NO_IMPACT_ID)) {
            rulesCategory.getAcceptedImpactIds().add(ImpactValueService.NO_IMPACT_ID);
        }
    }
}
