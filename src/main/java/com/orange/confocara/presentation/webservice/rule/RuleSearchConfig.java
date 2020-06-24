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

package com.orange.confocara.presentation.webservice.rule;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the Rule Search Module, except its web layer
 */
@Configuration
public class RuleSearchConfig {

    @Autowired
    private EntityManager entityManager;

    @Bean
    RuleQueryRepository ruleQueryRepository() {
        return RuleQueryRepository.instance(entityManager);
    }

    @Bean
    RuleQueryService ruleQueryService() {
        return RuleQueryService.instance(ruleQueryRepository());
    }
}
