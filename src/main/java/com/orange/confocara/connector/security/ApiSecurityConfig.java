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

package com.orange.confocara.connector.security;

import com.orange.confocara.presentation.webservice.RestApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
// order 1 since we have to check the REST api first
@Order(1)
/* Security config for the REST API, for non browser clients */
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        disableCsrfForNonBrowserApi(http);
        initApi(http);
    }

    private void initApi(HttpSecurity http) throws Exception {
        http
                // configure the HttpSecurity to only be invoked when matching the provided ant pattern
                .antMatcher(RestApi.WS_ROOT + "**")

                // configure restricting access
                    .authorizeRequests()

                // admin api restricted to... ADMIN
                .antMatchers(RestApi.WS_ADMIN + "**")
                    .hasRole("ADMIN")

                // and the rest is allowed by any authenticated user
                .antMatchers(RestApi.WS_SEC_IMPORT + "**")
                    .authenticated()

                .antMatchers(RestApi.WS_SEC_GET_USERS + "**")
                    .authenticated()
                .and()

                // configure basic authentication
                .httpBasic();
    }

    /**
     * When should you use CSRF protection? Our recommendation is to use CSRF protection for any
     * request that could be processed by a browser by normal users. If you are only creating a
     * service that is used by non-browser clients, you will likely want to disable CSRF protection.
     *
     * see http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#when-to-use-csrf-protection
     */
    private void disableCsrfForNonBrowserApi(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
