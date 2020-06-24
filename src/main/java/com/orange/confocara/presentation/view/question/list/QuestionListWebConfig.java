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

package com.orange.confocara.presentation.view.question.list;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.presentation.CommonWebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the web layer of the Question Listing Module
 */
@Configuration
@Import({CommonWebConfig.class})
public class QuestionListWebConfig {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RulesCategoryService rulesCategoryService;

    @Bean
    QuestionListViewController questionListViewController() {
        return new QuestionListViewController(questionService, rulesCategoryService);
    }
}
