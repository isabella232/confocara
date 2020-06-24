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

import com.orange.confocara.connector.persistence.model.Criterion;
import com.orange.confocara.connector.persistence.model.WithId;
import com.orange.confocara.connector.persistence.model.WithLabel;
import com.orange.confocara.connector.persistence.model.WithReference;
import com.orange.confocara.connector.persistence.model.WithRuleCategoryName;
import org.immutables.value.Value.Immutable;

/** description of a DTO */
@Immutable
interface RuleViewDto extends WithId, WithLabel, WithReference, WithRuleCategoryName {

    /**
     * Transforms a {@link Criterion} into a {@link RuleViewDto}
     *
     * @param rule a {@link Criterion}
     * @return a {@link RuleViewDto}
     */
    static RuleViewDto from(Criterion rule) {
        return ImmutableRuleViewDto
                .builder()
                .id(rule.getId())
                .reference(rule.getReference())
                .label(rule.getLabel())
                .ruleCategoryName(rule.getRuleCategoryName())
                .build();
    }
}
