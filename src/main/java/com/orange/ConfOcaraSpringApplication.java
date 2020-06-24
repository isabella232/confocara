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

package com.orange;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Entry point for the application
 */
@SpringBootApplication
public class ConfOcaraSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfOcaraSpringApplication.class, args);
    }

    /**
     * Helper for remembering a customized locale selection
     *
     * {@link CookieLocaleResolver} internally persists a custom locale and/or a time zone
     * information as browser cookie. It uses {@link javax.servlet.http.Cookie} to accomplish that.
     * This {@link LocaleResolver} is preferred to be used (over SessionLocaleResolver), when
     * application has to be stateless or when locale information should be persisted beyond
     * HTTPSession life time.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver slr = new CookieLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault());
        return slr;
    }
}
