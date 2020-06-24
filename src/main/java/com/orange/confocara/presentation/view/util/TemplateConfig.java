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

package com.orange.confocara.presentation.view.util;

import com.orange.confocara.connector.persistence.model.WithState;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuration for i18n
 */
@Configuration
public class TemplateConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public String translateState(WithState item) {
        return Optional
                .of(item)
                .filter(WithState::isActive)
                .map(q -> messageSource().getMessage(State.ACTIVE.toString().toLowerCase(), null, LocaleContextHolder
                        .getLocale()))
                .orElseGet(() -> messageSource().getMessage(State.INACTIVE.toString().toLowerCase(), null, LocaleContextHolder.getLocale()));
    }
}
