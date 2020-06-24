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

package com.orange.confocara.connector.persistence.repository;


import com.orange.confocara.connector.persistence.model.PublishedRuleset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

/**
 * Repository for {@link PublishedRuleset} entities
 */
@org.springframework.stereotype.Repository
public interface RulesetPublishingRepository extends CrudRepository<PublishedRuleset, Long> {

    /**
     *
     * @param reference a {@link com.orange.confocara.connector.persistence.model.Ruleset#reference}
     * @param version a {@link com.orange.confocara.connector.persistence.model.Ruleset#version}
     * @return a {@link PublishedRuleset}, if exists. Else null.
     */
    PublishedRuleset findOneByReferenceAndVersion(String reference, Integer version);

    /**
     *
     * @param entity a {@link PublishedRuleset}
     * @return the updated {@link PublishedRuleset}
     */
    PublishedRuleset save(PublishedRuleset entity);
}
