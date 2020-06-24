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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Useful tools configuration
 */
@SuppressWarnings("javadoc")
@Configuration
public class UtilConfig {

    @Bean
    public JacksonUtil jacksonUtil() {
        return JacksonUtil.instance(escapeUtil());
    }

    @Bean
    public EscapeUtil escapeUtil() {
        return EscapeUtil.instance();
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return jacksonUtil().buildObjectMapper();
    }
}
