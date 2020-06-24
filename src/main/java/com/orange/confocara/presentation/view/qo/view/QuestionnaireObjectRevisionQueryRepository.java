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

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
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
public class QuestionnaireObjectRevisionQueryRepository {

    private final EntityManager entityManager;

    public boolean exists(String reference, Integer version) {

        AuditConjunction conjunction = AuditEntity
                .conjunction()
                .add(AuditEntity.property("reference").eq(reference))
                .add(AuditEntity.property("version").eq(version));

        Object qo = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(Question.class, true, false)
                .add(conjunction)
                .getSingleResult();

        return qo != null && qo instanceof QuestionnaireObject;
    }

    public QuestionnaireObject findOne(String reference, Integer version) {
        AuditConjunction conjunction = AuditEntity
                .conjunction()
                .add(AuditEntity.property("reference").eq(reference))
                .add(AuditEntity.property("version").eq(version));

        Object qo = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(Question.class, true, false)
                .add(conjunction)
                .getSingleResult();

        return (QuestionnaireObject) qo;
    }
}
