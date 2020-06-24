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
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public List<Subject> all() {
        return (List<Subject>) subjectRepository.findAll();
    }

    @Transactional
    public Subject withId(long id) {
        return subjectRepository.findOne(id);
    }

    @Transactional
    public Subject getSubjectByName(String name) {
        return subjectRepository.findByName(name);
    }

    @Transactional
    public List<Subject> withName(String name) {
        return subjectRepository.filterWithName(name);
    }

    @Transactional
    public boolean isAvailable(String name) {
        Subject subject = subjectRepository.findByName(name.trim().replaceAll("\\s+", " "));
        return subject == null;
    }

    @Transactional
    public Subject create(@NonNull Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public List<String> deleteSubject(long id) {
        List<String> conflictualQuestions = new ArrayList<>();
        Subject subject = subjectRepository.findOne(id);

        List<Question> questionsBySubject = questionRepository.findBySubject(subject);
        for (Question question : questionsBySubject) {
            conflictualQuestions.add(question.getReference());
        }

        if (questionsBySubject.isEmpty()) {
            subjectRepository.delete(id);
        }

        return conflictualQuestions;
    }

    @Transactional
    public Subject update(Subject subject) {
        return subjectRepository.save(subject);
    }
}
