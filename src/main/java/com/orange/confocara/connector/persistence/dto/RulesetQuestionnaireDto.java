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

package com.orange.confocara.connector.persistence.dto;

/**
 * behaviour of a Data Transfer Object of {@link com.orange.confocara.connector.persistence.model.QuestionnaireObject}s
 */
public interface RulesetQuestionnaireDto {

    /**
     *
     * @return a {@link com.orange.confocara.connector.persistence.model.QuestionnaireObject#reference}
     */
    String getQuestionnaireReference();

    /**
     *
     * @return a {@link com.orange.confocara.connector.persistence.model.Equipment#name}
     */
    String getEquipmentName();

    /**
     *
     * @return a {@link com.orange.confocara.connector.persistence.model.QuestionnaireObject#name}
     */
    String getQuestionnaireName();

    /**
     *
     * @return a {@link com.orange.confocara.connector.persistence.model.QuestionnaireObject#version}
     */
    String getQuestionnaireVersion();

    /**
     *
     * @return a {@link com.orange.confocara.connector.persistence.model.User#username}
     */
    String getQuestionnaireAuthor();
}
