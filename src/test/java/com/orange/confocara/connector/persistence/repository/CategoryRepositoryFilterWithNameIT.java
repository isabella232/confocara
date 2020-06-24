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
public class CategoryRepositoryFilterWithNameIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void initialize() {
        // Given : 2 categories : cat and category
        GeneratorUtil.saveCategory("cat", categoryRepository);
        GeneratorUtil.saveCategory("category", categoryRepository);
    }

    @Test
    public void shouldReturn2CategoriesFilterWithNameCatWhenDBContains2CategoriesContainingThisName() {
        List<Category> categories = categoryRepository.filterWithName("cat");
        Assert.assertTrue(categories.size() == 2);
    }

    @Test
    public void shouldReturn1CategoryFilterWithNameCategWhenDBContains1CategoryContainingThisName() {
        List<Category> categories = categoryRepository.filterWithName("categ");
        Assert.assertTrue(categories.size() == 1);
    }

    @Test
    public void shouldReturn0CategoryWhenDBContains0CategoryWithThisName() {
        List<Category> categories = categoryRepository.filterWithName("hello");
        Assert.assertTrue(categories.size() == 0);
    }

    @Test
    public void shouldReturn2CategoriesFilterWithEmptyFilterWhenDBContains2Categories() {
        List<Category> categories = categoryRepository.filterWithName("");
        Assert.assertTrue(categories.size() == 2);
    }
}
