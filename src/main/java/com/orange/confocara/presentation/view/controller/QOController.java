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

import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.EquipmentService;
import com.orange.confocara.business.service.QuestionnaireObjectService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.dto.QOChainsAndSubQuestionnairesDtoWrapper;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class QOController {

    public static final String QUESTIONNAIRE_OBJECT_FILTER_COOKIE = "questionnaireObjectFilter";

    private static final String QO = "QO";

    private static final String USERNAME = "username";

    private static final String CHAIN = "chain";
    private static final String CHAINS = "chains";
    private static final String OBJECTS = "objects";
    private static final String ID = "id";

    private static final String QUESTIONNAIRES = "questionnaires";
    private static final String RULES_CATEGORIES = "rulesCategories";
    private static final String REDIRECT_QUESTIONNAIRES = "redirect:/questionnaires";
    private static final String EDIT_QUESTIONNAIRE = "editQuestionnaire";
    private static final String REDIRECT_QUESTIONNAIRES_ADD = "redirect:/questionnaires/add";
    private static final String REDIRECT_QUESTIONNAIRES_EDIT = "redirect:/questionnaires/edit";
    private static final String ERR_ADD_QUESTIONNAIRE = "err_add_questionnaire";
    private static final String ERR_ADD_QUESTIONNAIRE_NAME = "err_add_questionnaire_name";
    private static final String ERR_EDIT_QUESTIONNAIRE_NAME = "err_edit_questionnaire_name";

    private static final int INITIAL_VERSION_NUMBER = 1;
    private static final String QO_EDIT_ID_COOKIE = "questionnaireEditCookieId";

    @Autowired
    private UserService userService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private RulesCategoryService rulesCategoryService;
    @Autowired
    private QuestionnaireObjectService questionnaireObjectService;

    @GetMapping(value = "/questionnaires/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/questionnairesHelper";
    }

    @GetMapping(value = "/questionnaires/add/help")
    public String showAddHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/addQuestionnaireHelper";
    }

    @GetMapping(value = "/questionnaires/edit/help")
    public String showEditHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/editQuestionnaireHelper";
    }

    @RequestMapping(value = "/questionnaires/details/{reference}", method = RequestMethod.GET)
    public String showAssociatedChainsName(@PathVariable("reference") String reference, Model model) {
        model.addAttribute("chainsNameList", questionnaireObjectService.findChainsNameByReference(reference));

        return QUESTIONNAIRES + ":: resultsList";
    }

    @RequestMapping(value = "/questionnaires/published/details/{versionName}", method = RequestMethod.GET)
    public String showAssociatedChainsNameForPublishedQuestionnaire(@PathVariable("versionName") String versionName, Model model) {
        QOChainsAndSubQuestionnairesDtoWrapper chainsAndSubQuestionnaires = questionnaireObjectService.findChainsNameAndSubQuestionnairesByVersionName(versionName);
        model.addAttribute("chainsNameList", chainsAndSubQuestionnaires.getChains());
        model.addAttribute("questionnaireSubobjectList", chainsAndSubQuestionnaires.getSubobjectQuestionnaireDtos());

        return QUESTIONNAIRES + ":: resultsList";
    }

    /**
     * Populates the view for the creation of a new {@link QuestionnaireObject}
     *
     * @param principal a {@link Principal}
     * @param model a holder for the attributes in the view
     * @param selectedRulesCategory a {@link CookieValue}
     * @return the next location
     */
    @GetMapping("/questionnaires/add")
    public String addQuestionnaire(Principal principal, Model model,
                                   @CookieValue(value = RuleController.SELECTED_RULES_CATEGORY_COOKIE, required = false) String selectedRulesCategory) {
        if (!model.containsAttribute(QO)) {
            QuestionnaireObject questionnaireObject = new QuestionnaireObject();
            model.addAttribute(QO, questionnaireObject);
        }

        model.addAttribute(CHAIN, new Chain());
        model.addAttribute(CHAINS, chainService.all());
        model.addAttribute(OBJECTS, equipmentService.all());
        model.addAttribute(RULES_CATEGORIES, rulesCategoryService.all());
        model.addAttribute(USERNAME, principal.getName());

        if (selectedRulesCategory != null) {
            model.addAttribute(RuleController.SELECTED_RULES_CATEGORY, selectedRulesCategory);
        }

        return "addQuestionnaire";
    }

    /**
     * Populates the view for the edition of an existing {@link QuestionnaireObject}
     *
     * @param principal a {@link Principal}
     * @param id an identifier for a {@link QuestionnaireObject}
     * @param idCookie a token for a {@link Cookie}
     * @param model a holder for the attributes in the view
     * @param response a {@link HttpServletResponse}
     * @return the next location.
     */
    @GetMapping("/questionnaires/edit")
    public String editQuestionnaire(Principal principal, @Param(value = ID) Long id,
                                    @CookieValue(value = QO_EDIT_ID_COOKIE, required = false) String idCookie,
                                    Model model,
                                    HttpServletResponse response) {

        if (id != null) {
            response.addCookie(new Cookie(QO_EDIT_ID_COOKIE, String.valueOf(id)));
        }
        final Long idFinal = id != null ? id : Long.parseLong(idCookie);

        log.info("CtrlMessage=Requesting the edition of a questionnaire;QuestionnaireId={};CurrentUser={};", idFinal, principal.getName());

        if (!model.containsAttribute(QO)) {
            QuestionnaireObject qo = questionnaireObjectService.withId(idFinal);
            qo.setObjectId(Long.toString(qo.getEquipment().getId()));
            qo.initializeChainIds();
            model.addAttribute(QO, qo);
        }

        model.addAttribute(CHAIN, new Chain());
        model.addAttribute(CHAINS, chainService.all());
        model.addAttribute(OBJECTS, equipmentService.all());

        model.addAttribute(ID, idFinal);
        model.addAttribute(USERNAME, principal.getName());

        return EDIT_QUESTIONNAIRE;
    }

    /**
     * Stores a new {@link QuestionnaireObject}
     *
     * @param questionnaireObject the data to store
     * @param redirectAttributes a {@link Model} attribute for redirections
     * @param principal a {@link Principal}
     * @param response a {@link HttpServletResponse}
     * @return the next location. Redirects to the questionnaires'list when successful. Redirects to
     * the current questionnaire add page, when validation fails.
     */
    @RequestMapping(value = "/questionnaires/create", method = RequestMethod.POST)
    public String createQuestionnaire(@ModelAttribute QuestionnaireObject questionnaireObject,
                                      RedirectAttributes redirectAttributes, Principal principal,
                                      HttpServletResponse response) {
        Equipment equipment = null;
        List<Chain> sortedChains = new ArrayList<>();

        if (!questionnaireObjectService.isNameAvailable(questionnaireObject.getName())) {
            redirectAttributes.addFlashAttribute(ERR_ADD_QUESTIONNAIRE_NAME, "");
            redirectAttributes.addFlashAttribute(QO, questionnaireObject);
            return REDIRECT_QUESTIONNAIRES_ADD;
        }

        if (questionnaireObject.getObjectId() != null) {
            equipment = equipmentService.withId(Long.parseLong(questionnaireObject.getObjectId()));
        }

        if (questionnaireObject.getOrderedChainIds() != null) {
            sortedChains = chainService.sortedWithIds(GenericUtils.convertToLongList(questionnaireObject.getOrderedChainIds()));
        }

        if (equipment == null) {
            redirectAttributes.addFlashAttribute(ERR_ADD_QUESTIONNAIRE, "");
            redirectAttributes.addFlashAttribute(QO, questionnaireObject);
            return REDIRECT_QUESTIONNAIRES_ADD;
        } else {
            if (questionnaireObject.getRulesCategory() != null) {
                RulesCategory rulesCategory = rulesCategoryService.withId(questionnaireObject.getRulesCategory().getId());
                questionnaireObject.setRulesCategory(rulesCategory);

                CookieUtils.decorateResponse(response, rulesCategory);
            }

            questionnaireObject.setEquipment(equipment);
            questionnaireObject.setReference("");
            questionnaireObject.setName(questionnaireObject.getName().trim().replaceAll("\\s+", " "));
            questionnaireObject.setChains(sortedChains);
            questionnaireObject.setDate(new Date());
            questionnaireObject.setUser(userService.getUserByUsername(principal.getName()));
            questionnaireObject.setPublished(false);
            questionnaireObject.setVersion(INITIAL_VERSION_NUMBER);
            questionnaireObject.setListPositionAndChainRefMap();

            questionnaireObjectService.create(questionnaireObject);

            return REDIRECT_QUESTIONNAIRES;
        }
    }


    @RequestMapping(value = "/questionnaires/delete", method = RequestMethod.GET)
    public String deleteQuestionnaire(@RequestParam(value = ID) Long id) {
        questionnaireObjectService.delete(id);

        return REDIRECT_QUESTIONNAIRES;
    }
}
