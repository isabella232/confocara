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

import com.orange.confocara.connector.persistence.model.Question;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditConjunction;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionRevisionQueryRepository {

    private final EntityManager entityManager;

    public boolean exists(Long questionId, Long revId) {

        AuditConjunction conjunction = AuditEntity
                .conjunction()
                .add(AuditEntity.id().eq(questionId))
                .add(AuditEntity.revisionNumber().eq(revId));

        Object question = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forEntitiesAtRevision(Question.class, revId)
                .add(conjunction)
                .getSingleResult();

        return question != null && question instanceof Question;
    }

    public Question findOne(Long questionId, Long revId) {
        AuditConjunction conjunction = AuditEntity
                .conjunction()
                .add(AuditEntity.id().eq(questionId))
                .add(AuditEntity.revisionNumber().eq(revId));

        Object question = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forEntitiesAtRevision(Question.class, revId)
                .add(conjunction)
                .getSingleResult();

        return (Question) question;
    }

    public List<Question> findAll(Long revId) {

        List<Object> questions = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forEntitiesAtRevision(Question.class, revId)
                .getResultList();

        return questions
                .stream()
                .filter(q -> q != null && q instanceof Question)
                .map(q -> (Question) q)
                .collect(Collectors.toList());
    }
}
