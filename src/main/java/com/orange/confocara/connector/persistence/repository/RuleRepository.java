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

import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RuleRepository extends CrudRepository<Rule, Long> {

    Rule findByReference(String reference);

    List<Rule> findAllByOrderByIdDesc();

    List<Rule> findByUser(User user);

    List<Rule> findByRulesCategory(RulesCategory rulesCategory);

    List<Rule> findByQuestion(Question q);

    Rule findTopByOrderByIdDesc();

    List<Rule> findByIllustrations(Illustration illustration);
}
