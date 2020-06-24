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


import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Basic repository for CRUD operations on {@link QuestionnaireObject} entities
 */
@Repository
public interface QuestionnaireObjectReadRepository extends
        org.springframework.data.repository.Repository<QuestionnaireObject, Long> {

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    QuestionnaireObject findOne(Long id);

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids a bunch of identifiers
     * @return all entities that match the argument
     */
    Iterable<QuestionnaireObject> findAll(Iterable<Long> ids);

    /**
     * Retrieves an entity by its reference.
     *
     * @param reference must not be {@literal null}.
     * @return the entity with the given reference or {@literal null} if none found
     * @throws IllegalArgumentException if {@code reference} is {@literal null}
     */
    QuestionnaireObject findByReference(String reference);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    boolean exists(Long id);

    /**
     * Retrieves an entity by the name of a common equipment.
     *
     * @param name label of an equipement
     * @return a {@link List} of entities
     */
    List<QuestionnaireObject> findByEquipmentName(String name);
}
