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

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EquipmentRepository extends CrudRepository<Equipment, Long> {

    List<Equipment> findAllByOrderByNameAsc();

    Equipment findByName(String name);

    List<Equipment> findByCategories(Category category);

    List<Equipment> findByUser(User user);

    List<Equipment> findByType(String type);

    List<Equipment> findByIllustrations(Illustration illustration);

    List<Equipment> findBySubobjects(Equipment equipment);

    Equipment findByReference(String reference);
}
