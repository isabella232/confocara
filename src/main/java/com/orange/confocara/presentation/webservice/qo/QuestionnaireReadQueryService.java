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

package com.orange.confocara.presentation.webservice.qo;

import static com.orange.confocara.presentation.webservice.qo.QuestionnaireReadQueryService.Items.newItems;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import com.google.common.collect.Lists;
import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.immutables.value.Value;

/**
 * Behaviour of services that can query for {@link com.orange.confocara.connector.persistence.model.Rule}s
 */
@FunctionalInterface
public interface QuestionnaireReadQueryService {

    /**
     * Retrieves a {@link List} of rules
     *
     * @param questionnaire the arguments to look for.
     * @return a list of items
     */
    QuestionnaireResponse retrieveAllRules(QuestionnaireRequest questionnaire);

    static QuestionnaireReadQueryService instance(RuleDtoQueryRepository repository,
            RulesetQuestionnaireDtoQueryRepository queryRepository) {
        return new QuestionnaireReadQueryServiceImpl(repository, queryRepository);
    }

    @RequiredArgsConstructor
    @Slf4j
    final class QuestionnaireReadQueryServiceImpl implements QuestionnaireReadQueryService {

        private final RuleDtoQueryRepository ruleRepository;

        private final RulesetQuestionnaireDtoQueryRepository queryRepository;

        @Override
        public QuestionnaireResponse retrieveAllRules(
                QuestionnaireRequest request) {

            /*
             * One questionnaire can be shared by several rulesets.
             * First, we retrieve all of them.
             */
            List<RulesetQuestionnaireLightDto> foundQuestionnaires = queryRepository
                            .findAllByReferenceAndVersion(
                                    request.getReference(),
                                    request.getVersion());

            if (foundQuestionnaires.isEmpty()) {
                throw new BizException(ErrorCode.NOT_FOUND);
            }

            /*
             * Taking one of them as our questionnaire.
             * Except for the rulesetId, all the attributes should be the same.
             */
            RulesetQuestionnaireLightDto questionnaire = foundQuestionnaires.get(0);

            /*
             * Retrieving all the questionnaires that are related to the rulesets
             */
            Collection<RulesetQuestionnaireEnhancedDto> allQuestionnaires = queryRepository
                    .findAllByRulesetIds(foundQuestionnaires
                            .stream()
                            .map(RulesetQuestionnaireLightDto::getRulesetId)
                            .collect(toList()));

            /*
             * Converting elements to Items
             */
            Items rulesets = newItems(allQuestionnaires
                    .stream()
                    .map(Item::makeRulesetItem)
                    .collect(toSet()));

            Items otherQuestionnaires = newItems(allQuestionnaires
                    .stream()
                    .filter(q -> q.getQuestionnaireId() != questionnaire.getId())
                    .map(Item::makeQuestionnaireItem)
                    .collect(toSet()));

            List<RuleDto> allRules = ruleRepository.findAll(questionnaire.getId());

            Items rules = newItems(allRules
                    .stream()
                    .map(Item::makeRuleItem)
                    .collect(toSet()));

            Items questions = newItems(allRules
                    .stream()
                    .map(Item::makeQuestionItem)
                    .collect(toSet()));

            Items chains = newItems(allRules
                    .stream()
                    .map(Item::makeChainItem)
                    .collect(toSet()));

            /*
             * populating and returning the response object
             */
            return ImmutableQuestionnaireResponse
                    .builder()
                    .info(questionnaire)
                    .rulesets(rulesets)
                    .otherQuestionnaires(otherQuestionnaires)
                    .chains(chains)
                    .questions(questions)
                    .rules(rules)
                    .build();
        }
    }

    /**
     * a basic element of {@link QuestionnaireResponse}
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    class Item {

        private Long id;

        private String reference;

        private String name;

        private String type;

        @Builder.Default
        private Long parentId = null;

        @Builder.Default
        private boolean withIllustration = false;

        static Item makeRuleItem(RuleDto r) {
            return Item
                    .builder()
                    .id(r.getRuleId())
                    .reference(r.getRuleReference())
                    .name(r.getRuleName())
                    .type("rule")
                    .parentId(r.getQuestionId())
                    .withIllustration(r.getIllustrationsNb() > 0)
                    .build();
        }

        static Item makeQuestionItem(RuleDto r) {
            return Item
                    .builder()
                    .id(r.getQuestionId())
                    .reference(r.getQuestionReference())
                    .name(r.getQuestionName())
                    .type("question")
                    .parentId(r.getChainId())
                    .build();
        }

        static Item makeChainItem(RuleDto r) {
            return Item
                    .builder()
                    .id(r.getChainId())
                    .reference(r.getChainReference())
                    .name(r.getChainName())
                    .type("chain")
                    .parentId(r.getQuestionnaireId())
                    .build();
        }

        static Item makeRulesetItem(RulesetQuestionnaireEnhancedDto dto) {
            return Item
                    .builder()
                    .id(dto.getRulesetId())
                    .reference(dto.getRulesetReference())
                    .name(dto.getRulesetName())
                    .type("ruleset")
                    .build();
        }

        static Item makeQuestionnaireItem(RulesetQuestionnaireEnhancedDto dto) {
            return new Item(dto.getQuestionnaireId(), dto.getQuestionnaireReference(), dto.getQuestionnaireName(), "questionnaire",
                    dto.getRulesetId(), false);
        }
    }

    /**
     * a container for basic elements of {@link QuestionnaireResponse}
     */
    class Items {

        /**
         * the number of elements
         */
        private Integer count;

        /**
         * the bunch of elements
         */
        private Collection<Item> items;

        /**
         * instantiate
         *
         * @param elements a bunch of {@link Item}s
         */
        Items(Collection<Item> elements) {
            this.count = elements.size();
            this.items = elements;
        }

        public Integer getCount() {
            return count;
        }

        public Collection<Item> getItems() {
            return items;
        }

        /**
         * a factory
         *
         * @param items
         * @return an instance of {@link Items}
         */
        public static Items newItems(Collection<Item> items) {
            return new Items(items);
        }
    }

    /**
     * the response returned by {@link QuestionnaireReadWebService#rulesByQuestionnaire(String,
     * Integer)} when the request is successful
     */
    @Value.Immutable
    interface QuestionnaireResponse {

        /**
         * information about the queried questionnaire
         */
        RulesetQuestionnaireLightDto getInfo();

        /**
         * list of rulesets that use the questionnaire
         */
        Items getRulesets();

        /**
         * all the other questionnaires related to the rulesets
         */
        Items getOtherQuestionnaires();

        /**
         * list of chains that are in the questionnaire
         */
        Items getChains();

        /**
         * list of questions that are in the questionnaire
         */
        Items getQuestions();

        /**
         * list of rules that are in the questionnaire
         */
        Items getRules();

        static QuestionnaireResponse emptyResponse() {
            return ImmutableQuestionnaireResponse
                    .builder()
                    .info(ImmutableRulesetQuestionnaireLightDto
                            .builder()
                            .rulesetId(0L)
                            .alreadyPublished(true)
                            .id(0)
                            .name(randomAlphabetic(5))
                            .reference(randomAlphabetic(5))
                            .build())
                    .chains(new Items(Lists.newArrayList()))
                    .otherQuestionnaires(new Items(Lists.newArrayList()))
                    .questions(new Items(Lists.newArrayList()))
                    .rules(new Items(Lists.newArrayList()))
                    .rulesets(new Items(Lists.newArrayList()))
                    .build();
        }
    }
}
