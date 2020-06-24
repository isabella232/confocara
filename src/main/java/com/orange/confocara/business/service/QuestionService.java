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

import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final ChainRepository chainRepository;
    private final QuestionRepository questionRepository;
    private final RuleRepository ruleRepository;

    @Transactional
    public List<Question> all() {
        return questionRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Question withId(Long id) {
        return questionRepository.findOne(id);
    }

    @Transactional
    public List<Question> withIds(@NonNull List<Long> ids) {
        return (List<Question>) questionRepository.findAll(ids);
    }

    @Transactional
    public List<Question> sortedWithIds(@NonNull List<Long> ids) {
        List<Question> questions = new ArrayList<>();
        for (Long id : ids) {
            questions.add(questionRepository.findOne(id));
        }
        return questions;
    }

    @Transactional
    public List<Question> withRulesCategory(RulesCategory rulesCategory) {
        return questionRepository.findByRulesCategory(rulesCategory);
    }

    @Transactional
    public Question withReference(@NonNull String reference) {
        if (questionRepository.findByReference(reference) != null) {
            return questionRepository.findByReference(reference);
        } else {
            return null;
        }
    }

    @Transactional
    public Question create(@NonNull Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    public void delete(long id) {
        Question question = questionRepository.findOne(id);
        List<Chain> chainList = chainRepository.findByQuestions(question);

        for (Chain chain : chainList) {
            List<Question> questions = chain.getQuestions();
            if (questions.contains(question)) {
                questions.remove(question);
            }
        }

        List<Rule> rules = ruleRepository.findByQuestion(question);
        for (Rule item : rules) {
            List<Question> questions = item.getQuestion();
            if (questions.contains(question)) {
                questions.remove(question);
            }
        }

        questionRepository.delete(id);
    }

    @Transactional
    public Question update(@NonNull Question question) {
        return questionRepository.save(question);
    }
}
