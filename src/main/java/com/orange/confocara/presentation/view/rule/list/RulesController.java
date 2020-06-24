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

package com.orange.confocara.presentation.view.rule.list;

import com.orange.confocara.business.service.EquipmentService;
import com.orange.confocara.business.service.ProfileTypeService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.connector.persistence.model.WithId;
import com.orange.confocara.connector.persistence.model.WithName;
import com.orange.confocara.connector.persistence.model.WithReference;
import java.security.Principal;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.immutables.value.Value.Immutable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class RulesController {

    private static final String RULES = "rules";
    private static final String USERNAME = "username";
    private static final String RULES_CATEGORIES = "allCategories";


    private final RulesListService rulesListService;

    private final RulesCategoryService rulesCategoryService;

    private final EquipmentService equipmentService;

    private final ProfileTypeService profileTypeService;

    @GetMapping("/rules")
    public String index(Principal principal, Model model) {
        model.addAttribute(RULES, rulesListService.retrieveAllRules());
        model.addAttribute(
                RULES_CATEGORIES,
                rulesCategoryService
                        .all()
                        .stream()
                        .map(context -> ImmutableContextDto
                                .builder()
                                .id(context.getId())
                                .name(context.getName())
                                .reference("C" + context.getId())
                                .build())
                        .collect(Collectors.toList()));
        model.addAttribute(
                "allEquipments",
                equipmentService
                        .all()
                        .stream()
                        .map(equipment -> ImmutableEquipmentDto
                                .builder()
                                .id(equipment.getId())
                                .name(equipment.getName())
                                .reference(equipment.getReference())
                                .build())
                        .collect(Collectors.toList()));
        model.addAttribute(
                "allProfileTypes",
                profileTypeService
                        .all()
                        .stream()
                        .map(profileType -> ImmutableProfileTypeDto
                                .builder()
                                .id(profileType.getId())
                                .name(profileType.getName())
                                .reference(profileType.getReference())
                                .build())

                        .collect(Collectors.toList()));
        model.addAttribute(USERNAME, principal.getName());

        return RULES;
    }

    @GetMapping(value = "/rules/help")
    public String showHelpPage(Model model, Principal principal) {
        model.addAttribute(USERNAME, principal.getName());
        return "helpers/rulesHelper";
    }

    @Immutable
    interface ProfileTypeDto extends LightDto {

    }

    @Immutable
    interface EquipmentDto extends LightDto {

    }

    @Immutable
    interface ContextDto extends LightDto {

    }

    @Immutable
    interface LightDto extends WithId, WithName, WithReference {

    }
}
