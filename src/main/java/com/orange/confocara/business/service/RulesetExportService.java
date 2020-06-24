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

package com.orange.confocara.business.service;

import com.orange.confocara.business.service.helper.PublishedRulesetHelper;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.dto.impl.RulesetDtoImpl;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.JoinType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Behaviour of a service that retrieves exports of {@link Ruleset}s
 */
public interface RulesetExportService {

    /**
     *
     * @return a {@link List} of {@link RulesetDtoWrapper}
     */
    List<RulesetDtoWrapper> getPublishedRulesetDtoWrappers();

    /**
     * Retrieves the {@link Ruleset} that matches the given reference and version
     *
     * @return a {@link Ruleset}
     */
    Ruleset findPublishedRulesetByReferenceAndVersion(@NonNull String reference, int version);

    /**
     * Default implementation of {@link RulesetExportService}
     */
    @Service
    @Slf4j
    @Transactional
    class RulesetExportServiceImpl implements RulesetExportService {

        private static final String PUBLISHED_PROPERTY = "published";

        @Autowired
        private QuestionnaireObjectService questionnaireObjectService;

        @PersistenceContext
        private EntityManager entityManager;

        /**
         * Generates the RulesetDtoWrapper of all published rulesets
         *
         * @return the RulesetDtoWrapper of all published rulesets
         */
        @Override
        public List<RulesetDtoWrapper> getPublishedRulesetDtoWrappers() {
            // get the list of published questionnaires revision number
            List publishedRulesetRevisionsQueryResult = getPublishedRulesetsVersions();

            // now, for each published questionnaire, we need to get dto properties
            // we use forEntitiesAtRevision because JOIN are not available with forRevisionsOfEntity
            return getRulesetDtoWrappers(publishedRulesetRevisionsQueryResult);
        }

        /**
         * Find the published ruleset with version and reference
         *
         * @param reference the reference
         * @param version the version number
         * @return ruleset with reference and version number
         */
        @Override
        public Ruleset findPublishedRulesetByReferenceAndVersion(@NonNull String reference,
                int version) {

            Object result = null;

            try {
                result = AuditReaderFactory.get(entityManager).createQuery()
                        .forRevisionsOfEntity(Ruleset.class, false, false)
                        .add(AuditEntity.property("reference").eq(reference))
                        .add(AuditEntity.property("version").eq(version))
                        .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                        .getSingleResult();
            } catch (NoResultException e) {
                log.error("No published ruleset with reference=" + reference + " and version="
                        + version + " found", e);
            }

            if (result instanceof Object[] && ((Object[]) result)[0] != null
                    && ((Object[]) result)[0] instanceof Ruleset) {
                Ruleset ruleset = (Ruleset) ((Object[]) result)[0];
                log.debug(
                        "Message=Found {} published rulesets;Ruleset[0].reference={};Ruleset[0].version={}",
                        ((Object[]) result).length, ruleset.getReference(), ruleset.getVersion());

                ruleset.setVersionName(
                        generateVersionName(ruleset.getReference(), ruleset.getVersion()));
                Map<Integer, QuestionnaireObject> revNumberQuestionnaireMap = questionnaireObjectService
                        .withVersionNames(ruleset.getPublishedQuestionnairesVersionNames());
                List<QuestionnaireObject> questionnairesWithRelevantData = new PublishedRulesetHelper()
                        .checkQuestionnairesData(revNumberQuestionnaireMap);
                ruleset.setQuestionnaireObjects(questionnairesWithRelevantData);

                return ruleset;
            }

            return null;
        }

        /**
         * Returns all revision nb for published rulesets
         *
         * @return all revision nb for published rulesets
         */
        private List getPublishedRulesetsVersions() {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forRevisionsOfEntity(Ruleset.class, false, false)
                    .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                    .addProjection(AuditEntity.revisionNumber())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }

