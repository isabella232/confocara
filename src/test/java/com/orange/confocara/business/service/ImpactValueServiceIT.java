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

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.ImpactValue;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class ImpactValueServiceIT {

    public static final String IMPACT_NAME = "impactName";

    @Autowired
    private ImpactValueService impactValueService;

    @Test
    public void createImpactValue() {
        ImpactValue impactValue = createImpactValue(IMPACT_NAME, impactValueService);
        ImpactValue byName = impactValueService.findByName(IMPACT_NAME);
        Assertions.assertThat(impactValue).isNotNull();
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void updateImpactValue() {
        ImpactValue impactValue = createImpactValue(IMPACT_NAME, impactValueService);
        String newName = "new newName";
        impactValue.setName(newName);
        impactValueService.update(impactValue);
        ImpactValue byName = impactValueService.findByName(newName);
        Assertions.assertThat(byName).isNotNull();
    }

    @Test
    public void deleteImpactValue() {
        ImpactValue impactValue = createImpactValue(IMPACT_NAME, impactValueService);
        impactValueService.delete(impactValue.getId());
        ImpactValue byNameDel = impactValueService.findByName(IMPACT_NAME);
        Assertions.assertThat(byNameDel).isNull();
    }

    public static ImpactValue createImpactValue(String name, ImpactValueService impactValueService) {
        ImpactValue impactValue = new ImpactValue();
        impactValue.setName(name);

        return impactValueService.create(impactValue);
    }
}
