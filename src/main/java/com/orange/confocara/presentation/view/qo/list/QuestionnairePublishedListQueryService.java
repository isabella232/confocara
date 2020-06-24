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

import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.impl.QuestionnaireDtoImpl;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

@FunctionalInterface
public interface QuestionnairePublishedListQueryService {

    List<QuestionnaireDtoWrapper> getPublishedQuestionnairesDtoWrappers();

    static QuestionnairePublishedListQueryService instance(EntityManager entityManager) {
        return new QuestionnairePublishedListQueryServiceImpl(entityManager);
    }

    @RequiredArgsConstructor
    @Slf4j
    final class QuestionnairePublishedListQueryServiceImpl implements QuestionnairePublishedListQueryService {

        private static final String PUBLISHED_PROPERTY = "published";
        private static final String REFERENCE_PROPERTY = "reference";

        private final EntityManager entityManager;

        /**
         * Generates the QuestionnaireDtoWrapper of all published questionnaires
         *
         * @return the QuestionnaireDtoWrapper of all published questionnaires
         */
        public List<QuestionnaireDtoWrapper> getPublishedQuestionnairesDtoWrappers() {
            // get the list of published questionnaires revision number
            List publishedQORevisionsQueryResult = getPublishedQuestionnairesVersions();

            // now, for each published questionnaire, we need to get dto properties
            // we use forEntitiesAtRevision because JOIN are not available with forRevisionsOfEntity
            return getQuestionnaireDtoWrappers(publishedQORevisionsQueryResult);
        }

        /**
         * Gets revision numbers of published questionnaires
         *
         * @return revision numbers of published questionnaires
         */
        private List getPublishedQuestionnairesVersions() {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forRevisionsOfEntity(QuestionnaireObject.class, false, false)
                    .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                    .addProjection(AuditEntity.revisionNumber())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }

        /**
         * generates the questionnaires dto wrappers containing all the needed information of given questionnaires
         *
         * @param publishedQORevisionsQueryResult all revision nb for published questionnaires
         * @return the QuestionnaireDtoWrappers
         */
        private List<QuestionnaireDtoWrapper> getQuestionnaireDtoWrappers(List publishedQORevisionsQueryResult) {
            List<QuestionnaireDtoWrapper> publishedQuestionnaires = new ArrayList<>();

            for (Object qoObject : publishedQORevisionsQueryResult) {
                int questionnaireObjectRevNb = (int) qoObject;

                List publishedQODtoQueryResult = getQoPropertiesResultList(questionnaireObjectRevNb);

                List publishedQODtoUsernameQueryResult = getQOUsernameResultList(questionnaireObjectRevNb);

                Map<Integer, String> usernameMap = new HashMap<>();

                for (Object usernameArray : publishedQODtoUsernameQueryResult) {
                    if (usernameArray instanceof Object[]) {
                        usernameMap.put((Integer) ((Object[]) usernameArray)[0], (String) ((Object[]) usernameArray)[1]);
                    }
                }

                for (Object questionnaireDto : publishedQODtoQueryResult) {
                    if (questionnaireDto instanceof Object[]) {
                        QuestionnaireDtoWrapper questionnaire = getQuestionnaireDtoWrapper(usernameMap, (Object[]) questionnaireDto);

                        publishedQuestionnaires.add(questionnaire);
                    }
                }
            }

            return publishedQuestionnaires;
        }


        /**
         * select questionnaire's properties that must be displayed on questionnaire's page
         *
         * @param questionnaireObjectRevNb the revision nb
         * @return the questionnaire properties
         */
        private List getQoPropertiesResultList(int questionnaireObjectRevNb) {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forEntitiesAtRevision(QuestionnaireObject.class, questionnaireObjectRevNb)
                    .traverseRelation("equipment", JoinType.INNER)
                    .addProjection(AuditEntity.property("name"))
                    .up()
                    .traverseRelation("rulesCategory", JoinType.INNER)
                    .addProjection(AuditEntity.property("name"))
                    .up()
                    .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                    .addProjection(AuditEntity.property("id"))
                    .addProjection(AuditEntity.property(REFERENCE_PROPERTY))
                    .addProjection(AuditEntity.property("name"))
                    .addProjection(AuditEntity.property("version"))
                    .addProjection(AuditEntity.property("date"))
                    .addProjection(AuditEntity.revisionNumber())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }


        /**
         * Returns QuestionnaireDtoWrapper with query result
         *
         * @param usernameMap      the user usernames mapped by Questionnaire revision number
         * @param questionnaireDto query result
         * @return QuestionnaireDtoWrapper
         */
        private QuestionnaireDtoWrapper getQuestionnaireDtoWrapper(Map<Integer, String> usernameMap, Object[] questionnaireDto) {
            QuestionnaireDtoWrapper questionnaire = new QuestionnaireDtoWrapper();
            QuestionnaireDtoImpl dto = new QuestionnaireDtoImpl();
            String reference = (String) questionnaireDto[3];
            Integer version = (Integer) questionnaireDto[5];
            String username = null;

            if (usernameMap.containsKey((Integer) questionnaireDto[7])) {
                username = usernameMap.get((Integer) questionnaireDto[7]);
            }

            dto.setEquipmentName((String) questionnaireDto[0]);
            dto.setRulesCategoryName((String) questionnaireDto[1]);
            dto.setId((long) questionnaireDto[2]);
            dto.setReference(reference);
            dto.setName((String) questionnaireDto[4]);
            dto.setVersion(version);
            dto.setDate((Date) questionnaireDto[6]);
            dto.setPublished(true);
            dto.setVersionName(generateVersionName(reference, version));
            dto.setUsername(username);

            questionnaire.setDto(dto);
            questionnaire.setState(State.ACTIVE);
            questionnaire.setPublishingState(PublishingState.PUBLISHED);

            return questionnaire;
        }

        private String generateVersionName(String reference, int version) {
            return reference + "_" + version;
        }


        /**
         * Returns the questionnaire username
         *
         * @param questionnaireObjectRevNb the revision nb
         * @return the questionnaire username
         */
        private List getQOUsernameResultList(int questionnaireObjectRevNb) {
            return AuditReaderFactory.get(entityManager).createQuery()
                    .forEntitiesAtRevision(QuestionnaireObject.class, questionnaireObjectRevNb)
                    .addProjection(AuditEntity.revisionNumber())
                    .traverseRelation("user", JoinType.LEFT)
                    .addProjection(AuditEntity.property("username"))
                    .up()
                    .add(AuditEntity.property("user").isNotNull())
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();
        }

    }
}
