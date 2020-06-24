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

package com.orange.confocara.presentation.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orange.confocara.connector.persistence.model.Ruleset;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleSetExport {

    private String reference;
    private String app;
    private Integer version;
    private String language;
    private String state;
    private String comment;
    private String type;
    private RulesCategoryExport rulesCategory;
    private UserCredentialsWS userCredentials;
    private List<QuestionnaireGroup> questionnaireGroups;
    private List<ObjectDescriptionExport> objectDescriptions;
    private List<QuestionExport> questions;
    private List<QuestionnaireExport> questionnaires;
    private List<RuleExport> rules;
    private List<IllustrationWS> illustrations;
    private List<ImpactValueExport> impactValues;
    private List<ProfileTypeExport> profileTypes;
    private String date;

    public RuleSetExport(Ruleset ruleset) {
        app = ruleset.getApp();
        version = ruleset.getVersion();
        language = ruleset.getLanguage();
        reference = ruleset.getReference();
        state = ruleset.getState();
        type = ruleset.getType();
        userCredentials = UserCredentialsWS.newInstance(ruleset.getUser());
    }
}