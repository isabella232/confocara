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

import com.orange.confocara.business.service.QuestionnaireObjectService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.RulesetService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.dto.RulesetQuestionnaireDto;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import com.orange.confocara.presentation.view.util.CookieUtils;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller that manages relationship between the View and Services
 */
@Slf4j
@Controller
public class RulesetController {

    public static final String RULESET_FILTER_COOKIE = "rulesetFilter";

    private static final String RULESET = "ruleset";
    private static final String RULESETS = "rulesets";
    private static final String PUBLISHED_RULESETS = "publishedRulesets";

    private static final String ID = "id";
    private static final String QOS = "QOs";
    private static final String ASSOCIATED_QOS = "associatedQos";
    private static final String RULES_CATEGORIES = "rulesCategories";
    private static final String SIZE = "size";
    private static final String USERNAME = "username";

    private static final String ADD_RULESET = "addRuleset";
    private static final String EDIT_RULESET = "editRuleset";
    private static final String REDIRECT_RULESETS = "redirect:/rulesets";
    private static final String REDIRECT_ADD_RULESET = "redirect:/rulesets/add";
    private static final String REDIRECT_EDIT_RULESET = "redirect:/rulesets/edit";

    private static final String ERR_ADD_RULESET = "err_add_ruleset";
    private static final String ERR_ADD_RULESET_NAME = "err_add_ruleset_name";

    private static final int INITIAL_VERSION_NUMBER = 1;
    private static final String RULESET_EDIT_ID_COOKIE = "idEdit";

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionnaireObjectService questionnaireObjectService;

    @Autowired
    private RulesetService rulesetService;

    @Autowired
    private RulesCategoryService rulesCategoryService;

    @GetMapping("/rulesets")
    public String index(Principal principal, Model model) {
        List<RulesetDtoWrapper> publishedRulesetDtos = rulesetService.getPublishedRulesetDtoWrappers();
        List<RulesetDtoWrapper> draftRulesetDtos = rulesetService.getRulesetDtos();

        model.addAttribute(RULESETS, draftRulesetDtos);
        model.addAttribute(PUBLISHED_RULESETS, publishedRulesetDtos);
        model.addAttribute(SIZE, draftRulesetDtos.size() + publishedRulesetDtos.size());
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        return RULESETS;
    }

    @GetMapping("/rulesets/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/rulesetsHelper";
    }

    @GetMapping("/rulesets/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addRulesetHelper";
    }

    @GetMapping("/rulesets/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editRulesetHelper";
    }

    @GetMapping("/rulesets/add")
    public String addRuleset(Principal principal,
                             Model model,
                             @CookieValue(value = RuleController.SELECTED_RULES_CATEGORY_COOKIE, required = false) String selectedRulesCategory) {

        if (!model.containsAttribute(RULESET)) {
            Ruleset ruleset = new Ruleset();
            model.addAttribute(RULESET, ruleset);
        }

        model.addAttribute(QOS, questionnaireObjectService.getDraftQuestionnaireWithPublishedVersion());
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        if (selectedRulesCategory != null) {
            model.addAttribute(RuleController.SELECTED_RULES_CATEGORY, selectedRulesCategory);
        }

        return ADD_RULESET;
    }

