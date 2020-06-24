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

import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RulesCategoryRepository extends CrudRepository<RulesCategory, Long> {

    @Override
    List<RulesCategory> findAll();

    RulesCategory findByName(String name);

    List<RulesCategory> findByImpactValues(ImpactValue impactValue);

    List<RulesCategory> findByProfileTypes(ProfileType profileType);

    @Query("select u from RulesCategory u where instr(ucase(u.name), ucase(:text)) > 0 ")
    List<RulesCategory> filterWithName(@Param("text") String filter);
}
