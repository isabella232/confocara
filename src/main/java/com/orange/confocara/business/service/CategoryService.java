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
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    public static final String ALL_OBJECTS = "Tous les objets";
    public static final String ALL_OBJECTS_ID = "1";

    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;

    @PostConstruct
    void init() {
        createAndPersistDefaultCategory();
    }

    /**
     * init database with "all categories"
     * (not editable value)
     */
    @Transactional
    public void createAndPersistDefaultCategory() {
        if (getCategoryByName(ALL_OBJECTS) == null) {
            Category allCategories = new Category();

            allCategories.setName(ALL_OBJECTS);
            allCategories.setEditable(false);
            categoryRepository.save(allCategories);
        }
    }

    @Transactional
    public List<Category> all() {
        return (List<Category>) categoryRepository.findAll();
    }

    @Transactional
    public Category withId(long id) {
        return categoryRepository.findOne(id);
    }

    @Transactional
    public List<Category> withIds(List<Long> ids) {
        return (List<Category>) categoryRepository.findAll(ids);
    }

    @Transactional
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public List<Category> withName(String name) {
        return categoryRepository.filterWithName(name);
    }

    @Transactional
    public boolean isAvailable(String name) {
        Category category = categoryRepository.findByName(name.trim().replaceAll("\\s+", " "));
        return category == null;
    }

    @Transactional
    public Category create(@NonNull Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(@NonNull Category category) {
        return categoryRepository.save(category);
    }

    /**
     * When a category is deleted, links with equipments are deleted
     * If the equipment has only default category left, then return its name and cancel deletion
     *
     * @param id the id of the category to delete
     * @return the equipments name having category which have only default category after category deletion
     */
    @Transactional
    public List<String> deleteCategoryIfNoWarningRaised(long id) {
        List<String> equipmentsWithOnlyDefaultCategory = new ArrayList<>();
        Category category = categoryRepository.findOne(id);
        List<Equipment> equipments = equipmentRepository.findByCategories(category);

        for (Equipment equipment : equipments) {
            if (equipment.getCategories().size() == 2) {
                equipmentsWithOnlyDefaultCategory.add(equipment.getName());
            }
        }

        if (equipmentsWithOnlyDefaultCategory.isEmpty()) {
            for (Equipment equipment : equipments) {
                equipment.getCategories().remove(category);
            }

            categoryRepository.delete(id);
        }

        return equipmentsWithOnlyDefaultCategory;
    }

    @Transactional
    public void deleteCategory(long id) {
        Category category = categoryRepository.findOne(id);
        List<Equipment> equipments = equipmentRepository.findByCategories(category);

        for (Equipment equipment : equipments) {
            equipment.getCategories().remove(category);
        }

        categoryRepository.delete(id);
    }
}
