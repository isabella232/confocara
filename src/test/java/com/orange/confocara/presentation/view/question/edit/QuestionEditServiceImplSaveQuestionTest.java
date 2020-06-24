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

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RuleService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import com.orange.confocara.presentation.view.question.edit.QuestionEditService.QuestionEditServiceImpl;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @see QuestionEditServiceImpl#saveQuestion(Long, QuestionEditDto, Principal)
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionEditServiceImplSaveQuestionTest {

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

    @Captor
    private ArgumentCaptor<Question> questionArgCaptor;

    @Test
    public void shouldConfigureEntityBeforeUpdatingItWhenEntityHasRules() {

        // Given
        Long expectedQuestionId = nextLong();
        Question expectedQuestion = expectedEntity(expectedQuestionId);

        String subjectName = randomAlphabetic(10);
        Subject expectedSubject = expectedSubject(subjectName);

        String authorName = randomAlphabetic(10);
        User expectedAuthor = expectedUser(authorName);

        String ruleReference = randomAlphanumeric(5);
        Rule expectedRule = expectedRule(ruleReference);

        QuestionEditDto input = QuestionEditDto
                .builder()
                .subject(Subject.builder().name(subjectName).build())
                .ruleIds(newArrayList(ruleReference))
                .orderedRuleIds(newArrayList(ruleReference))
                .build();

        Principal author = () -> authorName;

        // When
        subject.saveQuestion(expectedQuestionId, input, author);

        // Then
        verify(ruleService).update(expectedRule);

        verify(questionService).update(questionArgCaptor.capture());

        List<Question> capturedQuestions = questionArgCaptor.getAllValues();
        assertThat(capturedQuestions).isNotEmpty();
        assertThat(capturedQuestions.size()).isEqualTo(1);

        Question item = capturedQuestions.get(0);
        assertThat(item.getId()).isEqualTo(expectedQuestionId);
        assertThat(item.getSubject()).isEqualTo(expectedSubject);
        assertThat(item.getUser()).isEqualTo(expectedAuthor);
        assertThat(item.getRules()).contains(expectedRule);
        assertThat(item.getRulesOrder()).contains(ruleReference);
        assertThat(item.getState()).isEqualTo(State.ACTIVE.toString().toLowerCase());
    }


    @Test
    public void shouldConfigureEntityBeforeUpdatingItWhenEntityHasNoRules() {

        // Given
        Long expectedQuestionId = nextLong();
        Question expectedQuestion = expectedEntity(expectedQuestionId);

        String subjectName = randomAlphabetic(10);
        Subject expectedSubject = expectedSubject(subjectName);

        String authorName = randomAlphabetic(10);
        User expectedAuthor = expectedUser(authorName);

        QuestionEditDto input = QuestionEditDto
                .builder()
                .subject(Subject.builder().name(subjectName).build())
                .ruleIds(Collections.emptyList())
                .orderedRuleIds(Collections.emptyList())
                .build();

        Principal author = () -> authorName;

        // When
        subject.saveQuestion(expectedQuestionId, input, author);

        // Then
        verify(ruleService, Mockito.never()).update(Matchers.any());

        verify(questionService).update(questionArgCaptor.capture());

        List<Question> capturedQuestions = questionArgCaptor.getAllValues();
        assertThat(capturedQuestions).isNotEmpty();
        assertThat(capturedQuestions.size()).isEqualTo(1);

        Question item = capturedQuestions.get(0);
        assertThat(item.getId()).isEqualTo(expectedQuestionId);
        assertThat(item.getSubject()).isEqualTo(expectedSubject);
        assertThat(item.getUser()).isEqualTo(expectedAuthor);
        assertThat(item.getRules()).isEmpty();
        assertThat(item.getRulesOrder()).isEqualTo("");
        assertThat(item.getState()).isEqualTo(State.INACTIVE.toString().toLowerCase());
    }

    Question expectedEntity(Long id) {
        Question entity = new Question();
        entity.setId(id);
        when(questionService.withId(id)).thenReturn(entity);
        return entity;
    }

    User expectedUser(String authorName) {
        User user = new User();
        user.setUsername(authorName);
        when(userService.getUserByUsername(authorName)).thenReturn(user);
        return user;
    }

    Rule expectedRule(String ref) {
        Rule rule = new Rule();
        rule.setReference(ref);

        when(questionRulesQueryService.retrieveOneRuleByReference(ref)).thenReturn(rule);

        return rule;
    }

    Subject expectedSubject(String name) {
        Subject subject = new Subject();
        subject.setName(name);

        when(subjectService.getSubjectByName(name)).thenReturn(subject);

        return subject;
    }
}