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

import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository for retrieving some associations between {@link QuestionnaireObject}s and {@link Ruleset}s
 */
@org.springframework.stereotype.Repository
public interface RulesetQuestionnaireDtoQueryRepository extends Repository<Ruleset, Long> {

    @Query("SELECT DISTINCT "
            + "qo.id AS id, "
            + "qo.name AS name, "
            + "qo.reference AS reference, "
            + "r.id AS rulesetId, "
            + "case when (qo.version > 0) then true else false end AS alreadyPublished "
            + "FROM Ruleset r "
            + "INNER JOIN r.questionnaireObjects AS qo "
            + "WHERE qo.reference = :reference "
            + "AND qo.version = :version "
            + "GROUP BY qo.id "
            + "ORDER BY id, name ASC")
    List<RulesetQuestionnaireLightDto> findAllByReferenceAndVersion(@Param("reference") String reference, @Param("version") Integer version);

    @Query("SELECT DISTINCT "
            + "r.id AS rulesetId, "
            + "r.reference AS rulesetReference, "
            + "r.rulesCategory.name AS rulesetName, "
            + "qo.id AS questionnaireId, "
            + "qo.name AS questionnaireName, "
            + "qo.reference AS questionnaireReference "
            + "FROM Ruleset r "
            + "INNER JOIN r.questionnaireObjects AS qo "
            + "WHERE r.id IN :ids "
            + "GROUP BY r.id "
            + "ORDER BY questionnaireId, questionnaireName ASC")
    List<RulesetQuestionnaireEnhancedDto> findAllByRulesetIds(@Param("ids") List<Long> rulesetIds);
}
