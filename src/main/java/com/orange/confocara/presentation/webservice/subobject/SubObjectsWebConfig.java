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

package com.orange.confocara.presentation.webservice.subobject;

import com.orange.confocara.presentation.CommonWebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the web layer of the Questionnaire List Module
 */
@Configuration
@Import({CommonWebConfig.class})
public class SubObjectsWebConfig {

    @Autowired
    private SubObjectsQueryService subObjectsQueryService;

    @Autowired
    private SubObjectsPublishService subObjectsPublishService;

    @Bean
    SubObjectWebService subObjectWebService() {
        return new SubObjectWebService(subObjectsQueryService, subObjectsPublishService);
    }
}
