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

public interface QuestionnaireDto {

    long getId();

    String getReference();

    String getName();

    boolean getPublished();

    Integer getVersion();

    String getRulesCategoryName();

    String getEquipmentName();

    String getUsername();

    java.util.Date getDate();
}
