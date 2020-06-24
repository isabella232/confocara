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

package com.orange.confocara.presentation.view.rule.list;

import com.orange.confocara.connector.persistence.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the Rules List View Module, except its web layer
 */
@Configuration
public class RuleListConfig {

    @Autowired
    private RuleRepository ruleRepository;

    @Bean
    RulesListService rulesListService() {
        return RulesListService.instance(ruleRepository);
    }
}
