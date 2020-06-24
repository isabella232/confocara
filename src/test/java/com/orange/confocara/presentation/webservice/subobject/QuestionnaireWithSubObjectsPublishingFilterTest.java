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

import static org.apache.commons.lang3.RandomUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectReadRepository;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @see QuestionnaireWithSubObjectsPublishingFilter#test(Long)
 */
public class QuestionnaireWithSubObjectsPublishingFilterTest {

    private QuestionnaireWithSubObjectsPublishingFilter subject;

    private QuestionnaireObjectReadRepository repository;

    @Before
    public void setUp() {
        repository = mock(QuestionnaireObjectReadRepository.class);

        subject = new QuestionnaireWithSubObjectsPublishingFilter(repository);
    }

    @Test
    public void shouldReturnFalseWhenNoEntityMatchesGivenId() {
        // Given
        when(repository.exists(anyLong())).thenReturn(false);

        // When
        boolean result = subject.test(nextLong());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenEntityMatchesGivenIdWithMultipleSubQuestionnaires() {
        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList(mock(QuestionnaireObject.class), mock(QuestionnaireObject.class)));

        QuestionnaireObject entity = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(entity.getEquipment()).thenReturn(equipment);

        when(repository.exists(anyLong())).thenReturn(false);
        when(repository.findOne(anyLong())).thenReturn(entity);

        // When
        boolean result = subject.test(nextLong());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenEntityMatchesGivenIdWithNoSubEquipments() {
        // Given
        QuestionnaireObject entity = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList());
        when(entity.getEquipment()).thenReturn(equipment);

        when(repository.exists(anyLong())).thenReturn(false);
        when(repository.findOne(anyLong())).thenReturn(entity);

        // When
        boolean result = subject.test(nextLong());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenEntityMatchesGivenIdWithOnlySingleSubQuestionnaires() {
        // Given
        when(repository.findByEquipmentName(anyString()))
                .thenReturn(newArrayList(mock(QuestionnaireObject.class)));

        QuestionnaireObject entity = mock(QuestionnaireObject.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getSubobjects()).thenReturn(newArrayList(mock(Equipment.class)));
        when(entity.getEquipment()).thenReturn(equipment);

        when(repository.exists(anyLong())).thenReturn(false);
        when(repository.findOne(anyLong())).thenReturn(entity);

        // When
        boolean result = subject.test(nextLong());

        // Then
        assertThat(result).isFalse();
    }
}