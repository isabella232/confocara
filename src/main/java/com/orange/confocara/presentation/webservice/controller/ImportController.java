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

package com.orange.confocara.presentation.webservice.controller;

import com.orange.confocara.business.service.*;
import com.orange.confocara.connector.persistence.model.*;
import com.orange.confocara.connector.persistence.model.utils.Role;
import com.orange.confocara.presentation.webservice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Types that carry this annotation are treated as controllers where @RequestMapping
 * methods assume @ResponseBody semantics by default, ie return json body.
 */
@Slf4j
@RestController
/** Rest controller: returns a json body */
public class ImportController {
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IllustrationService illustrationService;
    @Autowired
    private ProfileTypeService profileTypeService;
    @Autowired
    private ImageEquipmentService imageEquipmentService;
    @Autowired
    private ImageProfileTypeService imageProfileTypeService;
    @Autowired
    private ImageIllustrationService imageIllustrationService;
    @Autowired
    private RuleImpactService ruleImpactService;
    @Autowired
    private ImpactValueService impactValueService;
    @Autowired
    private RulesetImportService rulesetService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RulesCategoryService rulesCategoryService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private QuestionnaireObjectService questionnaireObjectService;

    @Value("app.user.default_password")
    private String encryptedPass;

    @RequestMapping(value = "/ws/import/users", method = RequestMethod.POST)
    public void importUsers(@RequestParam int number) {
        Long id = 1L;
        User user = userService.lastOne();
        if (user != null) {
            id = user.getId() + 1L;
        }

        List<User> users = new ArrayList<>();
        UserRole userRole = userRoleService.findByRole(Role.ROLE_USER.toString());
        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);

        for (int i = 0; i < number; i++) {
            User u = new User();
            u.setUsername("user" + id);
            u.setPasswordHash(encryptedPass);
            u.setEmail("user" + id + "@confocara.com");
            u.setUserRoles(roles);
            users.add(u);
            id++;
        }

