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

package com.orange.confocara.presentation.view.qo.list;

import com.orange.confocara.connector.persistence.model.ById;
import com.orange.confocara.connector.persistence.model.ByQuestionnaireRef;
import com.orange.confocara.connector.persistence.model.WithSubObjects;

/**
 * Behaviour of some data transfer object. It extends {@link ById} and {@link WithSubObjects}.
 */
public interface QuestionnaireWithSubObjectsDto extends ById, WithSubObjects<ByQuestionnaireRef> {

}
