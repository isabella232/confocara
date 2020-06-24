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

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuestionnaireObjectRepository extends CrudRepository<QuestionnaireObject, Long> {

    List<QuestionnaireObject> findAllByOrderByIdDesc();

    QuestionnaireObject findByReference(String reference);

    QuestionnaireObject findByName(String name);

    QuestionnaireObject findById(Long id);

    List<QuestionnaireObject> findByEquipment(Equipment equipment);

    List<QuestionnaireObject> findByEquipmentName(String name);

    List<QuestionnaireObject> findByEquipmentReference(String reference);

    List<QuestionnaireObject> findByChains(Chain chain);

    List<QuestionnaireObject> findByUser(User user);

    List<QuestionnaireObject> findByRulesCategory(RulesCategory rulesCategory);

    @Query("select chain.name from QuestionnaireObject qo INNER JOIN qo.chains as chain where qo.reference = :reference")
    List<String> findChainsNameByReference(@Param("reference") String reference);

    List<QuestionnaireObject> findByQuestionnaireSubObjects(QuestionnaireObject questionnaireObject);
}