        userService.create(users);
    }

    @RequestMapping(value = "/ws/import/user", method = RequestMethod.POST)
    public void importUser(@RequestBody UserCredentialsWS user) {
        User userFromDb = userService.getUserByUsername(user.getUsername());
        if (userFromDb == null) {
            UserRole userRole = userRoleService.findByRole(Role.ROLE_USER.toString());
            Set<UserRole> roles = new HashSet<>();
            roles.add(userRole);

            User u = new User();
            u.setUsername(user.getUsername());
            u.setPasswordHash(user.getPassword());
            u.setEmail(user.getEmail());
            u.setUserRoles(roles);

            userService.create(u);
        }
    }

    @RequestMapping(value = "/ws/import/ruleset", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String ruleset(@RequestBody List<RuleSetExport> ruleSets) {
        final List<Ruleset> rulesets = new ArrayList<>();
        for (RuleSetExport ruleSet : ruleSets) {
            rulesets.add(rulesetService.createRuleset(getRuleSetModel(ruleSet)));
        }

        updateRulesCategoryLinkWithProfileTypes();

        return rulesets.isEmpty() ? "NOK" : "OK " + rulesets.size();
    }

    private void updateRulesCategoryLinkWithProfileTypes() {
        List<RulesCategory> allRulesCategories = rulesCategoryService.all();
        for (RulesCategory rulesCategory : allRulesCategories) {
            List<ProfileType> profileTypes = profileTypeService.withRulesCategory(rulesCategory);
            for (ProfileType profileType : profileTypes) {
                if (rulesCategory.getProfileTypes() == null) {
                    rulesCategory.setProfileTypes(new ArrayList<>());
                }
                rulesCategory.getProfileTypes().add(profileType);
            }
        }
        rulesCategoryService.update(allRulesCategories);
    }

    private Ruleset getRuleSetModel(RuleSetExport ruleSet) {

        getIllustrationsListModel(ruleSet.getIllustrations());
        createImpact(ruleSet.getImpactValues());
        createProfileType(ruleSet.getProfileTypes());
        createRule(ruleSet.getRules());
        final RulesCategory rulesCategoryModel = getRulesCategoryModel(ruleSet.getRulesCategory());
        getQuestionList(ruleSet.getQuestions(), rulesCategoryModel);
        getEquipmentsListModel(ruleSet.getObjectDescriptions());

        Ruleset ruleset1 = new Ruleset();
        ruleset1.setApp(ruleSet.getApp());
        ruleset1.setLanguage(ruleSet.getLanguage());
        ruleset1.setReference(ruleSet.getReference());
        ruleset1.setType(ruleSet.getType());
        ruleset1.setComment(ruleSet.getComment());
        ruleset1.setVersion(ruleSet.getVersion());
        ruleset1.setUser(getUserModel(ruleSet.getUserCredentials()));
        ruleset1.setQuestionnaireObjects(getQuestionnaireObjectsListModel(ruleSet.getQuestionnaires(), rulesCategoryModel));
        ruleset1.setDate(getDateToString(ruleSet.getDate()));
        ruleset1.setRulesCategory(rulesCategoryModel);
        return ruleset1;

    }

    private List<Rule> createRule(List<RuleExport> rules) {
        List<Rule> rules1 = new ArrayList<>();
        for (RuleExport ruleExport : rules) {
            rules1.add(getRuleModel(ruleExport));
        }
        return rules1;
    }

    private List<ImpactValue> createImpact(List<ImpactValueExport> impactValues) {
        return getImpactValueListModel(impactValues);
    }

    private List<ProfileType> createProfileType(List<ProfileTypeExport> profileTypes) {
        List<ProfileType> profileTypeList = new ArrayList<>();
        for (ProfileTypeExport profileTypeExport : profileTypes) {
            profileTypeList.add(getProfileTypeModel(profileTypeExport));
        }
        return profileTypeList;
    }

    private List<QuestionnaireObject> getQuestionnaireObjectsListModel(List<QuestionnaireExport> questionnaires, RulesCategory rulesCategoryModel) {
        final List<QuestionnaireObject> questionnaireObjects = new ArrayList<>();
        for (QuestionnaireExport questionnaire : questionnaires) {
            questionnaireObjects.add(getQuestionnaireObjectModel(questionnaire, rulesCategoryModel));
        }
        return questionnaireObjects;
    }

    private QuestionnaireObject getQuestionnaireObjectModel(QuestionnaireExport questionnaire, RulesCategory rulesCategoryModel) {
        final QuestionnaireObject questionnaireObject1 = questionnaireObjectService.withReference(questionnaire.getReference());
        if (questionnaireObject1 != null) {
            return questionnaireObject1;
        } else {
            final QuestionnaireObject questionnaireObject = new QuestionnaireObject();
            questionnaireObject.setReference(questionnaire.getReference());
            questionnaireObject.setEquipment(getEquipmentsModelRef(questionnaire.getObjectDescriptionRef()));
            questionnaireObject.setChains(getChainListModel(questionnaire.getChains(), rulesCategoryModel));
            questionnaireObject.setDate(getDateToString(questionnaire.getDate()));
            questionnaireObject.setVersion(questionnaire.getVersion());
            questionnaireObject.setListPositionAndChainRefMap();
            questionnaireObject.setName(questionnaire.getName());
            questionnaireObject.setRulesCategory(rulesCategoryModel);
            return questionnaireObjectService.create(questionnaireObject);
        }
    }

    private Equipment getEquipmentsModelRef(String objectDescriptionRef) {
        return equipmentService.withReference(objectDescriptionRef);
    }

    private List<Chain> getChainListModel(List<ChainExport> chains, RulesCategory rulesCategoryModel) {
        List<Chain> chainList = new ArrayList<>();
        for (ChainExport chain : chains) {
            chainList.add(getChainModel(chain, rulesCategoryModel));
        }
        return chainList;
    }

    private Chain getChainModel(ChainExport chain, RulesCategory rulesCategoryModel) {
        final Chain chain2 = chainService.withReference(chain.getReference());
        if (chain2 != null) {
            return chain2;
        } else {
            final Chain chain1 = new Chain();
            chain1.setReference(chain.getReference());
            chain1.setName(chain.getName());
            chain1.setQuestions(getQuestionListFormRef(chain.getQuestionsRef()));
            chain1.setDate(getDateToString(chain.getDate()));
            chain1.setListPositionAndQuestionRefMap();
            chain1.setRulesCategory(rulesCategoryModel);
            return chainService.create(chain1);
        }
    }

    private List<Question> getQuestionListFormRef(List<String> questionsRef) {
        List<Question> questions = new ArrayList<>();
        for (String ref : questionsRef) {
            questions.add(questionService.withReference(ref));
        }
        return questions;
    }

    private List<Question> getQuestionList(List<QuestionExport> questions, RulesCategory rulesCategoryModel) {
        List<Question> questionList = new ArrayList<>();
        for (QuestionExport question : questions) {
            questionList.add(getQuestionModel(question, rulesCategoryModel));
        }
        return questionList;
    }

    private Question getQuestionModel(QuestionExport question, RulesCategory rulesCategoryModel) {
        final Question question2 = questionService.withReference(question.getReference());
        if (question2 != null) {
            return question2;
        } else {
            Question question1 = new Question();
            question1.setReference(question.getReference());
            question1.setLabel(question.getLabel());
            question1.setState(question.getState());
            question1.setSubject(getSubjectModel(question.getSubject()));
            question1.setRules(getRulesListModelFromRef(question.getRulesRef()));
            question1.setDate(getDateToString(question.getDate()));
            question1.setRulesCategory(rulesCategoryModel);
            return questionService.create(question1);
        }
    }

    private List<Rule> getRulesListModelFromRef(List<String> rulesRef) {
        List<Rule> ruleList = new ArrayList<>();
        for (String ref : rulesRef) {
            ruleList.add(ruleService.withReference(ref));
        }
        return ruleList;
    }

    private Rule getRuleModel(RuleExport rule) {
        final Rule rule2 = ruleService.withReference(rule.getReference());
        if (rule2 != null) {
            return rule2;
        } else {
            final Rule rule1 = new Rule();
            rule1.setReference(rule.getReference());
            rule1.setLabel(rule.getLabel());
            rule1.setOrigin(rule.getOrigin());
            final List<RuleImpact> rulImpactListModel = getRulImpactListModel(rule.getRuleImpacts());

            rule1.setRulesCategory(rulImpactListModel.get(0).getProfileType().getRulesCategories().get(0));
            rule1.setUser(getUserModel(rule.getUserCredentials()));

            rule1.setIllustrations(getIllustrationsListModelFormRef(rule.getIllustration()));
            rule1.setRuleImpacts(rulImpactListModel);
            rule1.setDate(getDateToString(rule.getDate()));
            return ruleService.create(rule1);
        }
    }

    private List<Illustration> getIllustrationsListModelFormRef(List<String> illustration) {
        List<Illustration> illustrations = new ArrayList<>();
        if (illustration != null) {
            for (String ref : illustration) {
                illustrations.add(illustrationService.withReference(ref));
            }
        }
        return illustrations;
    }

    private List<RuleImpact> getRulImpactListModel(List<RuleImpactExport> ruleImpacts) {
        List<RuleImpact> ruleImpactList = new ArrayList<>();
        for (RuleImpactExport ruleImpact : ruleImpacts) {
            ruleImpactList.add(getRuleImpactModel(ruleImpact));
        }
        return ruleImpactList;
    }

    private RuleImpact getRuleImpactModel(RuleImpactExport ruleImpact) {
        final RuleImpact ruleImpactFromDb = ruleImpactService.findById(Long.parseLong(ruleImpact.getReference()));
        if (ruleImpactFromDb != null) {
            return ruleImpactFromDb;
        } else {
            RuleImpact ruleImpact1 = new RuleImpact();
            ruleImpact1.setProfileType(getProfileTypeModelFromRef(ruleImpact.getProfileTypeRef()));
            final ImpactValue impactValueModelFormRef = getImpactValueModelFormRef(ruleImpact.getImpactValueRef());
            ruleImpact1.setImpact(impactValueModelFormRef);
            return ruleImpactService.create(ruleImpact1);
        }
    }

    private ImpactValue getImpactValueModelFormRef(String impactValueRef) {
        return impactValueService.withId(Long.parseLong(impactValueRef));
    }

    private ProfileType getProfileTypeModelFromRef(String profileTypeRef) {
        return profileTypeService.withReference(profileTypeRef);
    }

    private List<ImpactValue> getImpactValueListModel(List<ImpactValueExport> impactValues) {
        List<ImpactValue> impactValueList = new ArrayList<>();
        for (ImpactValueExport impactValue : impactValues) {
            impactValueList.add(getImpactValueModel(impactValue));
        }
        return impactValueList;
    }

    private ImpactValue getImpactValueModel(ImpactValueExport impactValue) {
        final ImpactValue impactValueFromDb = impactValueService.findByName(impactValue.getName());
        if (impactValueFromDb != null) {
            return impactValueFromDb;
        } else {
            ImpactValue impactValue1 = new ImpactValue();
            impactValue1.setName(impactValue.getName());
            impactValue1.setEditable(impactValue.isEditable());
            return impactValueService.create(impactValue1);
        }
    }

    private ProfileType getProfileTypeModel(ProfileTypeExport profileType) {
        final ProfileType profileType2 = profileTypeService.withReference(profileType.getReference());
        if (profileType2 != null) {
            return profileType2;
        } else {
            ProfileType profileType1 = ProfileType.builder().build();
            profileType1.setReference(profileType.getReference());
            profileType1.setName(profileType.getName());

            if (profileType.getIcon() != null) {
                ImageProfileType image = imageProfileTypeService.create(profileType.getIcon().replaceAll("\\s", "-"));
                profileType1.setIcon(image);
            }

            profileType1.setRulesCategories(getRulesCategoryListModel(profileType.getRulesCategories()));
            ProfileType savedProfileType = profileTypeService.create(profileType1);

            if (savedProfileType.getIcon() != null) {
                imageProfileTypeService.updateProfileType(savedProfileType.getIcon(), savedProfileType);
            }

            return savedProfileType;
        }
    }

    private List<RulesCategory> getRulesCategoryListModel(List<RulesCategoryExport> rulesCategories) {
        List<RulesCategory> categories = new ArrayList<>();
        for (RulesCategoryExport rulesCategoryImport : rulesCategories) {
            categories.add(getRulesCategoryModel(rulesCategoryImport));
        }
        return categories;
    }

    private RulesCategory getRulesCategoryModel(RulesCategoryExport rulesCategory) {
        final RulesCategory byName = rulesCategoryService.findByName(rulesCategory.getName());
        if (byName != null) {
            return byName;
        } else {
            RulesCategory rulesCategoryToCreate = new RulesCategory();
            rulesCategoryToCreate.setName(rulesCategory.getName());
            final ImpactValue byName1 = impactValueService.withId(Long.valueOf(rulesCategory.getDefaultImpact()));
            rulesCategoryToCreate.setDefaultImpact(byName1);
            rulesCategoryToCreate.setImpactValues(getImpactValuesFormName(rulesCategory.getAcceptedImpactList()));

            return rulesCategoryService.create(rulesCategoryToCreate);
        }
    }

    private List<ImpactValue> getImpactValuesFormName(List<String> acceptedImpactList) {
        List<ImpactValue> impactValues = new ArrayList<>();
        for (String impactName : acceptedImpactList) {
            final ImpactValue byName = impactValueService.withId(Long.parseLong(impactName));
            impactValues.add(byName);
        }
        return impactValues;
    }

    private Subject getSubjectModel(SubjectWS subject) {
        final Subject subject2 = subjectService.getSubjectByName(subject.getName());
        if (subject2 != null) {
            return subject2;
        } else {
            Subject subject1 = new Subject();
            subject1.setName(subject.getName());
            return subjectService.create(subject1);
        }
    }

    private Equipment getEquipmentModel(ObjectDescriptionExport objectDescription) {
        final Equipment equipment1 = equipmentService.withReference(objectDescription.getReference());
        final Equipment equipment2 = equipmentService.withName(objectDescription.getName());
        if (equipment1 != null) {
            return equipment1;
        } else if (equipment2 != null) {
            return equipment2;
        } else {
            final Equipment equipment = new Equipment();
            equipment.setReference(objectDescription.getReference());
            equipment.setName(objectDescription.getName());

            if (objectDescription.getIcon() != null) {
                ImageEquipment icon = imageEquipmentService.create(objectDescription.getIcon().replaceAll("\\s", "-"));
                equipment.setIcon(icon);
            }

            equipment.setDefinition(objectDescription.getDefinition());
            equipment.setType(objectDescription.getType());
            equipment.setCategories(getCategoryListModel(objectDescription.getCategories()));
            equipment.setIllustrations(getIllustrationsListModelFormRef(objectDescription.getIllustrationRef()));
            equipment.setSubobjects(getEquipmentsListModelRef(objectDescription.getSubObject()));
            equipment.setDate(getDateToString(objectDescription.getDate()));
            Equipment savedEquipment = equipmentService.create(equipment);

            if (savedEquipment.getIcon() != null) {
                imageEquipmentService.updateEquipment(savedEquipment.getIcon(), savedEquipment);
            }

            return savedEquipment;
        }
    }

    private List<Equipment> getEquipmentsListModelRef(List<String> subObject) {
        List<Equipment> equipment = new ArrayList<>();
        for (String ref : subObject) {
            equipment.add(equipmentService.withReference(ref));
        }
        return equipment;

    }

    private List<Equipment> getEquipmentsListModel(List<ObjectDescriptionExport> objects) {
        List<Equipment> equipments = new ArrayList<>();
        for (ObjectDescriptionExport objectDescription1 : objects) {
            equipments.add(getEquipmentModel(objectDescription1));
        }
        return equipments;
    }

    private List<Illustration> getIllustrationsListModel(List<IllustrationWS> illustration) {
        final List<Illustration> illustrations = new ArrayList<>();
        for (IllustrationWS illustration1 : illustration) {
            illustrations.add(getIllustrationModel(illustration1));
        }
        return illustrations;
    }

    private Illustration getIllustrationModel(IllustrationWS illustration1) {
        final Illustration illustration = illustrationService.withReference(illustration1.getReference());
        if (illustration != null) {
            return illustration;
        } else {
            Illustration illustration2 = new Illustration();
            illustration2.setReference(illustration1.getReference());
            illustration2.setTitle(illustration1.getTitle());

            if (illustration1.getImage() != null) {
                ImageIllustration image = imageIllustrationService.create(illustration1.getImage().replaceAll("\\s", "-"));
                illustration2.setImage(image);
            }

            illustration2.setComment(illustration1.getComment());
            illustration2.setDate(getDateToString(illustration1.getDate()));
            Illustration savedIllustration = illustrationService.create(illustration2);

            if (savedIllustration.getImage() != null) {
                imageIllustrationService.updateIllustration(savedIllustration.getImage(), savedIllustration);
            }

            return savedIllustration;
        }

    }

    private List<Category> getCategoryListModel(List<CategoryWS> categoryWSList) {
        final List<Category> categories = new ArrayList<>();
        for (CategoryWS categoryWS : categoryWSList) {
            categories.add(getCategoryModel(categoryWS));
        }
        return categories;
    }

    private Category getCategoryModel(CategoryWS category) {
        final String name = category.getName();
        final Category categoryByName = categoryService.getCategoryByName(name);
        if (categoryByName != null) {
            return categoryByName;
        } else {
            Category category1 = new Category();
            category1.setName(category.getName());
            return categoryService.create(category1);
        }
    }

    private Date getDateToString(String date) {
        if (date == null) {
            return null;
        }
        if (date.isEmpty()) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        final Date parse;
        try {
            parse = formatter.parse(date);
            return parse;
        } catch (ParseException e) {
            Logger.getAnonymousLogger().log(Level.OFF, e.getMessage());
            return null;
        }

    }

    private User getUserModel(@NotNull UserCredentialsWS userCredentials) {
        if (userCredentials.getUsername() != null && !"admin".equals(userCredentials.getUsername())) {
            User user = userService.getUserByUsername(userCredentials.getUsername());

            if (user != null) {
                return user;
            }
        }

        return null;
    }
}
