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

package com.orange.confocara.presentation.view.qo.update;

import com.orange.confocara.business.service.ChainService;
import com.orange.confocara.business.service.EquipmentService;
import com.orange.confocara.business.service.QuestionnaireObjectService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.presentation.view.controller.utils.GenericUtils;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class QuestionnaireObjectUpdateController {

    private static final String QO = "QO";

    private static final String ID = "id";

    private static final String REDIRECT_QUESTIONNAIRES = "redirect:/questionnaires";
    private static final String REDIRECT_QUESTIONNAIRES_EDIT = "redirect:/questionnaires/edit";
    private static final String ERR_EDIT_QUESTIONNAIRE_NAME = "err_edit_questionnaire_name";

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

    /**
     * Updates and saves a {@link QuestionnaireObject}
     *
     * @param questionnaireObject a {@link QuestionnaireObject}
     * @param id an identifier for a {@link QuestionnaireObject}
     * @param redirectAttributes a {@link Model} attribute for redirections
     * @param principal a {@link Principal}
     * @return the next location. Redirects to the questionnaires'list when successful. Redirects to
     * the current questionnaire edit page, when validation fails.
     */
    @Transactional
    @RequestMapping(value = "/questionnaires/update", method = RequestMethod.POST)
    public String updateQuestionnaire(@ModelAttribute QuestionnaireObject questionnaireObject,
            @RequestParam Long id,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (questionnaireObject.getObjectId() != null) {
            Equipment equipment = equipmentService.withId(Long.parseLong(questionnaireObject.getObjectId()));
            questionnaireObject.setEquipment(equipment);
        }

        if (questionnaireObject.getOrderedChainIds() != null) {
            List<Chain> sortedChains = chainService.sortedWithIds(GenericUtils.convertToLongList(questionnaireObject.getOrderedChainIds()));
            questionnaireObject.setChains(sortedChains);
        }

        QuestionnaireObject qo = questionnaireObjectService.withId(id);
        RulesCategory category = rulesCategoryService.withId(qo.getRulesCategory().getId());

        questionnaireObject.setId(id);
        questionnaireObject.setRulesCategory(category);
        questionnaireObject.setUser(userService.getUserByUsername(principal.getName()));
        questionnaireObject.setDate(new Date());
        questionnaireObject.setLastUpdateDate(new Date());
        questionnaireObject.setVersion(qo.getVersion());
        questionnaireObject.setListPositionAndChainRefMap();

        if (questionnaireObjectService.checkNameIsAvailable(questionnaireObject) ||
                questionnaireObject.getName().equalsIgnoreCase(qo.getName())) {
            questionnaireObjectService.update(questionnaireObject);
            return REDIRECT_QUESTIONNAIRES;
        } else {
            redirectAttributes.addFlashAttribute(ERR_EDIT_QUESTIONNAIRE_NAME, "");
            redirectAttributes.addFlashAttribute(QO, questionnaireObject);
            redirectAttributes.addAttribute(ID, id);
            return REDIRECT_QUESTIONNAIRES_EDIT;
        }
    }
}
