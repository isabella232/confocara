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

package com.orange.confocara.presentation.view.question.edit;

import com.orange.confocara.business.service.QuestionService;
import com.orange.confocara.business.service.RuleService;
import com.orange.confocara.business.service.SubjectService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.presentation.view.question.common.QuestionCommonConfig;
import com.orange.confocara.presentation.view.question.common.QuestionRulesQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the Question Editing view Module except its web layer
 */
@Import({QuestionCommonConfig.class})
@Configuration
public class QuestionEditConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionRulesQueryService questionRulesQueryService;

    @Autowired
    private RuleService ruleService;

    @Bean
    QuestionEditService questionEditService() {
        return QuestionEditService
                .instance(userService, questionService, subjectService, questionRulesQueryService, ruleService);
    }
}
