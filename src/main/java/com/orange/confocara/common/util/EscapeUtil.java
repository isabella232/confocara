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

package com.orange.confocara.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Class used to escape strings
 */
@FunctionalInterface
public interface EscapeUtil {

    /**
     * Escapes the string for the HTML, JavaScript and SQL codes.
     *
     * @param value A string
     * @return The escaped String
     */
    String escape(String value);

    /**
     * @return An instance of the default implementation
     */
    static EscapeUtil instance() {
        return new EscapeUtilImpl();
    }

    /**
     * Default implementation
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class EscapeUtilImpl implements EscapeUtil {

        private static final String ESCAPED_APOS = "\u00b4";
        /**
         * Validation key for pattern
         */
        public static final String PATTERN = "Pattern";
        /**
         * Validator key for empty strings
         */
        public static final String NOT_BLANK = "NotBlank";

        /**
         * Escapes the string for the HTML, JavaScript and SQL codes.
         *
         * @param value A string
         * @return The escaped String
         */
        @Override
        public String escape(String value) {

            String escapedValue = value;

            if (StringUtils.isNotBlank(escapedValue)) {

                escapedValue = StringEscapeUtils.unescapeXml(escapedValue);
                escapedValue = escapedValue
                        .replaceAll("'", ESCAPED_APOS)
                        .replaceAll("&apos;", ESCAPED_APOS);
            }

            return escapedValue;
        }
    }
}
