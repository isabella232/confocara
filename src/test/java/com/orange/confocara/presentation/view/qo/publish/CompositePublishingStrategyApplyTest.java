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

package com.orange.confocara.presentation.view.qo.publish;

import static org.apache.commons.lang3.RandomUtils.*;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * @see CompositePublishingStrategy#apply(QuestionnaireObject)
 */
public class CompositePublishingStrategyApplyTest {

    private CompositePublishingStrategy subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new CompositePublishingStrategy(repository);
    }

    @Test
    public void shouldAssignQuestionnairesSubObjectAndApplyPublishState() {

        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList(mock(QuestionnaireObject.class)));

        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        subject.apply(nextLong());

        // Then
        verify(mock).setQuestionnaireSubObjects(Matchers.anyList());
        verify(mock).markAsPublished();
    }
}