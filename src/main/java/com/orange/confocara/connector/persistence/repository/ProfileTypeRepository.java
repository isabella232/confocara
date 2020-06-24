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

import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileTypeRepository extends CrudRepository<ProfileType, Long> {

    ProfileType findByName(String name);

    List<ProfileType> findByRulesCategories(RulesCategory rulesCategory);

    ProfileType findByReference(String reference);

    @Query("select u from ProfileType u where instr(ucase(u.name), ucase(:text)) > 0 ")
    List<ProfileType> filterWithName(@Param("text") String filter);
}
