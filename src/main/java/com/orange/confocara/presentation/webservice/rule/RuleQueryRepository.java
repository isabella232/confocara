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

package com.orange.confocara.presentation.webservice.rule;

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.QChain;
import com.orange.confocara.connector.persistence.model.QQuestion;
import com.orange.confocara.connector.persistence.model.QQuestionnaireObject;
import com.orange.confocara.connector.persistence.model.QRule;
import com.orange.confocara.connector.persistence.model.QRuleImpact;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebService.RuleSearchCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.PageableExecutionUtils;

@NoRepositoryBean
public interface RuleQueryRepository extends Repository<Rule, Long> {

    Page<Rule> findAll(RuleSearchCriteria criteria, Pageable pageable);

    static RuleQueryRepository instance(EntityManager entityManager) {

        JpaEntityInformation entityInformation = JpaEntityInformationSupport
                .getEntityInformation(Rule.class, entityManager);

        return new RuleQueryRepositoryImpl(entityInformation, entityManager);
    }

    /**
     * default implementation of {@link RuleQueryRepository}
     */
    class RuleQueryRepositoryImpl extends QueryDslJpaRepository<Rule, Long>
            implements RuleQueryRepository {

        private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

        private final EntityManager entityManager;

        private final EntityPath<Rule> path;
        private final PathBuilder<Rule> builder;
        private final Querydsl querydsl;

        public RuleQueryRepositoryImpl(JpaEntityInformation<Rule, Long> entityInformation,
                EntityManager entityManager) {
            this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
        }

        public RuleQueryRepositoryImpl(JpaEntityInformation<Rule, Long> entityInformation,
                EntityManager entityManager, EntityPathResolver resolver) {

            super(entityInformation, entityManager);
            this.path = resolver.createPath(entityInformation.getJavaType());
            this.builder = new PathBuilder(this.path.getType(), this.path.getMetadata());
            this.querydsl = new Querydsl(entityManager, this.builder);
            this.entityManager = entityManager;
        }

        @Override
        public Page<Rule> findAll(RuleSearchCriteria criteria, Pageable pageable) {

            QRule qRule = QRule.rule;
            JPQLQuery jpqlQuery = new JPAQuery<>(entityManager).from(qRule);

            QQuestionnaireObject qQO = QQuestionnaireObject.questionnaireObject;

            ListPath<Chain, QChain> qChains = qQO.chains;
            QQuestion qQuestion = qChains.any().questions.any();

            BooleanBuilder predicate = new BooleanBuilder();

            if (criteria.getEquipmentIds() != null && !criteria.getEquipmentIds().isEmpty()) {
                jpqlQuery
                        .from(qQO)
                        .join(qChains);

                predicate
                        .and(qQuestion.rules.any().id.eq(qRule.id))
                        .and(qQO.equipment.id.in(criteria.getEquipmentIds()));
            }

            if (criteria.getContextIds() != null && !criteria.getContextIds().isEmpty()) {
                predicate
                        .and(qRule.rulesCategory.id.in(criteria.getContextIds()));
            }

            if (criteria.getConcernIds() != null && !criteria.getConcernIds().isEmpty()) {
                ListPath<RuleImpact, QRuleImpact> qRuleImpacts = qRule.ruleImpacts;

                QRuleImpact qRuleImpact = QRuleImpact.ruleImpact;

                predicate
                        .and(
                                qRuleImpacts.any().id.in(
                                        JPAExpressions
                                                .select(qRuleImpact.id)
                                                .from(qRuleImpact)
                                                .where(qRuleImpact.profileType.id.in(criteria.getConcernIds())
                                                    .and(qRuleImpact.impact.id.goe(2L))
                                                )
                                ));
            }
            if (criteria.getLabel() != null && !criteria.getLabel().isEmpty()) {
                predicate
                 .and(qRule.label.contains(criteria.getLabel()));
        }
                      jpqlQuery
                    .distinct()
                    .where(predicate);

            // Count query
            final JPQLQuery<Rule> countQuery = jpqlQuery;

            // Apply pagination
            JPQLQuery<Rule> query = querydsl.applyPagination(pageable, jpqlQuery);

            // Run query
            return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount);
        }
    }
}
