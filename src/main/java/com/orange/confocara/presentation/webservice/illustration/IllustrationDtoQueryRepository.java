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

package com.orange.confocara.presentation.webservice.illustration;

import com.orange.confocara.connector.persistence.model.Illustration;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link Illustration}s
 */
@FunctionalInterface
@org.springframework.stereotype.Repository
interface IllustrationDtoQueryRepository extends Repository<Illustration, Long> {

    @Query("SELECT DISTINCT "
            + "ill.id AS illustrationId, "
            + "ill.title AS illustrationTitle, "
            + "ill.reference AS illustrationReference, "
            + "ill.comment AS illustrationComment, "
            + "img.uuid AS imageName "
            + "FROM Rule r "
            + "INNER JOIN r.illustrations AS ill "
            + "LEFT JOIN ill.image AS img "
            + "WHERE r.id = :id "
            + "ORDER BY illustrationId ASC")
    List<IllustrationDto> findAll(@Param("id") Long ruleId);
}
