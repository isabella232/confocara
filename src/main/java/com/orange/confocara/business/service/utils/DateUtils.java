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

package com.orange.confocara.business.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Toolbox for {@link Date}s conversion
 */
public class DateUtils {

    private DateUtils() {
        // static class. avoid instantiation.
    }

    /**
     * Formats a Date into a date/time string.
     *
     * @param date a {@link Date} to be formatted
     * @return a {@link String} that matches the format <yyyyMMddHHmmss>
     */
    public static String format(Date date) {
        return date != null ? new SimpleDateFormat("yyyyMMddHHmmss").format(date) : "";
    }
}
