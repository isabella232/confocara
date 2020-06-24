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

package com.orange.confocara.presentation.webservice.rule;

import com.google.common.collect.Lists;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.presentation.webservice.rule.RuleSearchWebService.RuleSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.immutables.value.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Behaviour of a service dedicated to the searching for rules
 */
public interface RuleQueryService {

    /**
     * @param ruleSearchCriteria some search criteria
     * @param requestParams some pagination information
     * @return a sublist of a list of {@link RuleDto}
     */
    RulePageResponse retrieveRules(
            RuleSearchCriteria ruleSearchCriteria,
            Pageable requestParams);

    static RuleQueryService instance(RuleQueryRepository repository) {
        return new RuleQueryServiceImpl(repository);
    }

    @RequiredArgsConstructor
    class RuleQueryServiceImpl implements RuleQueryService {

        private final RuleQueryRepository queryRepository;

        @Override
        public RulePageResponse retrieveRules(
                RuleSearchCriteria ruleSearchCriteria,
                Pageable requestParams) {

            return formatResponse(queryRepository.findAll(ruleSearchCriteria, requestParams),
                    requestParams);
        }

        private RulePageResponse formatResponse(Page<Rule> input, Pageable params) {
            List<RuleDto> list = new ArrayList<>();
            for (Rule i : input
                    .getContent()) {
                String userName = "";
                if(i.getUser()!=null){
                    userName= i.getUser().getUsername();
                }
                ImmutableRuleDto build = ImmutableRuleDto
                        .builder()
                        .ruleId(i.getId())
                        .ruleReference(i.getReference())
                        .ruleName(i.getLabel())
                        .ruleCategoryName(i.getRuleCategoryName())
                        .authorName(userName)
                        .build();
                list.add(build);
            }
            return new RulePageResponse(list,
                    params,
                    input.getTotalElements());
        }
    }

    /**
     * an implementation of {@code Page}
     */
    class RulePageResponse extends PageImpl<RuleDto> {

        public RulePageResponse(
                List<RuleDto> content) {
            super(content);
        }

        public RulePageResponse(List<RuleDto> content, Pageable pageable, long totalElements) {
            super(content, pageable, totalElements);
        }

        public RulePageResponse() {
            super(Lists.newArrayList());
        }

        @Override
        public List<RuleDto> getContent() {
            return super.getContent();
        }

        @Override
        public int getNumber() {
            return super.getNumber();
        }

        @Override
        public int getSize() {
            return super.getSize();
        }

        @Override
        public int getNumberOfElements() {
            return super.getNumberOfElements();
        }

        @Override
        public Sort getSort() {
            return super.getSort();
        }

        @Override
        public boolean isLast() {
            return super.isLast();
        }

        @Override
        public boolean isFirst() {
            return super.isFirst();
        }
    }

    @Value.Immutable
    interface RuleDto {

        Long getRuleId();

        String getRuleReference();

        String getRuleName();

        String getRuleCategoryName();

        String getAuthorName();
    }
}
