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

package com.orange.confocara.presentation.view.qo.view;

import com.orange.confocara.connector.persistence.model.QQuestionnaireObject;
import com.orange.confocara.connector.persistence.model.QRuleset;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface RulesetQueryRepository extends Repository<Ruleset, Long>,
        QueryDslPredicateExecutor<Ruleset> {

    class RulesetCriteriaBuilder {

        private final QRuleset qRuleset = QRuleset.ruleset;

        private final ListPath<QuestionnaireObject, QQuestionnaireObject> qQuestionnaireObject = qRuleset.questionnaireObjects;

        private Long questionnaireId = null;

        private boolean withQuestionnaireId = false;

        public RulesetCriteriaBuilder withQuestionnaireId(Long questionnaireId) {
            this.questionnaireId = questionnaireId;
            withQuestionnaireId = true;
            return this;
        }

        public BooleanExpression buildPredicate() {

            return Expressions.allOf(matchQuestionnaire());
        }

        private BooleanExpression matchQuestionnaire() {

            return withQuestionnaireId && questionnaireId != null
                    ?
                    Expressions.asBoolean(qQuestionnaireObject.any().id.eq(questionnaireId))
                    :
                    null;
        }
    }
}
