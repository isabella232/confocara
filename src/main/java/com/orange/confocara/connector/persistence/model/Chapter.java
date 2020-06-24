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

package com.orange.confocara.connector.persistence.model;

import java.util.List;

/** description of an aggregate of {@link SubChapter}s */
public interface Chapter extends WithId, IsVersionable, WithName, WithState, IsPublishable {

    User getUser();

    RulesCategory getRulesCategory();

    List<SubChapter> getSubChapters();

    Equipment getEquipment();

    List<Chapter> getDependentChapters();

    List<CriteriaGroup> getCriteriaGroups();

    long questionsCount();

    long rulesCount();
}
