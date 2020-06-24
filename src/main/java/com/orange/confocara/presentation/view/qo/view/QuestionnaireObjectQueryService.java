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

import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Behaviour of services that can supply with {@link QuestionnaireObject}s
 *
 * @param <R> the type of the result objects
 */
@FunctionalInterface
public interface QuestionnaireObjectQueryService<R> {

    R retrieveOneQuestionnaire(String reference, Integer version);

    static QuestionnaireObjectQueryService instance(
            QuestionnaireObjectQueryRepository queryRepository,
            Function mapper) {
        return new QuestionnaireObjectQueryServiceImpl(queryRepository, mapper);
    }

    /**
     * Default implementation
     *
     * @param <R> the type of the result objects
     */
    @Slf4j
    @RequiredArgsConstructor
    final class QuestionnaireObjectQueryServiceImpl<R> implements QuestionnaireObjectQueryService {

        private final QuestionnaireObjectQueryRepository qoRepository;

        private final Function<QuestionnaireObject, R> mapper;

        @Override
        public R retrieveOneQuestionnaire(String reference, Integer version) {

            if (!qoRepository.existsByReferenceAndVersion(reference, version)) {
                throw new BizException(ErrorCode.INVALID, "Invalid arguments");
            }

            return Optional
                    .of(qoRepository.findByReferenceAndVersion(reference, version))
                    .map(mapper)
                    .orElseThrow(() -> new BizException(ErrorCode.INVALID, "Wrong arguments"));
        }
    }
}
