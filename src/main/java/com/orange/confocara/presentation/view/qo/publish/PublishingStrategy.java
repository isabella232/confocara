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

package com.orange.confocara.presentation.view.qo.publish;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;

/**
 * Behaviour of a handler for publishing {@link QuestionnaireObject}s
 */
public interface PublishingStrategy {

    /**
     * Checks if the given questionnaire is a valid candidate for the pubishing strategy
     *
     * @param q a {@link QuestionnaireObject}
     * @return true if the questionnaire matches the prerequisites for the publishing strategy
     */
    boolean check(Long id);

    /**
     * Applies the publishing strategy to the given questionnaire
     *
     * @param q a {@link QuestionnaireObject}
     */
    void apply(Long id);
}
