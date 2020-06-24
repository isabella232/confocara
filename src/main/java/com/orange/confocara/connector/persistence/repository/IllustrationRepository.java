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

import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IllustrationRepository extends CrudRepository<Illustration, Long> {

    List<Illustration> findByTitle(String name);

    List<Illustration> findAllByOrderByIdDesc();

    List<Illustration> findByUser(User user);

    Illustration findByReference(String reference);

    @Query("select u from Illustration u where instr(ucase(u.title), ucase(:text)) > 0 " + " order by u.id desc")
    List<Illustration> filterWithTitleByOrderByIdDesc(@Param("text") String text);
}
