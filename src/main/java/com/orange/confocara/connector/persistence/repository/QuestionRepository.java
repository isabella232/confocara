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

import com.orange.confocara.connector.persistence.model.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    Question findByReference(String reference);

    List<Question> findAllByOrderByIdDesc();

    List<Question> findBySubject(Subject subject);

    List<Question> findByUser(User user);

    List<Question> findByRules(Rule rule);

    List<Question> findByRulesCategory(RulesCategory rulesCategory);
}
