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

package com.orange.confocara.connector.persistence.model;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * see {@link Ruleset#markAsNewDraft()}
 */
public class RulesetMarkAsNewDraftTest {

    @Test
    public void shouldIncrementVersionNumberAndUnpublishRuleset() {
        // Given
        Integer version = nextInt(0, 10);
        Ruleset ruleset = givenRuleset(version, nextBoolean());

        // When
        ruleset.markAsNewDraft();

        // Then
        assertThat(ruleset.getVersion()).isEqualTo(version + 1);
        assertThat(ruleset.isPublished()).isFalse();
    }

    Ruleset givenRuleset(Integer currentVersion, boolean isPublished) {
        return Ruleset.builder().version(currentVersion).published(isPublished).build();
    }
}