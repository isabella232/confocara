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

package com.orange.confocara.presentation.view.question.common;

import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * behaivour of a service dedicated to querying for {@link Rule}s
 */
public interface QuestionRulesQueryService {

    List<RuleViewDto> allRules();

    Rule retrieveOneRuleByReference(String reference);

    static QuestionRulesQueryService instance(RuleRepository repository) {
        return new QuestionRulesQueryServiceImpl(repository);
    }

    @Slf4j
    @RequiredArgsConstructor
    @Transactional(readOnly = true)
    class QuestionRulesQueryServiceImpl implements QuestionRulesQueryService {

        private final RuleRepository ruleRepository;

        public List<RuleViewDto> allRules() {

            return ruleRepository
                    .findAllByOrderByIdDesc()
                    .stream()
                    .map(RuleViewDto::from)
                    .collect(Collectors.toList());
        }

        @Override
        public Rule retrieveOneRuleByReference(String label) {
            Rule output = ruleRepository.findByReference(label);

            log.info("Message=Retrieving one rule;RuleReference={};FoundId={}", label, output != null ? output.getId() : null);
            return output;
        }
    }
}
