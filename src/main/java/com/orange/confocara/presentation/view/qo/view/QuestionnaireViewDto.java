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

package com.orange.confocara.presentation.view.qo.view;

import static com.google.common.collect.Lists.newArrayList;
import static org.immutables.value.Value.Default;

import com.orange.confocara.connector.persistence.model.Clarification;
import com.orange.confocara.connector.persistence.model.CriteriaGroup;
import com.orange.confocara.connector.persistence.model.Criterion;
import com.orange.confocara.connector.persistence.model.SubChapter;
import com.orange.confocara.connector.persistence.model.WithId;
import com.orange.confocara.connector.persistence.model.WithName;
import com.orange.confocara.connector.persistence.model.WithReference;
import com.orange.confocara.connector.persistence.model.WithVersion;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.immutables.value.Value.Immutable;

@Immutable
public interface QuestionnaireViewDto {

    QuestionnaireView getQuestionnaire();

    List<String> getRulesets();

    List<ChainView> getChains();

    @Immutable
    interface QuestionnaireView extends WithId, WithName, WithReference, WithVersion {

        @Default
        default String getCategoryName() {
            return "";
        }

        @Default
        default String getEquipmentName() {
            return "";
        }

        @Default
        default String getAuthorName() {
            return "";
        }

        @Default
        default Date getLastUpdateDate() {
            return new Date();
        }

        @Default
        default String getState() {
            return "";
        }

        @Default
        default boolean getPublished() {
            return false;
        }

        @Default
        default long getQuestionsNb() {
            return 0;
        }

        @Default
        default long getRulesNb() {
            return 0;
        }

        static QuestionnaireView defaultQuestionnaireView() {
            return ImmutableQuestionnaireView
                    .builder()
                    .id(0L)
                    .name("<empty>")
                    .reference("<empty>")
                    .version(0)
                    .build();
        }
    }

    @Immutable
    interface ChainView extends WithId, WithName, WithReference {

        List<QuestionView> getQuestions();

        static ChainView from(SubChapter chain) {

            List<CriteriaGroup> groups = chain
                    .getCriteriaGroups();

            List<QuestionView> questions = groups
                    .stream()
                    .map(QuestionView::from)
                    .collect(Collectors.toList());

            return ImmutableChainView
                    .builder()
                    .id(chain.getId())
                    .name(chain.getName())
                    .reference(chain.getReference())
                    .questions(questions)
                    .build();
        }
    }

    @Immutable
    interface QuestionView extends WithId, WithName, WithReference {

        List<RuleView> getRules();

        static QuestionView from(CriteriaGroup q) {

            List<String> rulesOrder = q.getRulesOrder() != null
                    ? Arrays.asList(q.getRulesOrder().split(","))
                    : Collections.emptyList();

            List<RuleView> rules = q
                    .getRules()
                    .stream()
                    .map(RuleView::from)
                    .sorted((o1, o2) -> {
                        int left = rulesOrder.indexOf(o1.getReference());
                        int right = rulesOrder.indexOf(o2.getReference());
                        return Integer.compare(right, left);
                    })
                    .collect(Collectors.toList());

            return ImmutableQuestionView
                    .builder()
                    .id(q.getId())
                    .name(q.getLabel())
                    .reference(q.getReference())
                    .rules(rules)
                    .build();
        }
    }

    @Immutable
    interface RuleView extends WithId, WithName, WithReference {

        List<String> getCategories();

        List<IllustrationView> getIllustrations();

        static RuleView from(Criterion rule) {

            return ImmutableRuleView
                    .builder()
                    .id(rule.getId())
                    .name(rule.getLabel().replaceAll("\\r\\n|\\r|\\n", "<br />"))
                    .reference(rule.getReference())
                    .categories(
                            newArrayList(
                                    rule.getRuleCategoryName()))
                    .illustrations(
                            rule
                                .getIllustrations()
                                .stream()
                                .map(IllustrationView::from)
                                .collect(Collectors.toList())
                    )
                    .build();
        }
    }

    @Immutable
    interface IllustrationView {

        Long getIllustrationId();

        String getIllustrationTitle();

        String getIllustrationReference();

        String getIllustrationComment();

        String getImageName();

        static IllustrationView from(Clarification input) {

            return ImmutableIllustrationView
                    .builder()
                    .illustrationId(input.getId())
                    .illustrationReference(input.getReference())
                    .illustrationComment(input.getComment())
                    .illustrationTitle(input.getTitle())
                    .imageName(input.getImage() == null ? "" : input.getImage().getFileNameWithExtension())
                    .build();
        }
    }

}
