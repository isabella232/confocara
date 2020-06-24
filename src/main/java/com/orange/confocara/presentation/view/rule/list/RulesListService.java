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

package com.orange.confocara.presentation.view.rule.list;

import com.google.common.collect.Lists;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.immutables.value.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Behaviour of a service dedicated to the searching for rules
 */
public interface RulesListService {

    /**
     * @return a sublist of a list of {@link RuleDto}
     */
    Page<RuleDto> retrieveAllRules();

    static RulesListService instance(
            RuleRepository repository) {
        return new RulesListServiceImpl(repository);
    }

    @RequiredArgsConstructor
    class RulesListServiceImpl implements
            RulesListService {

        private final RuleRepository queryRepository;

        @Override
        public Page<RuleDto> retrieveAllRules() {

            return formatResponse(queryRepository.findAllByOrderByIdDesc());
        }

        private Page<RuleDto> formatResponse(Iterable<Rule> items) {

            return new PageImpl<>(Lists
                    .newArrayList(items)
                    .stream()
                    .map(RuleDto::from)
                    .collect(Collectors.toList()));
        }
    }

    @Value.Immutable
    interface RuleDto {

        Long getRuleId();

        String getRuleReference();

        String getRuleName();

        String getRuleDescription();

        String getRuleCategoryName();

        String getAuthorName();

        List<RuleIllustrationDto> getRuleIllustrations();

        List<RuleImpactDto> getRuleImpacts();

        Date getDate();

        static RuleDto from(Rule input) {
            String userName = "";
            if(input.getUser()!=null){
                userName= input.getUser().getUsername();
            }
            return ImmutableRuleDto
                    .builder()
                    .ruleId(input.getId())
                    .ruleReference(input.getReference())
                    .ruleName(input.getLabel())
                    .ruleDescription(input.getOrigin())
                    .ruleCategoryName(input.getRuleCategoryName())
                    .authorName(userName)
                    .date(input.getDate())
                    .ruleImpacts(input
                            .getRuleImpacts()
                            .stream()
                            .map(RuleImpactDto::from)
                            .collect(Collectors.toList()))
                    .ruleIllustrations(input
                            .getIllustrations()
                            .stream()
                            .map(RuleIllustrationDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Value.Immutable
    interface RuleIllustrationDto {

        String getTitle();

        String getFileName();

        boolean getIllustrated();

        static RuleIllustrationDto from(Illustration input) {
            return ImmutableRuleIllustrationDto
                    .builder()
                    .title(input.getTitle())
                    .illustrated(input.getImage() != null)
                    .fileName(input.getImage() == null ? "" : input.getImage().getFileNameWithExtension())
                    .build();
        }
    }

    @Value.Immutable
    interface RuleImpactDto {

        String getProfileTypeName();

        String getImpactName();

        static RuleImpactDto from(RuleImpact input) {
            return ImmutableRuleImpactDto
                    .builder()
                    .profileTypeName(input.getProfileType().getName())
                    .impactName(input.getImpact().getName())
                    .build();
        }
    }
}