        /**
         * generates the ruleset dto wrappers containing all the needed information of given
         * rulesets
         *
         * @param publishedRulesetRevisionsQueryResult all revision nb for published rulesets
         * @return the ruleset dto wrappers
         */
        private List<RulesetDtoWrapper> getRulesetDtoWrappers(
                List publishedRulesetRevisionsQueryResult) {
            List<RulesetDtoWrapper> publishedRulesets = new ArrayList<>();

            for (Object ruleset : publishedRulesetRevisionsQueryResult) {
                int rulesetRevNb = (int) ruleset;

                List publishedRulesetDtoQueryResult = getRulesetPropertiesResultList(rulesetRevNb);

                List publishedRulesetDtoUsernameQueryResult = getRulesetUsernameResultList(
                        rulesetRevNb);

                Map<Integer, String> usernameMap = new HashMap<>();

                for (Object usernameArray : publishedRulesetDtoUsernameQueryResult) {
                    if (usernameArray instanceof Object[]) {
                        usernameMap.put((Integer) ((Object[]) usernameArray)[0],
                                (String) ((Object[]) usernameArray)[1]);
                    }
                }

                for (Object rulesetDto : publishedRulesetDtoQueryResult) {
                    if (rulesetDto instanceof Object[]) {
                        RulesetDtoWrapper rulesetDtoWrapper = getRulesetDtoWrapper(usernameMap,
                                (Object[]) rulesetDto);

                        publishedRulesets.add(rulesetDtoWrapper);
                    }
                }
            }

            return publishedRulesets;
        }

        /**
         * select ruleset's properties that must be displayed on ruleset's page
         *
         * @param rulesetRevNb the revision nb
         * @return the ruleset properties
         */
        private List getRulesetPropertiesResultList(int rulesetRevNb) {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forEntitiesAtRevision(Ruleset.class, rulesetRevNb)
                    .traverseRelation("rulesCategory", JoinType.INNER)
                    .addProjection(AuditEntity.property("name"))
                    .up()
                    .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                    .addProjection(AuditEntity.property("id"))
                    .addProjection(AuditEntity.property("reference"))
                    .addProjection(AuditEntity.property("type"))
                    .addProjection(AuditEntity.property("version"))
                    .addProjection(AuditEntity.property("date"))
                    .addProjection(AuditEntity.property("comment"))
                    .addProjection(AuditEntity.property("language"))
                    .addProjection(AuditEntity.revisionNumber())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }

        /**
         * Returns the ruleset username
         *
         * @param rulesetRevNb the revision nb
         * @return the ruleset username
         */
        private List getRulesetUsernameResultList(int rulesetRevNb) {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forEntitiesAtRevision(Ruleset.class, rulesetRevNb)
                    .addProjection(AuditEntity.revisionNumber())
                    .traverseRelation("user", JoinType.LEFT)
                    .addProjection(AuditEntity.property("username"))
                    .up()
                    .add(AuditEntity.property("user").isNotNull())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }

        /**
         * Returns RulesetDtoWrapper with query result
         *
         * @param usernameMap the user usernames mapped by ruleset revision number
         * @param rulesetDto query result
         * @return RulesetDtoWrapper
         */
        private RulesetDtoWrapper getRulesetDtoWrapper(Map<Integer, String> usernameMap,
                Object[] rulesetDto) {
            RulesetDtoWrapper rulesetDtoWrapper = new RulesetDtoWrapper();
            RulesetDtoImpl dto = new RulesetDtoImpl();
            String reference = (String) rulesetDto[2];
            Integer version = (Integer) rulesetDto[4];
            String username = null;

            if (usernameMap.containsKey((Integer) rulesetDto[8])) {
                username = usernameMap.get((Integer) rulesetDto[8]);
            }

            dto.setRulesCategoryName((String) rulesetDto[0]);
            dto.setId((long) rulesetDto[1]);
            dto.setReference(reference);
            dto.setName((String) rulesetDto[3]);
            dto.setVersion(version);
            dto.setDate((Date) rulesetDto[5]);
            dto.setPublished(true);
            dto.setVersionName(generateVersionName(reference, version));
            dto.setComment((String) rulesetDto[6]);
            dto.setLanguage((String) rulesetDto[7]);
            dto.setUsername(username);

            rulesetDtoWrapper.setDto(dto);
            rulesetDtoWrapper.setState(State.ACTIVE.toString());

            return rulesetDtoWrapper;
        }

        private String generateVersionName(String reference, int version) {
            return reference + "_" + version;
        }
    }
}