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

package com.orange.confocara.presentation.webservice.subobject;

import com.google.common.collect.ImmutableSet;
import com.orange.confocara.connector.persistence.model.ByReference;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Behaviour of services that retrieve a bunch of {@link EquipmentDto}s
 */
interface SubObjectsQueryService {

    /**
     * @param questionnaireId an identifier for a {@link QuestionnaireObject}
     */
    List<EquipmentDto> retrieveSubObjects(Long questionnaireId);

    void assignSubObjects(Long targetId, List<ByReference> subObjectsReferences);

    static SubObjectsQueryService instance(
            QuestionnaireObjectRepository repository,
            Function<Equipment, EquipmentDto> mapper) {

        return new SubObjectsQueryServiceImpl(repository, mapper);
    }

    /**
     * Default implementation of {@link SubObjectsQueryService}
     */
    @Slf4j
    @RequiredArgsConstructor
    class SubObjectsQueryServiceImpl implements SubObjectsQueryService {

        private final QuestionnaireObjectRepository repository;

        private final Function<Equipment, EquipmentDto> mapper;

        @Override
        @Transactional(readOnly = true)
        public List<EquipmentDto> retrieveSubObjects(Long questionnaireId) {

            QuestionnaireObject qoToPublish = repository.findOne(questionnaireId);

            return makeList(qoToPublish, dto -> dto.getQuestionnairesRefs().size() > 1);
        }

        @Override
        @Transactional
        public void assignSubObjects(Long targetId, List<ByReference> associatedSubObjects) {
            QuestionnaireObject target = repository.findOne(targetId);

            // retrieving all the subObjects that were not shown to the user
            List<QuestionnaireObject> singleSubObjects = makeList(target, dto -> dto.getQuestionnairesRefs().size() == 1)
                    .stream()
                    .map(dto -> dto.getQuestionnairesRefs().get(0))
                    .map(repository::findByReference)
                    .collect(Collectors.toList());

            // retrieving all the subObjects that were selected by the user
            List<QuestionnaireObject> subObjects = associatedSubObjects
                    .stream()
                    .map(equipment -> repository.findByReference(equipment.getReference()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // merging the 2 lists into a single set, so as to have only unique elements
            // the class ImmutableSet.builder() is only used as a convenience tool
            List<QuestionnaireObject> list = ImmutableSet
                    .<QuestionnaireObject>builder()
                    .addAll(singleSubObjects)
                    .addAll(subObjects)
                    .build()
                    .asList();

            // assigning the subObjects to the given questionnaire
            target.setQuestionnaireSubObjects(list);
        }

        /**
         * Retrieves a bunch of subObjects that match the given arguments
         *
         * @param qo a  {@link QuestionnaireObject}
         * @param filter a {@link Predicate}
         * @return a {@link List} of the {@link EquipmentDto}s related to the given {@link QuestionnaireObject} that fit the {@link Predicate}
         */
        private List<EquipmentDto> makeList(QuestionnaireObject qo, Predicate<EquipmentDto> filter) {
            return qo
                    .getEquipment()
                    .getSubobjects()
                    .stream()
                    // transform the list of subobjects into equipments + questionnairesRefs
                    .map(mapper)
                    // only render the equipments that have 2 or more subobjects.
                    .filter(filter)
                    .collect(Collectors.toList());
        }
    }
}
