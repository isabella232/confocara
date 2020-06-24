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

import com.orange.confocara.connector.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The type of entity and ID that it works with, User and Long, are specified in the generic parameters on CrudRepository.
 * By extending CrudRepository, UserRepository inherits several methods for working with User persistence,
 * including methods for saving, deleting, and finding User entities.
 * <p>
 * Spring Data JPA also allows you to define other query methods by simply declaring their method signature.
 * In the case of UserRepository, an example is with the findByLastName() method.
 * <p>
 * In a typical Java application, you’d expect to write a class that implements CustomerRepository.
 * But that’s what makes Spring Data JPA so powerful: You don’t have to write an implementation of the repository interface.
 * Spring Data JPA creates an implementation on the fly when you run the application. Cool :)
 * <p>
 * written by cristophe maldivi
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByEmail(String email);

    User findTopByOrderByIdDesc();
}
