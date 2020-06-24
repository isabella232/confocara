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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.util.Lists;

public class TestUtils {

    static User genUser() {
        User user = new User();
        user.setUsername("Mister Test");
        user.setPasswordHash(randomAlphabetic(10));
        user.setEmail("test@test.com");
        return user;
    }

    static ImpactValue genImpactValue(String name) {
        ImpactValue impactValue = new ImpactValue();
        impactValue.setName(name);

        return impactValue;
    }

    static ImpactValue genImpactValue() {
        return genImpactValue(randomAlphabetic(10));
    }

    static RulesCategory genRulesCategory(ImpactValue impactValue) {
        return genRulesCategory(randomAlphabetic(10), impactValue);
    }

    static RulesCategory genRulesCategory(String name, ImpactValue impactValue) {

        RulesCategory rulesCategory = new RulesCategory();
        rulesCategory.setName(name);
        rulesCategory.setDefaultImpact(impactValue);

        List<ImpactValue> impactValues = new ArrayList<>();
        impactValues.add(impactValue);

        rulesCategory.setImpactValues(impactValues);

        return rulesCategory;
    }

    static Ruleset genRuleset(String name, String ref, Integer version,
            RulesCategory rulesCategory,
            String language,
            String comment,
            List<QuestionnaireObject> questionnaireObjects) {

        Ruleset ruleset = new Ruleset();
        ruleset.setReference(ref);
        ruleset.setVersion(version);
        ruleset.setType(name);
        ruleset.setRulesCategory(rulesCategory);
        ruleset.setPublished(false);
        ruleset.setQuestionnaireObjects(questionnaireObjects);
        ruleset.setState(State.ACTIVE.toString().toLowerCase());
        ruleset.setDate(new Date());
        ruleset.setLanguage(language);
        ruleset.setComment(comment);

        return ruleset;
    }

    static Ruleset genRuleset(String ref, RulesCategory rulesCategory, QuestionnaireObject questionnaireObject) {

        List<QuestionnaireObject> questionnaireObjects = Lists.newArrayList(questionnaireObject);

        return genRuleset(
                randomAlphabetic(10),
                ref,
                RandomUtils.nextInt(),
                rulesCategory,
                randomAlphabetic(2),
                randomAlphabetic(10),
                questionnaireObjects);
    }

    static QuestionnaireObject genQuestionnaire(String name, String ref, Integer version,
            RulesCategory rulesCategory, Equipment equipment, User author, List<Chain> chains) {
        QuestionnaireObject qo = new QuestionnaireObject();
        qo.setReference(ref);
        qo.setVersion(version);
        qo.setName(name);
        qo.setRulesCategory(rulesCategory);
        qo.setEquipment(equipment);
        qo.setPublished(false);
        qo.setChains(chains);
        qo.setState(State.ACTIVE.toString().toLowerCase());
        qo.setDate(new Date());
        qo.setUser(author);

        return qo;
    }

    static QuestionnaireObject genQuestionnaire(RulesCategory rulesCategory, Equipment equipment,
            Chain chain, User author) {

        return genQuestionnaire(
                randomAlphabetic(10),
                randomAlphabetic(10),
                RandomUtils.nextInt(),
                rulesCategory,
                equipment,
                author,
                Lists.newArrayList(chain));
    }

    static Chain genChain(String name, String ref, RulesCategory rulesCategory,
            List<Question> questions) {
        Chain chain = new Chain();
        chain.setName(name);
        chain.setReference(ref);
        chain.setRulesCategory(rulesCategory);
        chain.setQuestions(questions);
        return chain;
    }

    static Chain genChain(RulesCategory rulesCategory) {
        List<Question> questions = Lists.newArrayList();
        return genChain(
                randomAlphabetic(10),
                randomAlphabetic(10),
                rulesCategory,
                questions);
    }

    static Equipment genEquipment(String name, String ref, List<Category> categories, String type, String icon) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setReference(ref);
        equipment.setCategories(categories);

        ImageEquipment imageToSave = new ImageEquipment();
        imageToSave.setImageName(icon);
        imageToSave.setExtension("jpg");
        equipment.setIcon(imageToSave);

        equipment.setType(type);

        return equipment;
    }

    static Equipment genEquipment(Category category) {
        List<Category> categories = Lists.newArrayList(category);
        return genEquipment(
                randomAlphabetic(10),
                randomAlphabetic(10),
                categories,
                randomAlphabetic(10),
                randomAlphabetic(10));
    }

    static Category genCategory(String name) {
        Category category = new Category();
        category.setName(name);

        return category;
    }

    static Category genCategory() {
        return genCategory(randomAlphabetic(10));
    }
}
