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

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.connector.persistence.model.ByReference;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * see {@link SubObjectsPublishService#publishOneQuestionnaire(Long, List)}
 */
public class SubObjectsPublishServiceImplPublishOneQuestionnaireTest {

    private SubObjectsPublishService subject;

    private Predicate filter;

    private QuestionnaireWithSubObjectsPublisher publisher;

    private SubObjectsQueryService service;

    @Before
    public void setUp() {

        filter = mock(Predicate.class);
        publisher = mock(QuestionnaireWithSubObjectsPublisher.class);
        service = mock(SubObjectsQueryService.class);

        subject = SubObjectsPublishService.instance(filter, publisher, service);
    }

    @Test(expected = BizException.class)
    public void shouldThrowExceptionWhenQuestionnaireIdDoesNotExistInRepository() {

        // Given
        Long questionnaireId = nextLong();
        when(filter.test(Mockito.anyLong())).thenReturn(false);

        List<ByReference> equipments = newArrayList(mock(ByReference.class));

        // When
        subject.publishOneQuestionnaire(questionnaireId, equipments);

        // Then
    }

    @Test
    public void shouldApplyPublishingWhenQuestionnaireIdExistsInRepository() {

        // Given
        Long questionnaireId = nextLong();
        when(filter.test(Mockito.anyLong())).thenReturn(true);

        List<ByReference> equipments = newArrayList(mock(ByReference.class));

        // When
        subject.publishOneQuestionnaire(questionnaireId, equipments);

        // Then
        verify(service).assignSubObjects(questionnaireId, equipments);
        verify(publisher).publishQuestionnaire(questionnaireId);
        verify(publisher).draftQuestionnaire(questionnaireId);
    }
}