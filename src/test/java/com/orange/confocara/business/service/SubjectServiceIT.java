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

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class SubjectServiceIT {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    RulesCategoryService rulesCategoryService;

    @Autowired
    ImpactValueService impactValueService;

    @Test
    public void createSubjectTest() {
        createSubject();

        List<Subject> subjectList = subjectService.all();
        Assert.assertTrue(!subjectList.isEmpty());
        Assert.assertTrue(subjectList.size() == 1);
    }

    @Test
    public void updateSubjectTest() {
        Subject subject = new Subject();
        subject.setName("newName");
        subjectService.update(subject);

        List<Subject> subjectList = subjectService.all();
        Assert.assertTrue(subjectList.size() == 1);
        Assert.assertTrue(subjectList.get(0).getName().equals("newName"));
    }

    @Test
    public void deleteSubject() {
        Subject subject = createSubject();
        subjectService.deleteSubject(subject.getId());

        List<Subject> subjectList = subjectService.all();
        Assert.assertTrue(subjectList.isEmpty());
    }

    @Test
    public void deleteSubjectWithQuestions() {
        Subject subject = createSubject();
        createQuestion(subject);
        List<String> conflictualQuestions = subjectService.deleteSubject(subject.getId());

        Assert.assertFalse(conflictualQuestions.isEmpty());

        List<Subject> subjectList = subjectService.all();
        Assert.assertNotNull(subjectList);
    }

    private Subject createSubject() {
        Subject subject = new Subject();
        subject.setName("name");
        return subjectService.create(subject);
    }

    private Question createQuestion(Subject subject) {
        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("name", impactValueService, rulesCategoryService);

        Question question = new Question();
        question.setReference("ref5");
        question.setLabel("label01");
        question.setState("state");
        question.setSubject(subject);
        question.setRulesCategory(rulesCategory);

        return questionService.create(question);
    }
}
