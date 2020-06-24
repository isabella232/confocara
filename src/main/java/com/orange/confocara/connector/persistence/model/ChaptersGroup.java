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

import java.util.Date;
import java.util.List;

/**
 * description of a referential
 *
 * a {@link ChaptersGroup} is basically an aggregate of {@link Chapter}s.
 * */
public interface ChaptersGroup extends ById, IsVersionable, WithState, IsPublishable,
        WithUser, WithContext {

    String getLanguage();

    String getType();

    String getComment();

    List<Chapter> getChapters();

    String getVersionName();

    Date getDate();
}
