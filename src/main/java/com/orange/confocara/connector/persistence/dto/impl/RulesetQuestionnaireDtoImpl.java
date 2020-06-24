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

package com.orange.confocara.connector.persistence.dto.impl;

import com.orange.confocara.connector.persistence.dto.RulesetQuestionnaireDto;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import lombok.Data;

/**
 * basic implementation of {@link RulesetQuestionnaireDto}
 */
@Data
public class RulesetQuestionnaireDtoImpl implements RulesetQuestionnaireDto {

    private String questionnaireReference;

    private String equipmentName;

    private String questionnaireName;

    private String questionnaireVersion;

    private String questionnaireAuthor;

    /**
     * default constructor
     *
     * @param q a {@link QuestionnaireObject}
     */
    public RulesetQuestionnaireDtoImpl(QuestionnaireObject q) {
        questionnaireReference = q.getReference();
        questionnaireName = q.getName();
        questionnaireVersion = String.valueOf(q.getVersion());
        questionnaireAuthor = q.getUser().getUsername();
        equipmentName = q.getEquipment().getName();
    }
}