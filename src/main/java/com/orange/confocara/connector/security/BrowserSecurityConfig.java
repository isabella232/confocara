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

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureRoutes(http);
    }

    private void configureRoutes(HttpSecurity http) throws Exception {
        disableSecurityOnWebJars(http);
        disableSecForDBConsole(http);
        http
                // configure restricting access
                .antMatcher("/**")
                    .authorizeRequests()

                // open api is... opened
                .antMatchers("/ws/**", "/image/**", "/images/**", "/uploadPicto/**", "/testupload", "/css/**", "/fonts/**", "/js/**")
                    .permitAll()

                .antMatchers("/conditions.html", "/termsOfUse.html")
                    .permitAll()

                // admin api restricted to... ADMIN
                .antMatchers("/admin/**")
                    .hasAnyRole("ADMIN", "SUPERADMIN")
                .antMatchers("/admin/**")
                    .access("hasAnyRole('ADMIN', 'SUPERADMIN')")

                // and the rest is allowed by any authenticated user
                .anyRequest()
                    .authenticated()

                .and()
                // setup logout
                .formLogin()
                .loginPage("/login")
                .successHandler(successHandler())
                    .permitAll()

                // setup logout
                .and()
                .logout()
                .logoutSuccessUrl("/").deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                    .permitAll();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler();
    }

    private void disableSecurityOnWebJars(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/webjars/**").permitAll();
    }

    private void disableSecForDBConsole(HttpSecurity http) throws Exception {
        if (isDevProfile()) {
            log.warn("Disable security to allow H2 console");
            String url = "/h2-console/**";
            http.csrf().ignoringAntMatchers(url);
            http.authorizeRequests().antMatchers(url).permitAll();
            http.headers().frameOptions().disable();
        }
    }

    private boolean isDevProfile() {
        String[] profiles = environment.getActiveProfiles();
        return Arrays
                .stream(profiles)
                .anyMatch("dev"::equals);
    }
}
