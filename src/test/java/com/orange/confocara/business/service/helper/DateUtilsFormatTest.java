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

package com.orange.confocara.business.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import com.orange.confocara.business.service.utils.DateUtils;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Test;

/**
 * see {@link DateUtils#format(Date)}
 */
public class DateUtilsFormatTest {

    @Test
    public void shouldReturnEmptyStringWhenGivenDateIsNull() {

        // Given


        // When
        String result = DateUtils.format(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnFormattedStringWhenGivenDateIsNotNull() {

        // Given
        Date input = Date.from(
                LocalDateTime
                    .of(2017, Month.MARCH,29, 9, 59, 07)
                    .toInstant(ZoneOffset.ofHours(2)));

        // When
        String result = DateUtils.format(input);

        // Then
        assertThat(result).isEqualTo("20170329095907");
    }
}