    @GetMapping("/rulesets/edit")
    public String editRuleset(Principal principal,
                              @Param(value = ID) Long id,
                              @CookieValue(value = RULESET_EDIT_ID_COOKIE, required = false) String idCookie,
                              Model model,
                              HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(RULESET_EDIT_ID_COOKIE, String.valueOf(id)));
        }

        editRulesetTask(principal, id != null ? id : Long.parseLong(idCookie), model);

        return EDIT_RULESET;
    }

    @PostMapping("/rulesets/create")
    public String createRuleset(@ModelAttribute Ruleset ruleset, Principal principal,
                                RedirectAttributes redirectAttributes,
                                HttpServletResponse response) {

        if (!rulesetService.isAvailable(ruleset.getType())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_RULESET, "");
            redirectAttributes.addFlashAttribute(RULESET, ruleset);
            redirectAttributes.addFlashAttribute(ERR_ADD_RULESET_NAME, "");
            return REDIRECT_ADD_RULESET;
        } else {
            User user = userService.getUserByUsername(principal.getName());

            List<QuestionnaireObject> questionnaireObjects = new ArrayList<>();
            if (ruleset.getQoIds() != null) {
                questionnaireObjects = questionnaireObjectService.withIds(GenericUtils.convertToLongList(ruleset.getQoIds()));
            }

            if (ruleset.getRulesCategory() != null) {
                RulesCategory rulesCategory = rulesCategoryService.withId(ruleset.getRulesCategory().getId());
                ruleset.setRulesCategory(rulesCategory);
                CookieUtils.decorateResponse(response, rulesCategory);
            }

            ruleset.setQuestionnaireObjects(questionnaireObjects);
            ruleset.setReference("");
            ruleset.setUser(user);
            ruleset.setDate(new Date());
            ruleset.setPublished(false);
            ruleset.setVersion(INITIAL_VERSION_NUMBER);
            rulesetService.create(ruleset);

            return REDIRECT_RULESETS;
        }
    }

    @PostMapping("/rulesets/update")
    public String updateRuleset(@ModelAttribute Ruleset ruleset,
                                @RequestParam Long id, Principal principal,
                                RedirectAttributes redirectAttributes) {

        Ruleset oldRuleset = rulesetService.withId(ruleset.getId());
        if (!rulesetService.isAvailable(ruleset.getType()) && !ruleset.getType().trim().replaceAll("\\s+", " ").equalsIgnoreCase(oldRuleset.getType())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_RULESET, "");
            redirectAttributes.addFlashAttribute(RULESET, ruleset);
            redirectAttributes.addFlashAttribute(ERR_ADD_RULESET_NAME, "");
            redirectAttributes.addAttribute("id", id);

            return REDIRECT_EDIT_RULESET;
        } else {
            User user = userService.getUserByUsername(principal.getName());
            List<QuestionnaireObject> questionnaireObjects = new ArrayList<>();
            if (ruleset.getQoIds() != null) {
                questionnaireObjects = questionnaireObjectService.withIds(GenericUtils.convertToLongList(ruleset.getQoIds()));
            }
            ruleset.setQuestionnaireObjects(questionnaireObjects);
            ruleset.setRulesCategory(rulesetService.withId(id).getRulesCategory());
            ruleset.setUser(user);
            ruleset.setId(id);
            ruleset.setDate(new Date());
            ruleset.setVersion(oldRuleset.getVersion());
            rulesetService.update(ruleset);

            return REDIRECT_RULESETS;
        }
    }

    @Transactional
    @RequestMapping(value = "/rulesets/delete", method = RequestMethod.GET)
    public String deleteRuleset(@RequestParam(value = ID) Long id) {
        rulesetService.delete(id);

        return REDIRECT_RULESETS;
    }

    private void editRulesetTask(Principal principal, Long id, Model model) {
        if (!model.containsAttribute(RULESET)) {
            model.addAttribute(RULESET, rulesetService.withId(id));
        }

        model.addAttribute(QOS, questionnaireObjectService.getDraftQuestionnaireWithPublishedVersion());

        List<Long> questionnaireDraftsIds = new ArrayList<>();
        for (QuestionnaireObject qo : rulesetService.withId(id).getQuestionnaireObjects()) {
            questionnaireDraftsIds.add(qo.getId());
        }

        model.addAttribute(ASSOCIATED_QOS, questionnaireDraftsIds);
        model.addAttribute(ID, id);
        model.addAttribute(USERNAME, principal.getName());
    }

    @RequestMapping(value = "/rulesets/details/{reference}", method = RequestMethod.GET)
    public String showAssociatedChainsName(@PathVariable("reference") String reference, Model model) {
        List<RulesetQuestionnaireDto> dtos = rulesetService.findQuestionnairesNameAndEquipmentByReference(reference);
        log.info("CtrlMessage=Retrieving questionnaires related to a ruleset;RulesetReference={};QuestionnairesCount={};", reference, dtos.size());
        model.addAttribute("questionnaireDtoList", dtos);

        return RULESETS + ":: resultsList";
    }

    @RequestMapping(value = "/rulesets/published/details/{versionName}", method = RequestMethod.GET)
    public String showAssociatedChainsNameForPublishedQuestionnaire(@PathVariable("versionName") String versionName, Model model) {
        List<RulesetQuestionnaireDto> dtos = rulesetService.findQuestionnairesNameAndEquipmentByVersionName(versionName);
        log.info("CtrlMessage=Retrieving questionnaires related to a ruleset;RulesetVersionName={};QuestionnairesCount={};", versionName, dtos.size());
        model.addAttribute("questionnaireDtoList", dtos);
        return RULESETS + ":: resultsList";
    }
}
