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

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
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
public class CategoryRepositoryFindByNameIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void initialize() {
        // Given : 2 categories : cat and category
        GeneratorUtil.saveCategory("cat", categoryRepository);
        GeneratorUtil.saveCategory("category", categoryRepository);
    }

    @Test
    public void shouldReturn1CategoryWhenDBContains1CategoryWithTheSameName() {
        Category category = categoryRepository.findByName("cat");
        Assert.assertNotNull(category);
    }

    @Test
    public void shouldReturn0CategoryWhenDBDoesNotContainsCategoryWithThisName() {
        Category category = categoryRepository.findByName("categ");
        Assert.assertNull(category);
    }
}
