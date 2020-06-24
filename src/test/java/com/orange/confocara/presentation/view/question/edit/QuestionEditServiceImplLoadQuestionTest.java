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

package com.orange.confocara.presentation.view.question.edit;

import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RuleService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import com.orange.confocara.presentation.view.question.edit.QuestionEditService.QuestionEditServiceImpl;
import java.security.Principal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

/**
 * @see QuestionEditServiceImpl#loadQuestion(long, Principal, Model) (
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionEditServiceImplLoadQuestionTest {

    @InjectMocks
    private QuestionEditServiceImpl subject;

    @Mock
    private UserService userService;

    @Mock
    private QuestionService questionService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private QuestionRulesQueryService questionRulesQueryService;

    @Mock
    private RuleService ruleService;


    @Test
    public void shouldDoSomething() {

        // Given
        Question expectedEntity = expectedEntity(RandomUtils.nextLong());

        Principal inputAuthor = () -> RandomStringUtils.randomAlphabetic(10);

        Model outputModel = Mockito.mock(Model.class);

        // When
        subject.loadQuestion(expectedEntity.getId(), inputAuthor, outputModel);

        // Then
    }

    Question expectedEntity(Long id) {
        Question entity = new Question();
        entity.setId(id);
        when(questionService.withId(id)).thenReturn(entity);
        return entity;
    }
}