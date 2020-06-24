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

package com.orange.confocara.presentation.view.controller.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class GenericUtils {

    private GenericUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Gets a string list (should contain number strings) and converts it into long list
     *
     * @param stringList the string list
     * @return stringList converted into long list
     */
    public static List<Long> convertToLongList(List<String> stringList) {
        if (stringList == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(stringList.toArray())
                .map(string -> Long.parseLong((String) string))
                .collect(Collectors.toList());
    }
}
