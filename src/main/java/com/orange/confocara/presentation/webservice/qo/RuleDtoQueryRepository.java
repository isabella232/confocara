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

package com.orange.confocara.presentation.webservice.qo;

import com.orange.confocara.connector.persistence.model.Rule;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link Rule}s
 */
@FunctionalInterface
@org.springframework.stereotype.Repository
public interface RuleDtoQueryRepository extends Repository<Rule, Long> {

    @Query("SELECT DISTINCT "
            + "qo.id AS questionnaireId, "
            + "qo.name AS questionnaireName, "
            + "qo.equipment.name AS equipmentName, "
            + "c.id AS chainId, "
            + "c.name AS chainName, "
            + "c.reference AS chainReference, "
            + "q.id AS questionId, "
            + "q.label AS questionName, "
            + "q.reference AS questionReference, "
            + "r.id AS ruleId, "
            + "r.label AS ruleName, "
            + "r.reference AS ruleReference, "
            + "r.rulesCategory.name AS ruleCategoryName, "
            + "COUNT(i.id) AS illustrationsNb "
            + "FROM QuestionnaireObject qo "
            + "INNER JOIN qo.chains AS c "
            + "INNER JOIN c.questions AS q "
            + "INNER JOIN q.rules AS r "
            + "LEFT JOIN r.illustrations AS i "
            + "WHERE qo.id = :id "
            + "GROUP BY r.id "
            + "ORDER BY chainName, questionnaireName, ruleName ASC")
    List<RuleDto> findAll(@Param("id") Long questionnaireId);
}
