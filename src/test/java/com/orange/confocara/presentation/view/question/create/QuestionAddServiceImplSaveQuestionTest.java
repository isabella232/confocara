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

package com.orange.confocara.presentation.view.question.create;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import com.orange.confocara.presentation.view.question.create.QuestionAddService.QuestionAddServiceImpl;
import java.security.Principal;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @see QuestionAddServiceImpl#saveQuestion(QuestionCreateDto, Principal)
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionAddServiceImplSaveQuestionTest {

    @InjectMocks
    private QuestionAddServiceImpl subject;

    @Mock
    private UserService userService;

    @Mock
    private QuestionService questionService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private QuestionRulesQueryService questionRulesQueryService;

    @Mock
    private RulesCategoryService rulesCategoryService;

    @Captor
    private ArgumentCaptor<Question> questionArgCaptor;

    @Test
    public void shouldConfigureEntityBeforeCreatingIt() {

        // Given
        String expectedLabel = randomAlphabetic(10);

        Long subjectId = nextLong();
        String subjectName = randomAlphabetic(10);
        Subject expectedSubject = expectedSubject(subjectId, subjectName);

        String authorName = randomAlphabetic(10);
        User expectedAuthor = expectedUser(authorName);

        String ruleReference = randomAlphanumeric(5);
        Rule expectedRule = expectedRule(ruleReference);

        Long ruleCategory = nextLong();
        RulesCategory expectedCategory = expectedCategory(ruleCategory);

        QuestionCreateDto input = QuestionCreateDto
                .builder()
                .subject(Subject.builder().id(subjectId).build())
                .rulesCategory(RulesCategory.builder().id(ruleCategory).build())
                .ruleIds(newArrayList(ruleReference))
                .orderedRuleIds(newArrayList(ruleReference))
                .label(expectedLabel)
                .build();

        Principal author = () -> authorName;

        // When
        subject.saveQuestion(input, author);

        // Then
        verify(questionService).create(questionArgCaptor.capture());

        List<Question> capturedQuestions = questionArgCaptor.getAllValues();
        assertThat(capturedQuestions).isNotEmpty();
        assertThat(capturedQuestions.size()).isEqualTo(1);

        Question item = capturedQuestions.get(0);
        assertThat(item.getLabel()).isEqualTo(expectedLabel);
        assertThat(item.getSubject()).isEqualTo(expectedSubject);
        assertThat(item.getUser()).isEqualTo(expectedAuthor);
        assertThat(item.getRulesCategory()).isEqualTo(expectedCategory);
        assertThat(item.getRules()).contains(expectedRule);
        assertThat(item.getRulesOrder()).contains(ruleReference);

    }

    User expectedUser(String authorName) {
        User user = new User();
        user.setUsername(authorName);
        when(userService.getUserByUsername(authorName)).thenReturn(user);
        return user;
    }

    RulesCategory expectedCategory(Long id) {
        RulesCategory category = new RulesCategory();
        category.setId(id);
        when(rulesCategoryService.withId(id)).thenReturn(category);
        return category;
    }

    Rule expectedRule(String ref) {
        Rule rule = new Rule();
        rule.setReference(ref);

        when(questionRulesQueryService.retrieveOneRuleByReference(ref)).thenReturn(rule);

        return rule;
    }

    Subject expectedSubject(Long id, String name) {
        Subject subject = new Subject();
        subject.setId(id);
        subject.setName(name);

        when(subjectService.withId(id)).thenReturn(subject);

        return subject;
    }
}