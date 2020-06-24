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
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.IllustrationRepository;
import com.orange.confocara.connector.persistence.repository.ImageEquipmentRepository;
import com.orange.confocara.connector.persistence.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class EquipmentServiceIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageEquipmentRepository imageEquipmentRepository;

    @Autowired
    IllustrationService illustrationService;

    @Autowired
    IllustrationRepository illustrationRepository;

    @Test
    public void delObject() {
        // given
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("email@e");
        User user = userRepository.save(user1);

        Category catName1 = new Category();
        catName1.setName("CatName1");
        Category catName = categoryService.create(catName1);

        String name = "porte";
        Equipment equipment = new Equipment();
        equipment.setReference("REFFFF");
        equipment.setName(name);
        List<Category> categories = new ArrayList<>();
        categories.add(catName);
        equipment.setCategories(categories);
        equipment.setUser(user);
        equipment.setType("");

        addIconToEquipment(equipment, "test.jpg", "jpg");

        // do
        Equipment createdEquipment = equipmentService.create(equipment);
        equipmentService.delete(createdEquipment.getId());

        // then
        Assertions.assertThat(equipmentService.all()).isEmpty();
    }

    @Test
    public void createObjectWithChildren() {
        // given
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("email@em");
        User user = userRepository.save(user1);

        String name = "porte";
        String catName11 = "CatName1";
        Category catName1 = new Category();
        catName1.setName(catName11);
        Category catName = categoryService.create(catName1);
        // do
        Equipment ref8 = new Equipment();
        ref8.setReference("REF8");
        ref8.setName(name);
        ref8.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(catName);
        ref8.setCategories(categories);
        ref8.setType("");
        addIconToEquipment(ref8, "test.jpg", "jpg");
        Equipment equipment1 = equipmentService.create(ref8);

        Equipment ref7 = new Equipment();
        ref7.setReference("REF7");
        ref7.setName(name + "test");
        ref7.setUser(user);
        ref7.setCategories(categories);
        ref7.setType("");
        addIconToEquipment(ref7, "test2.jpg", "jpg");
        Equipment equipment2 = equipmentService.create(ref7);

        List<Equipment> objects1 = new ArrayList<>();
        objects1.add(equipment1);
        objects1.add(equipment2);

        Equipment ref9 = new Equipment();
        ref9.setReference("REF9");
        ref9.setName( name + "bat");
        ref9.setUser(user);
        ref9.setCategories(categories);
        ref9.setType("");
        addIconToEquipment(ref9, "test3.jpg", "jpg");
        ref9.setSubobjects(objects1);
        Equipment equipment3 = equipmentService.create(ref9);

        // then
        Assertions.assertThat(equipment3.getSubobjects().contains(equipment1)).isTrue();
    }

    @Test
    @Transactional
    public void delObjectWithChildren() {
        // given
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("email@em");
        User user = userRepository.save(user1);
        Category catName1 = new Category();
        catName1.setName("CatName1");
        Category catName = categoryService.create(catName1);
        String name = "porte";
        Equipment ref10 = new Equipment();
        ref10.setReference("REF10");
        ref10.setName(name);
        ref10.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(catName);
        ref10.setCategories(categories);
        ref10.setType("");
        addIconToEquipment(ref10, "test.jpg", "jpg");
        equipmentService.create(ref10);

        List<Equipment> equipments = equipmentService.all();
        Equipment equipment2 = new Equipment();
        equipment2.setReference("REF11");
        equipment2.setName(name + "BAT");
        equipment2.setUser(user);
        equipment2.setCategories(categories);
        equipment2.setSubobjects(equipments);
        equipment2.setType("");
        addIconToEquipment(equipment2, "test3.jpg", "jpg");
        Equipment equipmentFinal = equipmentService.create(equipment2);
        equipmentService.delete(equipmentFinal.getId());

        Assertions.assertThat(equipmentService.isAvailable(name)).isFalse();
    }

    @Test
    public void updateObject() {
        // given
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("email@e");
        User user = userRepository.save(user1);
        Category catName1 = new Category();
        catName1.setName("CatName1");
        Category catName = categoryService.create(catName1);
        String name = "porte";
        Equipment equipment2 = new Equipment();
        equipment2.setReference("ref1");
        equipment2.setName(name);
        equipment2.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(catName);
        equipment2.setCategories(categories);
        equipment2.setType("");
        addIconToEquipment(equipment2, "test.jpg", "jpg");

        Equipment equipment = equipmentService.create(equipment2);
        String nameUp = name + "up";
        equipment.setName(nameUp);
        Equipment update = equipmentService.update(equipment);
        // then
        Assertions.assertThat(update).isNotNull();
        Assertions.assertThat(update.getName()).isEqualTo(nameUp);
        Assertions.assertThat(update.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @Transactional
    public void createObjectWithCategory() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);

        String namecat = "toto";
        Category catName1 = new Category();
        catName1.setName(namecat);
        Category category = categoryService.create(catName1);
        String name = "porte";
        Equipment equipment2 = new Equipment();
        equipment2.setReference("ref2");
        equipment2.setName(name);
        equipment2.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        equipment2.setCategories(categories);
        equipment2.setType("");
        addIconToEquipment(equipment2, "test.jpg", "jpg");
        // do
        Equipment createdEquipment = equipmentService.create(equipment2);
        Equipment equipment = equipmentRepository.findOne(createdEquipment.getId());

        // then
        Assertions.assertThat(equipment).isNotNull();
        Assertions.assertThat(equipment.getName()).isEqualTo(name);
        Assertions.assertThat(equipment.getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(equipment.getCategories().get(0).getId()).isEqualTo(category.getId());
    }

    @Test
    public void delObjectWithCategory() {
        // given
        String username = "titi";
        final User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("password");
        user1.setEmail("email@a");
        User user = userRepository.save(user1);

        String name = "porte";
        String namecat = "toto";

        Category category1 = new Category();
        category1.setName(namecat);
        Category category = categoryService.create(category1);
        Equipment equipment2 = new Equipment();
        equipment2.setReference("REF4");
        equipment2.setName(name);
        equipment2.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        equipment2.setCategories(categories);
        equipment2.setType("");
        addIconToEquipment(equipment2, "test.jpg", "jpg");
        // do
        Equipment createdEquipment = equipmentService.create(equipment2);
        equipmentService.delete(createdEquipment.getId());

        // then
        Assertions.assertThat(equipmentService.withName(name)).isNull();
    }

    @Test
    @Transactional
    public void createObjectWithIllustrations() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("TEST@TEST");
        User user = userService.create(user1);

        String namecat = "toto";
        Category category1 = new Category();
        category1.setName(namecat);
        Category category = categoryService.create(category1);

        String title = "porte";
        String comment = "...";
        Illustration refill = new Illustration();
        refill.setReference("REFILL");
        refill.setTitle(title);
        refill.setComment(comment);
        illustrationService.create(refill);

        String name = "porte";
        List<Illustration> illustrations;
        illustrations = (List<Illustration>) illustrationRepository.findAll();
        Equipment ref6 = new Equipment();
        ref6.setReference("REF6");
        ref6.setName(title);
        ref6.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        ref6.setCategories(categories);
        ref6.setIllustrations(illustrations);
        ref6.setType("");
        addIconToEquipment(ref6, "test.jpg", "jpg");
        // do
        Equipment createdEquipment = equipmentService.create(ref6);
        Equipment equipment = equipmentRepository.findOne(createdEquipment.getId());

        // then
        Assertions.assertThat(equipment).isNotNull();
        Assertions.assertThat(equipment.getName()).isEqualTo(name);
        Assertions.assertThat(equipment.getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(equipment.getCategories().get(0).getId()).isEqualTo(category.getId());
    }

    @Test
    public void delObjectWithIllustrations() {
        // given
        String username = "titi";
        User user1 = new User();
        user1.setUsername(username);
        user1.setPasswordHash("");
        user1.setEmail("Test@TEST");
        User user = userService.create(user1);

        String namecat = "toto";
        Category category1 = new Category();
        category1.setName(namecat);
        Category category = categoryService.create(category1);

        String title = "porte";
        String comment = "...";
        Illustration refill = new Illustration();
        refill.setReference("REF2");
        refill.setTitle(title);
        refill.setComment(comment);
        illustrationService.create(refill);

        List<Illustration> illustrations;
        illustrations = (List<Illustration>) illustrationRepository.findAll();
        Equipment ref123 = new Equipment();
        ref123.setReference("REF123");
        ref123.setName(title);
        ref123.setUser(user);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        ref123.setCategories(categories);
        ref123.setIllustrations(illustrations);
        addIconToEquipment(ref123, "test.jpg", "jpg");
        ref123.setType("object");

        // do
        Equipment createdEquipment = equipmentService.create(ref123);
        refill.setReference("REFIIL1");
        refill.setTitle(title+"2");
        refill.setComment(comment);
        illustrationService.create(refill);

        equipmentService.delete(createdEquipment.getId());

        // then
        Assertions.assertThat(equipmentService.withName(title)).isNull();
    }

    private void addIconToEquipment(Equipment equipment, String name, String extension) {
        ImageEquipment imageToSave = new ImageEquipment();
        imageToSave.setImageName(name);
        imageToSave.setExtension(extension);
        equipment.setIcon(imageToSave);
    }
}
