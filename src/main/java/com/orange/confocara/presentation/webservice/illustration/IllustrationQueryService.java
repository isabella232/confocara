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

package com.orange.confocara.presentation.webservice.illustration;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Behaviour of services that can query for {@link com.orange.confocara.connector.persistence.model.Illustration}s
 */
@FunctionalInterface
interface IllustrationQueryService {

    /**
     * Retrieves a {@link List} of illustrations
     *
     * @return a list of items
     */
    IllustrationsResponse retrieveAllIllustrations(
            Long ruleId);

    static IllustrationQueryService instance(
            IllustrationDtoQueryRepository repository) {
        return new IllustrationQueryServiceImpl(repository);
    }

    @RequiredArgsConstructor
    @Slf4j
    final class IllustrationQueryServiceImpl implements
            IllustrationQueryService {

        private final IllustrationDtoQueryRepository repository;

        @Override
        public IllustrationsResponse retrieveAllIllustrations(Long ruleId) {

            return ImmutableIllustrationsResponse
                    .builder()
                    .addAllIllustrations(repository.findAll(ruleId))
                    .build();
        }
    }
}
