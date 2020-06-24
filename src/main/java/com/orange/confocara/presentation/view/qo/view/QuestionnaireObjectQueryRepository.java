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

package com.orange.confocara.presentation.view.qo.view;

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link QuestionnaireObject}s
 */
@Repository
public interface QuestionnaireObjectQueryRepository extends JpaRepository<QuestionnaireObject, Long> {

    boolean existsByReferenceAndVersion(String reference, Integer version);

    QuestionnaireObject findByReferenceAndVersion(String reference, Integer version);
}
