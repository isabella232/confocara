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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Toolbox for JSON converters
 */
@FunctionalInterface
public interface JacksonUtil {

    ObjectMapper buildObjectMapper();

    static JacksonUtil instance() {
        return new JacksonUtilImpl();
    }

    /**
     * Default implentation of {@link JacksonUtil}
     */
    @Service
    final class JacksonUtilImpl implements JacksonUtil {

        @Override
        public ObjectMapper buildObjectMapper() {
            return new ObjectMapper();
        }
    }
}
