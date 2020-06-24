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
 * see {@link Ruleset#markAsPublished()}
 */
public class RulesetMarkAsPublishedTest {

    @Test
    public void shouldPublishRuleset() {
        // Given
        Integer version = nextInt(0, 10);
        Ruleset ruleset = givenRuleset(version, nextBoolean());

        // When
        ruleset.markAsPublished();

        // Then
        assertThat(ruleset.getVersion()).isEqualTo(version);
        assertThat(ruleset.isPublished()).isTrue();
        assertThat(ruleset.getDate()).isNotNull();
    }

    Ruleset givenRuleset(Integer currentVersion, boolean isPublished) {
        return Ruleset.builder().version(currentVersion).published(isPublished).build();
    }
}