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

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import lombok.Data;

/**
 * This class is used to set questionnaireDto for revision questionnaire entities
 */

@Data
public class QuestionnaireDtoImpl implements QuestionnaireDto {

    private long id;

    private String reference;

    private String name;

    private boolean published;

    private Integer version;

    private String rulesCategoryName;

    private String equipmentName;

    private String versionName;

    private String username;

    private java.util.Date date;

    @Override
    public boolean getPublished() {
        return published;
    }
}
