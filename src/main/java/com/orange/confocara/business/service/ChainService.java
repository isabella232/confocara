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
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChainService {

    private final ChainRepository chainRepository;
    private final QuestionnaireObjectRepository questionnaireObjectRepository;

    @Transactional
    public List<Chain> all() {
        List<Chain> chains = chainRepository.findAllByOrderByIdDesc();

        for (Chain chain : chains) {
            chain.orderQuestions();
        }

        return chains;
    }

    @Transactional
    public Chain withId(Long id) {
        return chainRepository.findOne(id);
    }

    @Transactional
    public Chain withReference(String reference) {
        return chainRepository.findByReference(reference);
    }

    @Transactional
    public List<Chain> withRulesCategory(RulesCategory rulesCategory) {
        return chainRepository.findByRulesCategory(rulesCategory);
    }

    @Transactional
    public List<Chain> withName(String name) {
        return chainRepository.withName(name.trim().replaceAll("\\s+", " "));
    }

    @Transactional
    public List<Chain> withIds(List<Long> ids) {
        return (List<Chain>) chainRepository.findAll(ids);
    }

    @Transactional
    public List<Chain> sortedWithIds(List<Long> ids) {
        List<Chain> chains = new ArrayList<>();
        for (Long id : ids) {
            chains.add(chainRepository.findOne(id));
        }
        return chains;
    }

    @Transactional
    public Chain create(@NonNull Chain chain) {
        return chainRepository.save(chain);
    }

    @Transactional
    public void delete(long id) {
        Chain chain = chainRepository.findOne(id);

        List<QuestionnaireObject> questionnaireList = questionnaireObjectRepository.findByChains(chain);
        for (QuestionnaireObject questionnaire : questionnaireList) {
            List<Chain> chains = questionnaire.getChains();
            chains.remove(chain);
        }

        chainRepository.delete(id);
    }

    @Transactional
    public Chain update(@NonNull Chain chain) {
        return chainRepository.save(chain);
    }
}
