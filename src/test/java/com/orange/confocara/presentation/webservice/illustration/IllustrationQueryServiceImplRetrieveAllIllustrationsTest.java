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

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.orange.confocara.presentation.webservice.illustration.IllustrationQueryService.IllustrationQueryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * see {@link IllustrationQueryService#retrieveAllIllustrations(Long)}
 */
public class IllustrationQueryServiceImplRetrieveAllIllustrationsTest {

    private IllustrationQueryServiceImpl subject;

    private IllustrationDtoQueryRepository repository;

    @Before
    public void setUp() {
        repository = mock(IllustrationDtoQueryRepository.class);

        subject = new IllustrationQueryServiceImpl(repository);
    }

    @Test
    public void shouldDelegateToRepository() {
        // Given


        // When
        subject.retrieveAllIllustrations(nextLong());

        // Then
        verify(repository).findAll(Matchers.anyLong());
    }
}