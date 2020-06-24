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

package com.orange.confocara.presentation.view.question.create;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RulesCategoryService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.presentation.view.question.common.QuestionCommonConfig;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the Question Adding view Module except its web layer
 */
@Configuration
@Import({QuestionCommonConfig.class})
public class QuestionAddConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionRulesQueryService questionRulesQueryService;

    @Autowired
    private RulesCategoryService rulesCategoryService;

    @Bean
    QuestionAddService questionAddService() {
        return QuestionAddService.instance(userService, questionService, subjectService,
                questionRulesQueryService, rulesCategoryService);
    }
}
