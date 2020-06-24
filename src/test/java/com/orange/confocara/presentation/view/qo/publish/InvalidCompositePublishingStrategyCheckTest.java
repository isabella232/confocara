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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @see InvalidCompositePublishingStrategy#check(Long)
 */
public class InvalidCompositePublishingStrategyCheckTest {

    private InvalidCompositePublishingStrategy subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new InvalidCompositePublishingStrategy(repository);
    }


    @Test
    public void shouldReturnTrueWhenSubEquipmentsHaveMultipleQuestionnaires() {

        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList(mock(QuestionnaireObject.class), mock(QuestionnaireObject.class)));

        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        boolean result = subject.check(nextLong());

        // Then
        assertThat(result).isTrue();
    }


    @Test
    public void shouldReturnFalseWhenEquipmentHasNullSubEquipments() {

        // Given
        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(null);
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        boolean result = subject.check(nextLong());

        // Then
        assertThat(result).isFalse();
    }
    @Test
    public void shouldReturnFalseWhenEquipmentHasNoSubEquipments() {

        // Given
        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(null);
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        boolean result = subject.check(nextLong());

        // Then
        assertThat(result).isFalse();
    }


    @Test
    public void shouldReturnFalseWhenSubEquipmentsHaveNoQuestionnaires() {

        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList());

        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        boolean result = subject.check(nextLong());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenSubEquipmentsHaveOnlyOneQuestionnaire() {

        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList(mock(QuestionnaireObject.class)));

        QuestionnaireObject mock = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(mock.getEquipment()).thenReturn(equipment);

        when(repository.findOne(Mockito.anyLong())).thenReturn(mock);

        // When
        boolean result = subject.check(nextLong());

        // Then
        assertThat(result).isFalse();
    }
}