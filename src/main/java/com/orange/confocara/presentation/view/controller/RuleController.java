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

import com.orange.confocara.business.service.*;
import com.orange.confocara.connector.persistence.model.*;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import com.orange.confocara.presentation.view.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RuleController {

    public static final String RULE_LABEL_FILTER_COOKIE = "ruleLabelFilter";
    public static final String SELECTED_RULES_CATEGORY_COOKIE = "selectedRulesCategory";
    public static final String SELECTED_RULES_CATEGORY = "selectedRulesCategoryValue";

    private static final String ID = "id";
    private static final String RULES = "rules";
    private static final String RULE = "rule";
    private static final String USERNAME = "username";
    private static final String ILLUSTRATION = "illustration";
    private static final String ILLUSTRATIONS = "illustrations";
    private static final String ILLUSTRATS = "illustrats";
    private static final String RULES_CATEGORIES = "rulesCategories";

    private static final String REDIRECT_RULES = "redirect:/rules";
    private static final String REDIRECT_ADD_RULES = "redirect:/rules/add";
    private static final String REDIRECT_EDIT_RULES = "redirect:/rules/edit";
    private static final String ADD_RULE = "addRule";
    private static final String EDIT_RULE = "editRule";
    private static final String RULE_EDIT_ID_COOKIE = "ruleEditCookieID";
    private static final String ERR_NO_PERTINENT_IMPACT = "noPertinentImpactError";

    @Autowired
    private UserService userService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private IllustrationService illustrationService;
    @Autowired
    private ProfileTypeService profileTypeService;
    @Autowired
    private RuleImpactService ruleImpactService;
    @Autowired
    private RulesCategoryService rulesCategoryService;

    @GetMapping(value = "/rules/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addRuleHelper";
    }

    @GetMapping(value = "/rules/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editRuleHelper";
    }

    @RequestMapping(value = "/rules/details/{reference}", method = RequestMethod.GET)
    public String showAssociatedQuestionnaires(@PathVariable("reference") String reference, Model model) {
        model.addAttribute("ruleAssociatedQuestionnaireList", ruleService.getAssociatedQuestionnaires(reference));

        return RULES + " :: questionnairesList";
    }

    @GetMapping("/rules/add")
    public String addRule(Principal principal, ModelMap model, @CookieValue(value = SELECTED_RULES_CATEGORY_COOKIE, required = false) String selectedRulesCategory) {
        if (!model.containsAttribute(RULE)) {
            List<ProfileType> allProfileTypes = profileTypeService.all();
            List<RuleImpact> ruleImpacts = ruleImpactService.generateRuleImpactsFromProfileTypes(allProfileTypes);

            Rule rule = new Rule();
            rule.setRuleImpacts(ruleImpacts);
            model.addAttribute(RULE, rule);
        } else if (model.get(RULE) instanceof Rule) {
            Rule ruleFromModel = (Rule) model.get(RULE);
            ruleFromModel.setRuleImpacts(ruleImpactService.generateRuleImpactsFromProfileTypesWithRule(profileTypeService.all(), ruleFromModel));
        }

        Illustration illustration = new Illustration();
        model.addAttribute(ILLUSTRATION, illustration);
        model.addAttribute(ILLUSTRATS, illustrationService.all());
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());

        if (selectedRulesCategory != null) {
            model.addAttribute(SELECTED_RULES_CATEGORY, selectedRulesCategory);
        }

        model.addAttribute(USERNAME, principal.getName());

        return ADD_RULE;
    }

    @RequestMapping(value = "/rules/create", method = RequestMethod.POST, params = "action=create")
    public String createRule(@ModelAttribute Rule rule,
                             Principal principal,
                             RedirectAttributes redirectAttributes,
                             HttpServletResponse response) {

        User user = userService.getUserByUsername(principal.getName());
        RulesCategory rulesCategory = rulesCategoryService.withId(rule.getRulesCategoryId());
        List<Illustration> illustrations = new ArrayList<>();

        Map<Long, ProfileType> profileTypeMap = profileTypeService.getProfileTypeMap(rulesCategory);
        Map<Long, ImpactValue> impactValuesMap = rulesCategoryService.getImpactValuesMap(rulesCategory);

        rule.setUser(user);

        if (rule.getIllustrationIds() != null) {
            illustrations = illustrationService.withIds(GenericUtils.convertToLongList(rule.getIllustrationIds()));
        }

        rule.setIllustrations(illustrations);
        rule.setRulesCategory(rulesCategory);
        rule.setDate(new Date());
        rule.setReference("");

        setImpactsToRule(profileTypeMap, impactValuesMap, rule);

        if (!ruleService.containsPertinentImpact(rule)) {
            redirectAttributes.addFlashAttribute(RULE, rule);
            redirectAttributes.addFlashAttribute(ILLUSTRATIONS, rule.getIllustrations().stream()
                    .map(Illustration::getId)
                    .collect(Collectors.toList()));
            redirectAttributes.addFlashAttribute(ERR_NO_PERTINENT_IMPACT, "");
            return REDIRECT_ADD_RULES;
        }

        ruleService.create(rule);

        CookieUtils.decorateResponse(response, rulesCategory);

        return REDIRECT_RULES;
    }

    @GetMapping("/rules/edit")
    public String editRule(Principal principal, @Param(value = ID) Long id,
                           @CookieValue(value = RULE_EDIT_ID_COOKIE, required = false) String idCookie,
                           ModelMap model, HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(RULE_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        model.addAttribute(ID, idFinal);
        Illustration illustration = new Illustration();
        model.addAttribute(ILLUSTRATION, illustration);
        model.addAttribute(ILLUSTRATS, illustrationService.all());
        model.addAttribute(USERNAME, principal.getName());

        if (!model.containsAttribute(RULE)) {
            Rule rule = ruleService.withId(idFinal);

            model.addAttribute(ILLUSTRATIONS, rule.getIllustrations().stream()
                    .map(Illustration::getId)
                    .collect(Collectors.toList()));

            ruleImpactService.setRulesCategoryToRuleImpacts(rule);

            model.addAttribute(RULE, rule);

        } else if (model.get(RULE) instanceof Rule) {
            Rule ruleFromModel = (Rule) model.get(RULE);
            ruleImpactService.setRulesCategoryToRuleImpacts(ruleFromModel);
        }

        return EDIT_RULE;
    }

    @RequestMapping(value = "/rules/update", method = RequestMethod.POST, params = "action=update")
    public String updateRule(@ModelAttribute Rule rule,
                             @RequestParam Long id,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        User user = userService.getUserByUsername(principal.getName());
        List<Illustration> illustrations = new ArrayList<>();

        if (rule.getIllustrationIds() != null) {
            illustrations = illustrationService.withIds(GenericUtils.convertToLongList(rule.getIllustrationIds()));
        }

        rule.setId(id);
        rule.setIllustrations(illustrations);

        RulesCategory rulesCategory = ruleService.withId(id).getRulesCategory();
        rule.setRulesCategory(rulesCategory);

        rule.setUser(user);
        rule.setDate(new Date());

        Map<Long, ProfileType> profileTypeMap = profileTypeService.getProfileTypeMap(rulesCategory);
        Map<Long, ImpactValue> impactValuesMap = rulesCategoryService.getImpactValuesMap(rulesCategory);
        setImpactsToRule(profileTypeMap, impactValuesMap, rule);

        if (!ruleService.containsPertinentImpact(rule)) {
            redirectAttributes.addFlashAttribute(RULE, rule);
            redirectAttributes.addFlashAttribute(ILLUSTRATIONS, rule.getIllustrations().stream()
                    .map(Illustration::getId)
                    .collect(Collectors.toList()));
            redirectAttributes.addFlashAttribute(ERR_NO_PERTINENT_IMPACT, "");
            redirectAttributes.addAttribute(ID, id);
            return REDIRECT_EDIT_RULES;
        }

        ruleService.update(rule);

        return REDIRECT_RULES;
    }

    @RequestMapping(value = "/rules/delete", method = RequestMethod.GET)
    public String deleteRule(@RequestParam(value = ID) Long id) {
        ruleService.delete(id);

        return REDIRECT_RULES;
    }

    private void setImpactsToRule(Map<Long, ProfileType> profileTypeMap,
                                  Map<Long, ImpactValue> impactValuesMap,
                                  Rule rule) {

        if (rule.getRuleImpacts() != null && !rule.getRuleImpacts().isEmpty()) {
            List<RuleImpact> ruleImpactsToCreate = new ArrayList<>();

            for (RuleImpact impact : rule.getRuleImpacts()) {

                if (profileTypeMap.containsKey(impact.getProfileType().getId())
                        && impact.getRulesCategory().getId().equals(rule.getRulesCategory().getId())) {

                    ImpactValue impactValue = impactValuesMap.get(impact.getImpact().getId());
                    ProfileType profileType = profileTypeMap.get(impact.getProfileType().getId());
                    impact.setProfileType(profileType);
                    impact.setImpact(impactValue);
                    ruleImpactsToCreate.add(impact);
                }
            }

            rule.setRuleImpacts(ruleImpactsToCreate);
        }
    }
}
