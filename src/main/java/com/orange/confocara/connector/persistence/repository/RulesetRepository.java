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

import com.orange.confocara.connector.persistence.dto.RulesetDto;
import com.orange.confocara.connector.persistence.dto.RulesetQuestionnaireDto;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Interface for Search and CRUD operations on a repository for the {@link Ruleset} type.
 */
public interface RulesetRepository extends CrudRepository<Ruleset, Long> {

    Ruleset findByReference(String reference);

    List<Ruleset> findByQuestionnaireObjects(QuestionnaireObject questionnaireObject);

    List<Ruleset> findByRulesCategory(RulesCategory rulesCategory);

    List<Ruleset> findByUser(User user);

    Ruleset findByType(String type);

    @Query("select "
            + "ruleset.id as id, "
            + "ruleset.reference as reference, "
            + "ruleset.type as name, "
            + "ruleset.published as published,"
            + "ruleset.version as version, "
            + "ruleset.rulesCategory.name as rulesCategoryName, "
            + "ruleset.language as language, "
            + "ruleset.comment as comment, "
            + "case when user is null then '' else ruleset.user.username end as username, "
            + "ruleset.date as date "
            + "from Ruleset ruleset "
            + "LEFT JOIN ruleset.user as user")
    List<RulesetDto> findAllRulesetDto();

    @Query("select "
            + "questionnaire.reference as questionnaireReference, "
            + "questionnaire.equipment.name as equipmentName, "
            + "questionnaire.name as questionnaireName, "
            + "questionnaire.version as questionnaireVersion, "
            + "questionnaire.user.username as questionnaireAuthor "
            + "from Ruleset ruleset "
            + "INNER JOIN ruleset.questionnaireObjects as questionnaire "
            + "where ruleset.reference = :reference "
            + "ORDER BY questionnaireName ASC")
    List<RulesetQuestionnaireDto> findQuestionnairesNameAndEquipmentByReference(@Param("reference") String reference);
}
