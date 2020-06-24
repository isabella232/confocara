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

package com.orange.confocara.connector.persistence.repository;

import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ImpactValueRepositoryFilterWithNameIT {

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Before
    public void initialize() {
        GeneratorUtil.saveImpactValue("imp", impactValueRepository);
        GeneratorUtil.saveImpactValue("ImpactValue", impactValueRepository);
    }

    @Test
    public void shouldReturn2ImpactsFilterWithNameImpWhenDBContains2ImpactsContainingThisName() {
        List<ImpactValue> impacts = impactValueRepository.filterWithName("imp");
        Assert.assertTrue(impacts.size() == 2);
    }

    @Test
    public void shouldReturn1ImpactValueFilterWithNameImpacWhenDBContains1ImpactContainingThisName() {
        List<ImpactValue> impacts = impactValueRepository.filterWithName("impac");
        Assert.assertTrue(impacts.size() == 1);
    }

    @Test
    public void shouldReturn0ImpactWhenDBContains0ImpactWithThisName() {
        List<ImpactValue> impacts = impactValueRepository.filterWithName("hello");
        Assert.assertTrue(impacts.size() == 0);
    }

    @Test
    public void shouldReturn2ImpactsFilterWithEmptyFilterWhenDBContains2Impacts() {
        List<ImpactValue> impacts = impactValueRepository.filterWithName("");
        Assert.assertTrue(impacts.size() == 2);
    }
}
