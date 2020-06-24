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

package com.orange.confocara;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.util.Lists.newArrayList;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.QuestionnaireDtoWrapper;
import com.orange.confocara.connector.persistence.dto.RulesetDto;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.ImageEquipment;
import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ImpactValue;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class TestUtils {

    @NonNull
    public static List<QuestionnaireDtoWrapper> generateQuestionnaireDtoWrapperList(int count) {
        List<QuestionnaireDtoWrapper> resultList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            resultList.add(generateQuestionnaireDtoWrapper(i));
        }

        return resultList;
    }

    @NonNull
    public static QuestionnaireDtoWrapper generateQuestionnaireDtoWrapper(int id) {
        QuestionnaireDtoWrapper questionnaireDto = new QuestionnaireDtoWrapper();
        questionnaireDto.setQuestionsNb(2);
        questionnaireDto.setRulesNb(5);
        questionnaireDto.setDto(new QuestionnaireDto() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public String getReference() {
                return "ref" + id;
            }

            @Override
            public String getName() {
                return "name" + id;
            }

            @Override
            public boolean getPublished() {
                return false;
            }

            @Override
            public Integer getVersion() {
                return 1;
            }

            @Override
            public String getRulesCategoryName() {
                return "rulesCatName";
            }

            @Override
            public String getEquipmentName() {
                return "eqName";
            }

            @Override
            public String getUsername() {
                return "username";
            }

            @Override
            public Date getDate() {
                return new Date();
            }
        });

        return questionnaireDto;
    }

    public static Subject genSubject() {
        Subject subject = new Subject();

        subject.setName("name");

        return subject;
    }

    public static Subject genSubject(String name) {
        Subject subject = new Subject();

        subject.setName(name);

        return subject;
    }

    public static Question genQuestion(String name, RulesCategory category, List<Rule> rules, User user, Subject subject) {

        Question question = new Question();

        question.setReference("ref");
        question.setLabel(name);
        question.setRulesCategory(category);
        question.setSubject(subject);
        question.setRules(rules);
        question.setUser(user);
        question.setState(State.ACTIVE.toString());
        question.setDate(new Date());

        return question;
    }

    public static Question genQuestion(RulesCategory category, List<Rule> rules, User user, Subject subject) {

        Question question = new Question();

        question.setReference("ref");
        question.setLabel("label");
        question.setRulesCategory(category);
        question.setSubject(subject);
        question.setRules(rules);
        question.setUser(user);
        question.setState("state");
        question.setDate(new Date());

        return question;
    }

    public static Rule genRule(String ref, RulesCategory category, List<RuleImpact> impacts, User user) {

        Rule rule = new Rule();

        rule.setReference(ref);
        rule.setRulesCategory(category);
//        rule.setRuleImpacts(impacts);
        rule.setOrigin("origin");
        rule.setLabel("label");
        rule.setUser(user);
        rule.setDate(new Date());

        return rule;
    }

    @NonNull
    public static List<RulesetDtoWrapper> generateRulesetDtoWrapperList(int count) {
        List<RulesetDtoWrapper> resultList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            resultList.add(generateRulesetDtoWrapper(i));
        }

        return resultList;
    }

    @NonNull
    public static RulesetDtoWrapper generateRulesetDtoWrapper(int id) {
        RulesetDtoWrapper rulesetDto = new RulesetDtoWrapper();
        rulesetDto.setDto(new RulesetDto() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public String getReference() {
                return "ref" + id;
            }

            @Override
            public String getName() {
                return "name" + id;
            }

            @Override
            public boolean getPublished() {
                return false;
            }

            @Override
            public Integer getVersion() {
                return 1;
            }

            @Override
            public String getRulesCategoryName() {
                return "rulesCatName";
            }

            @Override
            public String getLanguage() {
                return "fr";
            }

            @Override
            public String getComment() {
                return "comment"+id;
            }

            @Override
            public String getUsername() {
                return "username";
            }

            @Override
            public Date getDate() {
                return new Date();
            }
        });

        return rulesetDto;
    }

    public static User genUser() {
        User user = new User();
        user.setUsername("Mister Test");
        user.setPasswordHash(randomAlphabetic(10));
        user.setEmail("test@test.com");
        return user;
    }

    public static ImpactValue genImpactValue(Long id, String name) {
        ImpactValue impactValue = new ImpactValue();
        impactValue.setId(id);
        impactValue.setName(name);
        impactValue.setEditable(false);
        return impactValue;
    }

    public static ImpactValue genImpactValue() {
        return genImpactValue(RandomUtils.nextLong(1L, 10L), randomAlphabetic(10));
    }

    public static ImpactValue genDefaultImpactValue() {
        ImpactValue impactValue = new ImpactValue();

        impactValue.setId(1L);
        impactValue.setName(randomAlphabetic(10));

        return impactValue;
    }

    public static RulesCategory genRulesCategory(ImpactValue impactValue) {
        return genRulesCategory(randomAlphabetic(10), impactValue);
    }

    public static RulesCategory genRulesCategory(String name, ImpactValue impactValue) {

        RulesCategory rulesCategory = new RulesCategory();
        rulesCategory.setName(name);
        rulesCategory.setDefaultImpact(impactValue);

        List<ImpactValue> impactValues = new ArrayList<>();
        impactValues.add(impactValue);

        rulesCategory.setImpactValues(impactValues);

        return rulesCategory;
    }

    public static Ruleset genRuleset(String name, String ref, Integer version,
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

    public static Ruleset genRuleset(String ref, RulesCategory rulesCategory, QuestionnaireObject questionnaireObject) {

        List<QuestionnaireObject> questionnaireObjects = newArrayList(questionnaireObject);

        return genRuleset(
                randomAlphabetic(10),
                ref,
                nextInt(0, 10),
                rulesCategory,
                randomAlphabetic(2),
                randomAlphabetic(10),
                questionnaireObjects);
    }

    public static QuestionnaireObject genQuestionnaire(String name, String ref, Integer version,
            RulesCategory rulesCategory, Equipment equipment, User author, List<Chain> chains, boolean isPublished) {
        QuestionnaireObject qo = new QuestionnaireObject();
        qo.setReference(ref);
        qo.setVersion(version);
        qo.setName(name);
        qo.setRulesCategory(rulesCategory);
        qo.setEquipment(equipment);
        qo.setPublished(isPublished);
        qo.setChains(chains);
        qo.setState(State.ACTIVE.toString().toLowerCase());
        qo.setDate(new Date());
        qo.setUser(author);

        return qo;
    }

    public static QuestionnaireObject genQuestionnaire(RulesCategory rulesCategory, Equipment equipment,
            Chain chain, User author) {

        return genQuestionnaire(
                randomAlphabetic(10),
                randomAlphabetic(10),
                nextInt(),
                rulesCategory,
                equipment,
                author,
                newArrayList(chain),
                nextBoolean());
    }

    public static ProfileType genProfileType(ImageProfileType icon, List<RulesCategory> categories) {
        ProfileType type = new ProfileType();

        type.setName("name");
        type.setReference("ref");
        type.setRulesCategories(categories);

        type.setIcon(icon);

        return type;
    }

    public static ImageProfileType genImageProfileType() {
        ImageProfileType output = new ImageProfileType();

        output.setImageName(RandomStringUtils.randomAlphabetic(5) + ".jpg");
        output.setExtension("jpg");
        output.setPublished(true);

        return output;
    }

    public static ProfileType genProfileType(String name, List<RulesCategory> categories) {
        ProfileType type = new ProfileType();
        type.setName(name);
        type.setReference(null);
        type.setRulesCategories(categories);
        return type;
    }

    public static RuleImpact genRuleImpact(ImpactValue value, RulesCategory category, ProfileType type) {
        RuleImpact impact = new RuleImpact();
        impact.setImpact(value);
        impact.setRulesCategory(category);
        impact.setProfileType(type);
        return impact;
    }

    public static Chain genChain(String name, String ref, RulesCategory rulesCategory,
            List<Question> questions) {
        Chain chain = new Chain();
        chain.setName(name);
        chain.setReference(ref);
        chain.setRulesCategory(rulesCategory);
        chain.setQuestions(questions);
        return chain;
    }

    public static Chain genChain(RulesCategory rulesCategory) {
        List<Question> questions = newArrayList();
        return genChain(
                randomAlphabetic(10),
                randomAlphabetic(10),
                rulesCategory,
                questions);
    }

    public static ImageEquipment genImageEquipment(String name) {

        ImageEquipment output = new ImageEquipment();
        output.setImageName(name);
        output.setUuid(UUID.randomUUID().toString());
        output.setExtension("jpg");

        return output;
    }

    public static Equipment genEquipment(String name, String ref, List<Category> categories, String type) {
        Equipment output = new Equipment();
        output.setName(name);
        output.setReference(ref);
        output.setCategories(categories);
        output.setType(type);
        output.setSubobjects(newArrayList());
        output.setIllustrations(newArrayList());

        return output;
    }

    public static Equipment genEquipment(String name, String ref, List<Category> categories, String type, String icon) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setReference(ref);
        equipment.setCategories(categories);

        ImageEquipment imageToSave = new ImageEquipment();
        imageToSave.setImageName(icon);
        imageToSave.setUuid(UUID.randomUUID().toString());
        imageToSave.setExtension("jpg");
        equipment.setIcon(imageToSave);

        equipment.setType(type);
        equipment.setSubobjects(newArrayList());
        equipment.setIllustrations(newArrayList());

        return equipment;
    }

    public static Equipment genEquipment(Category category) {
        List<Category> categories = newArrayList(category);
        return genEquipment(
                randomAlphabetic(10),
                randomAlphabetic(10),
                categories,
                randomAlphabetic(10),
                randomAlphabetic(10));
    }

    public static Category genCategory(String name) {
        Category category = new Category();
        category.setName(name);

        return category;
    }

    public static Category genCategory() {
        return genCategory(randomAlphabetic(10));
    }
}
