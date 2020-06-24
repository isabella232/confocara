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

package com.orange.confocara.business.service.transform;

import com.google.common.collect.Lists;
import com.orange.confocara.connector.persistence.model.Category;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
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
import com.orange.confocara.presentation.webservice.model.CategoryWS;
import com.orange.confocara.presentation.webservice.model.ChainExport;
import com.orange.confocara.presentation.webservice.model.IllustrationWS;
import com.orange.confocara.presentation.webservice.model.ImpactValueExport;
import com.orange.confocara.presentation.webservice.model.ObjectDescriptionExport;
import com.orange.confocara.presentation.webservice.model.ProfileTypeExport;
import com.orange.confocara.presentation.webservice.model.QuestionExport;
import com.orange.confocara.presentation.webservice.model.QuestionnaireExport;
import com.orange.confocara.presentation.webservice.model.QuestionnaireGroup;
import com.orange.confocara.presentation.webservice.model.RuleExport;
import com.orange.confocara.presentation.webservice.model.RuleImpactExport;
import com.orange.confocara.presentation.webservice.model.RuleSetExport;
import com.orange.confocara.presentation.webservice.model.RulesCategoryExport;
import com.orange.confocara.presentation.webservice.model.SubjectWS;
import com.orange.confocara.presentation.webservice.model.UserCredentialsWS;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Function that transforms a {@link Ruleset} into a {@link RuleSetExport}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RulesetToRulesetExportFunction implements Function<Ruleset, RuleSetExport> {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    @Override
    public RuleSetExport apply(Ruleset ruleset) {

        return exportJson(ruleset);
    }

    private RuleSetExport exportJson(Ruleset ruleset) {
        if (ruleset != null) {
            RuleSetExport ruleSet = rulesetExport(ruleset);

            ruleSet.setQuestionnaireGroups(getQuestionnaireGroup(ruleset.getQuestionnaireObjects()));

            Map<Long, Equipment> equipments = new HashMap<>();
            Map<Long, Rule> rules = new HashMap<>();
            Map<Long, Question> questionsMap = new HashMap<>();
            Map<Long, Illustration> illustrations = new HashMap<>();
            Map<Long, ImpactValue> impactValues = new HashMap<>();
            Map<Long, ProfileType> profileTypes = new HashMap<>();

            final Map<Long, QuestionnaireObject> questionnaireObjects = getQuestionnaireObjectMap(ruleset);
            final Map<Long, String> questionnaireObjectsByEquipmentId = getLinkedQuestionnaireObjectMap(ruleset);

            List<QuestionnaireObject> questionnaireObjectList = new ArrayList<>(questionnaireObjects.values());

            for (QuestionnaireObject questionnaireObject : questionnaireObjectList) {
                processEquipments(equipments, illustrations, questionnaireObject);

                final List<Chain> chains = questionnaireObject.getChains();
                for (Chain chain : chains) {
                    final List<Question> questions = chain.getQuestions();
                    for (Question question : questions) {
                        addQuestionToMap(questionsMap, question);
                        final List<Rule> rules1 = question.getRules();
                        for (Rule rule : rules1) {
                            addRuleToMap(rules, rule);
                            addAllImpactsToMap(impactValues, profileTypes, rule);
                            addIllustrationsToMap(illustrations, rule.getIllustrations());
                        }
                    }
                }
            }

            ruleSet.setProfileTypes(profileTypeExports(new ArrayList<>(profileTypes.values())));
            ruleSet.setObjectDescriptions(getObjectDescriptionList(new ArrayList<>(equipments.values()), questionnaireObjectsByEquipmentId));
            ruleSet.setQuestions(getQuestionList(new ArrayList<>(questionsMap.values())));
            ruleSet.setQuestionnaires(getQuestionnaireList(new ArrayList<>(questionnaireObjects.values())));
            ruleSet.setRules(getRuleList(new ArrayList<>(rules.values())));
            ruleSet.setIllustrations(getIllustrationList(new ArrayList<>(illustrations.values())));
            ruleSet.setImpactValues(getImpactValueList(new ArrayList<>(impactValues.values())));
            ruleSet.setDate(getDateToString(ruleset.getDate()));

            return ruleSet;
        }
        return null;

    }

    private void addAllImpactsToMap(Map<Long, ImpactValue> impactValues, Map<Long, ProfileType> profileTypes, Rule rule) {
        for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
            addImpactValueToMap(impactValues, ruleImpact);
            final ProfileType profileType = ruleImpact.getProfileType();
            addProfileTypeToMap(profileTypes, profileType);

            final List<RulesCategory> rulesCategories = profileType.getRulesCategories();
            addRulesCategoryImpactsToMap(impactValues, rulesCategories);
        }
    }

    private void addRuleToMap(Map<Long, Rule> rules, Rule rule) {
        if (!rules.containsKey(rule.getId())) {
            rules.put(rule.getId(), rule);
        }
    }

    private void addRulesCategoryImpactsToMap(Map<Long, ImpactValue> impactValues, List<RulesCategory> rulesCategories) {
        for (RulesCategory rulesCategory : rulesCategories) {
            if (!impactValues.containsKey(rulesCategory.getDefaultImpact().getId())) {
                impactValues.put(rulesCategory.getDefaultImpact().getId(), rulesCategory.getDefaultImpact());
            }

            for (ImpactValue value : rulesCategory.getImpactValues()) {
                if (!impactValues.containsKey(value.getId())) {
                    impactValues.put(value.getId(), value);
                }
            }
        }
    }

    private void addImpactValueToMap(Map<Long, ImpactValue> impactValues, RuleImpact ruleImpact) {
        final ImpactValue impactValue = ruleImpact.getImpact();
        if (!impactValues.containsKey(impactValue.getId())) {
            impactValues.put(impactValue.getId(), impactValue);
        }
    }

    private void addProfileTypeToMap(Map<Long, ProfileType> profileTypes, ProfileType profileType) {
        if (!profileTypes.containsKey(profileType.getId())) {
            profileTypes.put(profileType.getId(), profileType);
        }
    }

    private void addQuestionToMap(Map<Long, Question> questionsMap, Question question) {
        if (!questionsMap.containsKey(question.getId())) {
            questionsMap.put(question.getId(), question);
        }
    }

    private void processEquipments(Map<Long, Equipment> equipments, Map<Long, Illustration> illustrations, QuestionnaireObject questionnaireObject) {
        final Equipment equipment = questionnaireObject.getEquipment();

        addIllustrationsToMap(illustrations, equipment.getIllustrations());
        addEquipmentToMap(equipments, equipment);

        for (Equipment subEquipment : equipment.getSubobjects()) {

            addEquipmentToMap(equipments, subEquipment);
            addIllustrationsToMap(illustrations, subEquipment.getIllustrations());
        }
    }

    private void addEquipmentToMap(Map<Long, Equipment> equipments, Equipment equipment) {
        if (!equipments.containsKey(equipment.getId())) {
            equipments.put(equipment.getId(), equipment);
        }
    }

    private void addIllustrationsToMap(Map<Long, Illustration> illustrationsMap, List<Illustration> illustrations) {
        for (Illustration illustration : illustrations) {
            if (!illustrationsMap.containsKey(illustration.getId())) {
                illustrationsMap.put(illustration.getId(), illustration);
            }
        }
    }

    private Map<Long, QuestionnaireObject> getQuestionnaireObjectMap(Ruleset ruleset) {
        final Map<Long, QuestionnaireObject> questionnaireObjects = new HashMap<>();
        for (QuestionnaireObject questionnaireObject : ruleset.getQuestionnaireObjects()) {
            if (!questionnaireObjects.containsKey(questionnaireObject.getId())) {
                questionnaireObjects.put(questionnaireObject.getId(), questionnaireObject);
                for (QuestionnaireObject subQuestionnaire : questionnaireObject.getQuestionnaireSubObjects()) {
                    if (!questionnaireObjects.containsKey(subQuestionnaire.getId())) {
                        setParentObjectToSubQuestionnaire(questionnaireObjects, questionnaireObject, subQuestionnaire);
                    } else {
                        addParentObjectToSubQuestionnaire(questionnaireObjects, questionnaireObject, subQuestionnaire);
                    }
                }
            }
        }
        return questionnaireObjects;
    }

    /**
     * Returns the questionnaire reference that are directly connected with the ruleset, mapped by questionnaire equipment id
     *
     * @param ruleset the ruleset to export
     * @return the questionnaire reference that are directly connected with the ruleset, mapped by questionnaire equipment id
     */
    private Map<Long, String> getLinkedQuestionnaireObjectMap(Ruleset ruleset) {
        final Map<Long, String> questionnaireObjects = new HashMap<>();

        for (QuestionnaireObject questionnaireObject : ruleset.getQuestionnaireObjects()) {
            questionnaireObjects.put(questionnaireObject.getEquipment().getId(), questionnaireObject.getReference());
        }
        return questionnaireObjects;
    }

    private void addParentObjectToSubQuestionnaire(Map<Long, QuestionnaireObject> questionnaireObjects, QuestionnaireObject questionnaireObject, QuestionnaireObject subQuestionnaire) {
        List<String> parentObjectRefs = questionnaireObjects.get(subQuestionnaire.getId()).getParentObjectRefs();
        if (parentObjectRefs == null) {
            List<String> parentObject = new ArrayList<>();
            parentObject.add(questionnaireObject.getEquipment().getReference());
            questionnaireObjects.get(subQuestionnaire.getId()).setParentObjectRefs(parentObject);
        } else if (!parentObjectRefs.contains(questionnaireObject.getEquipment().getReference())) {
            parentObjectRefs.add(questionnaireObject.getEquipment().getReference());
        }
    }

    private void setParentObjectToSubQuestionnaire(Map<Long, QuestionnaireObject> questionnaireObjects, QuestionnaireObject questionnaireObject, QuestionnaireObject subQuestionnaire) {
        List<String> parentObject = new ArrayList<>();
        parentObject.add(questionnaireObject.getEquipment().getReference());
        subQuestionnaire.setParentObjectRefs(parentObject);
        questionnaireObjects.put(subQuestionnaire.getId(), subQuestionnaire);
    }

    private RuleSetExport rulesetExport(Ruleset input) {
        RuleSetExport output = new RuleSetExport();
        output.setApp(input.getApp());
        output.setVersion(input.getVersion());
        output.setLanguage(input.getLanguage());
        output.setReference(input.getReference());
        output.setState(input.getState());
        output.setType(input.getType());
        output.setUserCredentials(getUserCredentials(input.getUser()));
        output.setComment(input.getComment());

        return output;
    }

    private List<ProfileTypeExport> profileTypeExports(List<ProfileType> profileTypes) {
        List<ProfileTypeExport> profileTypeEx = new ArrayList<>();
        for (ProfileType profileType : profileTypes) {
            profileTypeEx.add(getProfileType(profileType));
        }
        return profileTypeEx;
    }


    private List<QuestionnaireGroup> getQuestionnaireGroup(List<QuestionnaireObject> questionnaireObjects) {
        List<QuestionnaireGroup> questionnaireGroups = new ArrayList<>();

        List<Category> categories = new ArrayList<>();
        for (QuestionnaireObject questionnaireObject : questionnaireObjects) {
            for (Category category : questionnaireObject.getEquipment().getCategories()) {
                if (!categories.contains(category)) {
                    categories.add(category);
                }
            }
        }
        for (Category category : categories) {
            final QuestionnaireGroup questionnaireGroup = new QuestionnaireGroup();
            questionnaireGroup.setName(category.getName());
            final List<String> refs = new ArrayList<>();
            for (QuestionnaireObject questionnaireObject : questionnaireObjects) {
                final Equipment equipment = questionnaireObject.getEquipment();
                if (equipment.getCategories().contains(category)) {
                    refs.add(equipment.getReference());
                }
            }
            questionnaireGroup.setObjectRef(refs);
            questionnaireGroups.add(questionnaireGroup);
        }
        return questionnaireGroups;
    }

    private String getDateToString(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT);
        return date != null ? formater.format(date) : "";
    }

    private List<QuestionnaireExport> getQuestionnaireList(List<QuestionnaireObject> questionnaireObjects) {
        List<QuestionnaireExport> questionnaireList = new ArrayList<>();
        for (QuestionnaireObject questionnaireObject : questionnaireObjects) {
            final QuestionnaireExport questionnaire = getQuestionnaire(questionnaireObject);
            if (questionnaire != null) {
                questionnaireList.add(questionnaire);
            }
        }
        return questionnaireList;
    }

    private QuestionnaireExport getQuestionnaire(QuestionnaireObject questionnaireObject) {
        if (questionnaireObject != null) {
            final QuestionnaireExport questionnaire = new QuestionnaireExport();
            questionnaire.setReference(questionnaireObject.getReference());
            questionnaire.setObjectDescriptionRef(questionnaireObject.getEquipment().getReference());
            questionnaire.setChains(getChainList(questionnaireObject.getChains()));
            questionnaire.setDate(getDateToString(questionnaireObject.getDate()));
            questionnaire.setVersion(questionnaireObject.getVersion());
            questionnaire.setParentObjectRefs(questionnaireObject.getParentObjectRefs());

            return questionnaire;
        }
        return null;
    }

    private UserCredentialsWS getUserCredentials(User user) {
        final UserCredentialsWS userCredentials = new UserCredentialsWS();
        if (user != null) {
            userCredentials.setUsername(user.getUsername());
        }

        return userCredentials;
    }

    private List<IllustrationWS> getIllustrationList(List<Illustration> illustrations) {
        List<IllustrationWS> illustrationWs = new ArrayList<>();
        for (Illustration illustration : illustrations) {
            illustrationWs.add(getIllustration(illustration));
        }
        return illustrationWs;
    }

    private IllustrationWS getIllustration(Illustration illustration) {
        final IllustrationWS illustration1 = new IllustrationWS();
        illustration1.setReference(illustration.getReference());
        illustration1.setImage(illustration.getImage() != null ? illustration.getImage().getFileNameWithExtension() : "");
        illustration1.setComment(illustration.getComment());
        illustration1.setDate(getDateToString(illustration.getDate()));
        return illustration1;
    }

    private List<ChainExport> getChainList(List<Chain> chains) {
        List<ChainExport> chainList = new ArrayList<>();
        for (Chain chain : chains) {
            chainList.add(getChain(chain));
        }
        return chainList;
    }

    private ChainExport getChain(Chain chain) {
        final ChainExport chain1 = new ChainExport();
        chain1.setReference(chain.getReference());
        chain1.setName(chain.getName());
        final List<QuestionExport> questionList = getQuestionList(chain.getQuestions());
        List<String> questionRef = new ArrayList<>();
        for (QuestionExport questionExport : questionList) {
            questionRef.add(questionExport.getReference());
        }
        chain1.setQuestionsRef(questionRef);
        chain1.setDate(getDateToString(chain.getDate()));
        return chain1;
    }

    private List<QuestionExport> getQuestionList(List<Question> questions1) {
        final List<QuestionExport> questions = new ArrayList<>();
        for (Question question : questions1) {
            questions.add(getQuestion(question));
        }
        return questions;
    }

    private QuestionExport getQuestion(Question question) {
        final QuestionExport question1 = new QuestionExport();
        question1.setReference(question.getReference());
        question1.setLabel(question.getLabel());
        question1.setState(question.getState());
        question1.setSubject(getSubject(question.getSubject()));
        question1.setDate(getDateToString(question.getDate()));

        List<String> output;
        if (question.getRulesOrder() == null || question.getRulesOrder().isEmpty()) {
            if (question.getRules() != null) {
                output = question.getRules()
                        .stream()
                        .map(Rule::getReference)
                        .collect(Collectors.toList());
            } else {
                output = Lists.newArrayList();
            }
        } else {
            output = Lists.newArrayList(question.getRulesOrder().split(","));
        }
        question1.setRulesRef(output);

        return question1;
    }

    private List<ObjectDescriptionExport> getObjectDescriptionList(List<Equipment> equipments,
            Map<Long, String> linkedQuestionnairesMappedByEquipmentId) {
        List<ObjectDescriptionExport> objects = new ArrayList<>();
        if (equipments != null) {
            for (Equipment equipment1 : equipments) {
                String questionnaireRef = null;
                if (linkedQuestionnairesMappedByEquipmentId.containsKey(equipment1.getId())) {
                    questionnaireRef = linkedQuestionnairesMappedByEquipmentId.get(equipment1.getId());
                }
                objects.add(getObjectDescription(equipment1, questionnaireRef));
            }
        }
        return objects;
    }

    private ObjectDescriptionExport getObjectDescription(Equipment equipment, String questionnaireRef) {

        final ObjectDescriptionExport objectDescription = new ObjectDescriptionExport();
        objectDescription.setReference(equipment.getReference());
        objectDescription.setName(equipment.getName());
        objectDescription.setIcon(equipment.getIcon().getFileNameWithExtension());
        objectDescription.setDefinition(equipment.getDefinition());
        objectDescription.setType(equipment.getType());
        objectDescription.setQuestionnaireRef(questionnaireRef);

        final List<IllustrationWS> illustrationList = getIllustrationList(equipment.getIllustrations());
        List<String> illustrationRef = new ArrayList<>();
        for (IllustrationWS illustration : illustrationList) {
            illustrationRef.add(illustration.getReference());
        }
        objectDescription.setIllustrationRef(illustrationRef);
        objectDescription.setCategories(getCategoryList(equipment.getCategories()));
        objectDescription.setSubObject(equipment.getSubobjects().stream().map(Equipment::getReference).collect(
                Collectors.toList()));
        objectDescription.setDate(getDateToString(equipment.getDate()));
        return objectDescription;
    }

    private List<RuleExport> getRuleList(List<Rule> rules) {
        List<RuleExport> rules1 = new ArrayList<>();
        for (Rule rule : rules) {
            rules1.add(getRule(rule));
        }
        return rules1;
    }

    private RuleExport getRule(Rule rule) {
        final RuleExport rule1 = new RuleExport();
        rule1.setReference(rule.getReference());
        rule1.setLabel(rule.getLabel());
        rule1.setUserCredentials(getUserCredentials(rule.getUser()));
        final List<IllustrationWS> illustrationList = getIllustrationList(rule.getIllustrations());
        List<String> illustrationListRef = new ArrayList<>();
        for (IllustrationWS illustration : illustrationList) {
            illustrationListRef.add(illustration.getReference());
        }
        rule1.setIllustration(illustrationListRef);
        rule1.setRuleImpacts(getRuleImpactsList(rule.getRuleImpacts()));
        rule1.setDate(getDateToString(rule.getDate()));
        return rule1;
    }

    private List<RuleImpactExport> getRuleImpactsList(List<RuleImpact> ruleImpacts) {
        List<RuleImpactExport> ruleImpactsList = new ArrayList<>();
        for (RuleImpact ruleImpact : ruleImpacts) {
            ruleImpactsList.add(getRuleImpact(ruleImpact));
        }
        return ruleImpactsList;
    }

    private RuleImpactExport getRuleImpact(RuleImpact ruleImpact) {
        RuleImpactExport ruleImpact1 = new RuleImpactExport();
        ruleImpact1.setImpactValueRef(getImpactValue(ruleImpact.getImpact()).getReference());
        ruleImpact1.setProfileTypeRef(getProfileType(ruleImpact.getProfileType()).getReference());
        ruleImpact1.setReference(String.valueOf(ruleImpact.getId()));
        return ruleImpact1;
    }

    private List<ImpactValueExport> getImpactValueList(List<ImpactValue> impactValues) {
        List<ImpactValueExport> impactValuesList = new ArrayList<>();
        for (ImpactValue impactValue : impactValues) {
            impactValuesList.add(getImpactValue(impactValue));
        }
        return impactValuesList;
    }

    private ImpactValueExport getImpactValue(ImpactValue impactValue) {
        ImpactValueExport impactValue1 = new ImpactValueExport();
        impactValue1.setReference(String.valueOf(impactValue.getId()));
        impactValue1.setName(impactValue.getName());
        impactValue1.setEditable(impactValue.isEditable());
        return impactValue1;
    }

    private ProfileTypeExport getProfileType(ProfileType profileType) {
        ProfileTypeExport profileType1 = new ProfileTypeExport();
        profileType1.setReference(profileType.getReference());
        profileType1.setName(profileType.getName());
        profileType1.setIcon(profileType.getIcon().getFileNameWithExtension());
        profileType1.setRulesCategories(getRulesCategoryList(profileType.getRulesCategories()));
        return profileType1;
    }

    private List<RulesCategoryExport> getRulesCategoryList(List<RulesCategory> rulesCategories) {
        List<RulesCategoryExport> categoriesList = new ArrayList<>();
        for (RulesCategory rulesCategory : rulesCategories) {
            categoriesList.add(getRulesCategory(rulesCategory));
        }
        return categoriesList;
    }

    private RulesCategoryExport getRulesCategory(RulesCategory rulesCategory) {
        RulesCategoryExport rulesCategory1 = new RulesCategoryExport();
        rulesCategory1.setName(rulesCategory.getName());
        rulesCategory1.setDefaultImpact(getImpactValue(rulesCategory.getDefaultImpact()).getReference());
        final List<ImpactValueExport> impactValueList = getImpactValueList(rulesCategory.getImpactValues());
        List<String> impactRef = new ArrayList<>();
        for (ImpactValueExport impactValue : impactValueList) {
            impactRef.add(impactValue.getReference());
        }
        rulesCategory1.setAcceptedImpactList(impactRef);
        return rulesCategory1;
    }

    private SubjectWS getSubject(Subject subject) {
        final SubjectWS subject1 = new SubjectWS();
        subject1.setName(subject.getName());
        return subject1;
    }

    private List<CategoryWS> getCategoryList(List<Category> categories) {
        List<CategoryWS> categoriesList = new ArrayList<>();
        for (Category category : categories) {
            categoriesList.add(getCategory(category));
        }
        return categoriesList;
    }

    private CategoryWS getCategory(Category category1) {
        final CategoryWS category = new CategoryWS();
        category.setName(category1.getName());
        return category;
    }
}
