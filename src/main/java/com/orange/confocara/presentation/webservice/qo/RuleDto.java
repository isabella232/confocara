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

package com.orange.confocara.presentation.webservice.qo;

/**
 * Data Transfer Object for {@link com.orange.confocara.connector.persistence.model.Rule}s
 */
public interface RuleDto {

    Long getQuestionnaireId();

    String getQuestionnaireName();

    String getEquipmentName();

    Long getChainId();

    String getChainName();

    String getChainReference();

    Long getQuestionId();

    String getQuestionName();

    String getQuestionReference();

    Long getRuleId();

    String getRuleReference();

    String getRuleName();

    String getRuleCategoryName();

    int getIllustrationsNb();
}
