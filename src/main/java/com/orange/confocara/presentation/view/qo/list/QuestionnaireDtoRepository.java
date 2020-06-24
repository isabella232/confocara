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

package com.orange.confocara.presentation.view.qo.list;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireIdDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireQuestionsDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireRulesDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireSubObjectsDto;
import com.orange.confocara.connector.persistence.model.ByReference;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface QuestionnaireDtoRepository extends Repository<QuestionnaireObject, Long> {

    QuestionnaireWithSubObjectsDto findOne(Long id);

    List<ByReference> findByEquipmentName(String name);

    @Query("select "
            + "qo.id as id, "
            + "qo.reference as reference, "
            + "qo.name as name, "
            + "qo.published as published,"
            + "qo.version as version, "
            + "qo.rulesCategory.name as rulesCategoryName, "
            + "qo.equipment.name as equipmentName, "
            + "case when user is null then '' else qo.user.username end as username, "
            + "qo.date as date "
            + "from QuestionnaireObject qo "
            + "LEFT JOIN qo.user as user "
            + "ORDER BY id desc")
    List<QuestionnaireDto> findAll();

    @Query("select "
            + "qo.id as id, "
            + "count(qu) as questionsNb "
            + "from QuestionnaireObject qo "
            + "INNER JOIN qo.chains as chain "
            + "INNER JOIN chain.questions as qu "
            + "GROUP BY qo")
    List<QuestionnaireQuestionsDto> findAllQuestionnaireQuestionDto();

    @Query("select "
            + "qo.id as id, "
            + "count(subobjects) as subObjectsNb "
            + "from QuestionnaireObject qo "
            + "INNER JOIN qo.equipment as e "
            + "INNER JOIN e.subobjects as subobjects "
            + "GROUP BY qo")
    List<QuestionnaireSubObjectsDto> findAllQuestionnaireSubObjectDto();

    @Query("select "
            + "qo.id as id, "
            + "count(ru) as rulesNb "
            + "from QuestionnaireObject qo "
            + "INNER JOIN qo.chains as chain "
            + "INNER JOIN chain.questions as qu "
            + "INNER JOIN qu.rules as ru "
            + "GROUP BY qo")
    List<QuestionnaireRulesDto> findAllQuestionnaireRulesDto();


    @Query("select "
            + "qo.id as id, "
            + "qo.reference as reference, "
            + "qo.name as name "
            + "from QuestionnaireObject qo "
            + "INNER JOIN qo.equipment as equip1 "
            + "LEFT JOIN equip1.subobjects as sub1 "
            + "LEFT JOIN qo.chains as chain "
            + "LEFT JOIN chain.questions as qu "
            + "where qu.state = 'inactive' or size(qo.chains) = 0 or size(chain.questions) = 0"
            + " or sub1.id in "
            + "("
            + "select distinct equip.id "
            + "from QuestionnaireObject qo2 "
            + "INNER JOIN qo2.equipment as equip "
            + "LEFT JOIN qo2.chains as chain1 "
            + "LEFT JOIN chain1.questions as qu1 "
            + "where qu1.state = 'inactive' or size(qo2.chains) = 0 or size(chain1.questions) = 0) "
            + "or sub1.id not in "
            + "("
            + "select distinct equip2.id from QuestionnaireObject qo3 "
            + "INNER JOIN qo3.equipment as equip2) "
            + "GROUP BY qo")
    List<QuestionnaireIdDto> findAllInactiveQuestionnaire();
}
