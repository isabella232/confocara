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

import static org.assertj.core.api.Assertions.assertThat;

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
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
public class EquipmentRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IllustrationRepository illustrationRepository;

    @Autowired
    private ImageRepository imageRepository;

    private String username1 = "Duchess";
    private String password1 = "1234";

    private String name = "Porte";

    private String categoryName = "accès zone";
    private String title = "porte";
    private String image = "porte.png";
    private String comment = "c'est une porte";

    @Test
    public void createObjectWithUser() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);

        Category category = new Category();
        category.setName(name);
        Category savedCategory = categoryRepository.save(category);

        Equipment equipment = new Equipment();
        equipment.setReference("");
        equipment.setName(name);
        //equipment.setIcon("porte");
        equipment.setUser(userFromDB);
        equipment.setType("");
        List<Category> categories = new ArrayList<>();
        categories.add(savedCategory);
        equipment.setCategories(categories);
        equipmentRepository.save(equipment);

        Equipment equipment1 = equipmentRepository.findByName(name);
        Assert.assertTrue(equipment1.getName().equals(name));
    }

    @Test
    public void createObjectWithCategory() {
        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);
        Category category = GeneratorUtil.saveCategory(categoryName, categoryRepository);

        Equipment equipment = new Equipment();
        equipment.setReference("");
        equipment.setName(name);
        //equipment.setIcon( "porte");
        equipment.setUser(userFromDB);
        equipment.setType("");
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        equipment.setCategories(categories);
        equipmentRepository.save(equipment);

        Equipment equipment1 = equipmentRepository.findByCategories(category).get(0);
        assertThat(equipment1.getCategories().get(0)).isEqualTo(category);
    }

    @Test
    public void createObjectWithIllustrations() {
        final Illustration illustration1 = new Illustration();
        illustration1.setReference("");
        illustration1.setTitle(title);
        //illustration1.setImage(image);
        illustration1.setComment(comment);

        Illustration illustration = illustrationRepository.save(illustration1);
        List<Illustration> ills = new ArrayList<>();
        ills.add(illustration);

        final User user = new User();
        user.setUsername(username1);
        user.setPasswordHash( password1);
        user.setEmail("email");
        User userFromDB = userRepository.save(user);

        Category categoryToSave = new Category();
        categoryToSave.setName(categoryName);
        Category category = categoryRepository.save(categoryToSave);

        Equipment equipment = new Equipment();
        equipment.setReference("");
        equipment.setName("porte");
        //equipment.setIcon("");
        equipment.setType("");
        equipment.setUser(userFromDB);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        equipment.setCategories(categories);
        equipment.setIllustrations(ills);

        equipmentRepository.save(equipment);

        List<Equipment> equipmentsList = ((List<Equipment>) equipmentRepository.findAll());
        List<String> names = equipmentsList.stream()
                .map(Equipment::getName)
                .collect(Collectors.toList());
        System.out.println("All equipment names :" + Arrays.toString(names.toArray()));
        System.out.println("All objects :");
        equipmentRepository.findAll().forEach(System.out::println);
    }
}
