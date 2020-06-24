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

import com.orange.confocara.connector.persistence.model.Category;
import java.util.List;
import org.junit.Assert;
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
public class CategoryServiceIT {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EquipmentService equipmentService;

    @Test
    public void createCategory() {
        // do
        Category cat1 = new Category();
        cat1.setName("cat1");
        categoryService.create(cat1);
        Category cat2 = new Category();
        cat2.setName("cat2");
        categoryService.create(cat2);
        List<Category> categories = categoryService.withName("cat");
        Assert.assertTrue(!categories.isEmpty());
        Assert.assertTrue(categories.size() == 2);
    }

    @Test
    public void updateCategory() {
        // do
        Category cat11 = new Category();
        cat11.setName("cat11");
        Category category = categoryService.create(cat11);
        String cat2 = "cat21";
        category.setName(cat2);
        Category category2 = categoryService.update(category);

        List<Category> categories = categoryService.withName(cat2);
        Assert.assertTrue(!categories.isEmpty());
        Assert.assertTrue(category2.getName().equals(categories.get(0).getName()));
    }

    @Test
    public void deleteCategory() {
        // do
        Category cat12 = new Category();
        cat12.setName("cat12");
        categoryService.create(cat12);
        String cat2 = "cat22";
        Category category = new Category();
        category.setName(cat2);
        Category category2 = categoryService.create(category);
        categoryService.deleteCategory(category2.getId());

        List<Category> all = categoryService.withName(cat2);
        Assert.assertTrue(all.isEmpty());
    }
}
