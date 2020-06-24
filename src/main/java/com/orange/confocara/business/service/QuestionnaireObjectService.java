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

import static org.hibernate.envers.query.AuditEntity.property;
import static org.hibernate.envers.query.AuditEntity.revisionNumber;

import com.orange.confocara.connector.persistence.dto.QOChainsAndSubQuestionnairesDtoWrapper;
import com.orange.confocara.connector.persistence.dto.SubobjectQuestionnaireDto;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Equipment;
import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RuleImpact;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionnaireObjectService {

    private static final String PUBLISHED_PROPERTY = "published";
    private static final String REFERENCE_PROPERTY = "reference";
    private static final String VERSION_PROPERTY = "version";

    @Autowired
    QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    RulesetRepository rulesetRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<QuestionnaireObject> all() {
        List<QuestionnaireObject> all = questionnaireObjectRepository.findAllByOrderByIdDesc();
        all.sort(new Comparator<QuestionnaireObject>() {
            public int compare(QuestionnaireObject s1, QuestionnaireObject s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        return all;
    }

    public List<QuestionnaireObject> withIds(List<Long> ids) {
        return (List<QuestionnaireObject>) questionnaireObjectRepository.findAll(ids);
    }

    public QuestionnaireObject withId(Long id) {
        return questionnaireObjectRepository.findOne(id);
    }

    public QuestionnaireObject withReference(String reference) {
        return questionnaireObjectRepository.findByReference(reference);
    }

    public List<QuestionnaireObject> findByEquipment(Equipment equipment) {
        return questionnaireObjectRepository.findByEquipment(equipment);
    }

    public List<String> findChainsNameByReference(@NonNull String reference) {
        return questionnaireObjectRepository.findChainsNameByReference(reference);
    }

    @Transactional
    public List<QuestionnaireObject> withRulesCategory(RulesCategory rulesCategory) {
        return questionnaireObjectRepository.findByRulesCategory(rulesCategory);
    }

    public boolean isNameAvailable(String name) {
        return questionnaireObjectRepository.findByName(name.trim().replaceAll("\\s+", " ")) == null;
    }

    public boolean checkNameIsAvailable(QuestionnaireObject input) {
        String name = input.getName().trim().replaceAll("\\s+", " ");
        QuestionnaireObject entity = questionnaireObjectRepository.findByName(name);
        return entity == null || input.getId() == entity.getId();
    }

    @Transactional
    public QuestionnaireObject create(@NonNull QuestionnaireObject questionnaireObject) {
        return questionnaireObjectRepository.save(questionnaireObject);
    }

    @Transactional
    public QuestionnaireObject update(@NonNull QuestionnaireObject questionnaireObject) {
        return questionnaireObjectRepository.save(questionnaireObject);
    }

    @Transactional
    public QuestionnaireObject publish(@NonNull QuestionnaireObject questionnaireObject) {
        savePublishedPicture(questionnaireObject);
        return questionnaireObjectRepository.save(questionnaireObject);
    }

    @Transactional
    public void delete(long id) {
        QuestionnaireObject questionnaireObject = questionnaireObjectRepository.findOne(id);

        List<Ruleset> rulesets = rulesetRepository.findByQuestionnaireObjects(questionnaireObject);
        if (!rulesets.isEmpty()) {
            for (Ruleset ruleset : rulesets) {
                List<QuestionnaireObject> questionnaireObjectList = ruleset.getQuestionnaireObjects();
                questionnaireObjectList.remove(questionnaireObject);
            }
        }

        if (Equipment.SUBOBJECT_TYPE.equals(questionnaireObject.getEquipment().getType())) {
            List<QuestionnaireObject> associatedQuestionnaires = questionnaireObjectRepository.findByQuestionnaireSubObjects(questionnaireObject);
            if (!associatedQuestionnaires.isEmpty()) {
                for (QuestionnaireObject qo : associatedQuestionnaires) {
                    List<QuestionnaireObject> questionnaireObjectList = qo.getQuestionnaireSubObjects();
                    questionnaireObjectList.remove(questionnaireObject);
                }
            }
        }

        questionnaireObjectRepository.delete(id);
    }

    /**
     * get the draft questionnaires which have at least one published version
     *
     * @return the draft questionnaires which have at least one published version
     */
    public List<QuestionnaireObject> getDraftQuestionnaireWithPublishedVersion() {
        List publishedQuestionnaireRefs = getPublishedQuestionnaireRefs();
        List<QuestionnaireObject> allQuestionnaires = all();
        List<QuestionnaireObject> questionnaires = new ArrayList<>();

        for (QuestionnaireObject questionnaireObject : allQuestionnaires) {
            if (publishedQuestionnaireRefs.contains(questionnaireObject.getReference())) {
                questionnaires.add(questionnaireObject);
            }
        }

        return questionnaires;
    }

    /**
     * Returns all published questionnaires (full object, not optimized)
     *
     * @return all published questionnaires
     */
    private List<QuestionnaireObject> getPublishedQuestionnaires() {
        List<QuestionnaireObject> publishedQuestionnaires = new ArrayList<>();
        List publishedQOQueryResult = AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(QuestionnaireObject.class, false, false)
                .add(property(PUBLISHED_PROPERTY).eq(true))
                .addOrder(revisionNumber().desc())
                .getResultList();

        for (Object qoObject : publishedQOQueryResult) {
            if (qoObject instanceof Object[] && ((Object[]) qoObject)[0] != null
                    && ((Object[]) qoObject)[0] instanceof QuestionnaireObject) {
                QuestionnaireObject questionnaireObject = (QuestionnaireObject) ((Object[]) qoObject)[0];
                questionnaireObject.setVersionName(generateVersionName(questionnaireObject.getReference(), questionnaireObject.getVersion()));
                orderChainsAndQuestions(questionnaireObject);

                publishedQuestionnaires.add(questionnaireObject);
            }
        }

        return publishedQuestionnaires;
    }



    /**
     * foreach published questionnaire, returns only the latest version
     *
     * @return the published questionnaires (only latest versions)
     */
    private List<QuestionnaireObject> getLastVersionPublishedQuestionnaires() {
        List<QuestionnaireObject> publishedQuestionnaires = getPublishedQuestionnaires();
        Map<String, QuestionnaireObject> filteredPublishedQuestionnairesMap = new LinkedHashMap<>();

        for (QuestionnaireObject questionnaireObject : publishedQuestionnaires) {
            if (!filteredPublishedQuestionnairesMap.containsKey(questionnaireObject.getReference())) {
                filteredPublishedQuestionnairesMap.put(questionnaireObject.getReference(), questionnaireObject);
            } else if (filteredPublishedQuestionnairesMap.get(questionnaireObject.getReference()).getVersion() < questionnaireObject.getVersion()) {
                filteredPublishedQuestionnairesMap.put(questionnaireObject.getReference(), questionnaireObject);
            }
        }

        return new ArrayList<>(filteredPublishedQuestionnairesMap.values());
    }

    /**
     * Finds the rules associated to the given equipment (rules are linked with equipment by existing questionnaires)
     *
     * @param equipmentReference the equipment reference
     * @return the rules associated to the given equipment
     */
    @NonNull
    public List<Rule> getAssociatedRulesWithEquipment(@NonNull String equipmentReference) {
        List<Rule> associatedRules = new ArrayList<>();
        List<String> addedRulesRefs = new ArrayList<>();
        List<QuestionnaireObject> draftQuestionnairesWithObject = questionnaireObjectRepository
                .findByEquipmentReference(equipmentReference);

        for (QuestionnaireObject questionnaireObject : draftQuestionnairesWithObject) {
            for (Chain chain : questionnaireObject.getChains()) {
                addAssociatedRulesAndRulesRefsToLists(associatedRules, addedRulesRefs, chain);
            }
        }

        return associatedRules;
    }

    /**
     * Returns the questionnaires having given rule
     *
     * @param rule the rule
     * @return the questionnaires having given rule
     */
    public List<QuestionnaireObject> withRule(@NotNull Rule rule) {
        List<QuestionnaireObject> questionnaireObjects = getLastVersionPublishedQuestionnaires();
        questionnaireObjects.addAll(all());
        List<QuestionnaireObject> withRule = new ArrayList<>();

        for (QuestionnaireObject questionnaireObject : questionnaireObjects) {
            for (Chain chain : questionnaireObject.getChains()) {
                for (Question question : chain.getQuestions()) {
                    for (Rule ruleFromQuestion : question.getRules()) {
                        if (ruleFromQuestion.getId() == rule.getId() && !withRule.contains(questionnaireObject)) {
                            withRule.add(questionnaireObject);
                        }
                    }
                }
            }
        }

        return withRule;
    }

    /**
     * Find published questionnaires having versionNames
     *
     * @param publishedQoVersionName the versionName list
     * @return published questionnaires having versionNames
     */
    public Map<Integer, QuestionnaireObject> withVersionNames(@NotNull List<String> publishedQoVersionName) {
        Map<Integer, QuestionnaireObject> revNumberQuestionnaireMap = new HashMap<>();
        for (String versionName : publishedQoVersionName) {
            String[] versionAndNameArray = versionName.split("_");
            String reference = versionAndNameArray[0];
            int version = Integer.parseInt(versionAndNameArray[1]);
            Integer revNumber = getQuestionnaireRevisionNumberByVersionName(reference, version);
            QuestionnaireObject questionnaireObject = getQuestionnaireByRevisionNumber(reference, version, revNumber);
            revNumberQuestionnaireMap.put(revNumber, questionnaireObject);
        }

        return revNumberQuestionnaireMap;
    }

    /**
     * Find the questionnaire chains names and the questionnaire subobject names associated with this questionnaire
     *
     * @param versionName the version name of published questionnaire
     * @return the questionnaire chains names and the questionnaire subobject names associated with this questionnaire having versionName
     */
    public QOChainsAndSubQuestionnairesDtoWrapper findChainsNameAndSubQuestionnairesByVersionName(@NonNull String versionName) {
        List<String> chainsName = new ArrayList<>();
        List<SubobjectQuestionnaireDto> subQuestionnaires = new ArrayList<>();

        String[] versionAndNameArray = versionName.split("_");
        if (versionAndNameArray.length == 2) {
            QuestionnaireObject questionnaireObject = getQuestionnaireByVersionName(versionAndNameArray[0], Integer.parseInt(versionAndNameArray[1]));

            if (questionnaireObject != null) {
                chainsName.addAll(questionnaireObject.getChains().stream().map(Chain::getName).collect(Collectors.toList()));

                for (QuestionnaireObject questionnaireSubObject : questionnaireObject.getQuestionnaireSubObjects()) {
                    SubobjectQuestionnaireDto subobjectQuestionnaireDto = new SubobjectQuestionnaireDto();
                    subobjectQuestionnaireDto.setName(questionnaireSubObject.getName());
                    subobjectQuestionnaireDto.setReference(questionnaireSubObject.getReference());
                    subQuestionnaires.add(subobjectQuestionnaireDto);
                }
            }
        }

        QOChainsAndSubQuestionnairesDtoWrapper wrapper = new QOChainsAndSubQuestionnairesDtoWrapper();
        wrapper.setChains(chainsName);
        wrapper.setSubobjectQuestionnaireDtos(subQuestionnaires);

        return wrapper;
    }

    /**
     * Orders the chains and the questions in questionnaireObject (including questionnaires subobjects)
     *
     * @param questionnaireObject the questionnaireObject
     */
    private void orderChainsAndQuestions(QuestionnaireObject questionnaireObject) {
        questionnaireObject.orderChains();

        for (Chain chain : questionnaireObject.getChains()) {
            chain.orderQuestions();
        }

        for (QuestionnaireObject subQuestionnaire : questionnaireObject.getQuestionnaireSubObjects()) {
            orderChainsAndQuestions(subQuestionnaire);
        }
    }

    private String generateVersionName(String reference, int version) {
        return reference + "_" + version;
    }


    /**
     * Returns the published questionnaires reference list
     *
     * @return the published questionnaires reference list
     */
    private List getPublishedQuestionnaireRefs() {

        return AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(QuestionnaireObject.class, false, false)
                .add(property(PUBLISHED_PROPERTY).eq(true))
                .addProjection(property(REFERENCE_PROPERTY).distinct())
                .addOrder(revisionNumber().desc())
                .getResultList();
    }

    /**
     * add to associatedRules and addedRulesRefs the rules linked with chain
     *
     * @param associatedRules list of rules
     * @param addedRulesRefs  list of rules ref
     * @param chain           the chain
     */
    private void addAssociatedRulesAndRulesRefsToLists(List<Rule> associatedRules, List<String> addedRulesRefs, Chain chain) {
        for (Question question : chain.getQuestions()) {
            for (Rule rule : question.getRules()) {
                if (!addedRulesRefs.contains(rule.getReference())) {
                    associatedRules.add(rule);
                    addedRulesRefs.add(rule.getReference());
                }
            }
        }
    }

    private Integer getQuestionnaireRevisionNumberByVersionName(String reference, int version) {
        log.debug("Retrieving Questionnaire's revision number for reference={} and version={}", reference, version);
        return (Integer) AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(QuestionnaireObject.class, false, false)
                .add(property(REFERENCE_PROPERTY).eq(reference))
                .add(property(VERSION_PROPERTY).eq(version))
                .add(property(PUBLISHED_PROPERTY).eq(true))
                .addProjection(revisionNumber())
                .getSingleResult();
    }

    private QuestionnaireObject getQuestionnaireByVersionName(String reference, int version) {

        Object qo = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(QuestionnaireObject.class, false, false)
                .add(property(REFERENCE_PROPERTY).eq(reference))
                .add(property(VERSION_PROPERTY).eq(version))
                .add(property(PUBLISHED_PROPERTY).eq(true))
                .getSingleResult();

        if (qo instanceof Object[] && ((Object[]) qo)[0] != null
                && ((Object[]) qo)[0] instanceof QuestionnaireObject) {
            orderChainsAndQuestions((QuestionnaireObject) ((Object[]) qo)[0]);
            return (QuestionnaireObject) ((Object[]) qo)[0];
        }

        return null;
    }

    private QuestionnaireObject getQuestionnaireByRevisionNumber(String reference, int version, Integer revision) {

        List<QuestionnaireObject> result = (List<QuestionnaireObject>) AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forEntitiesAtRevision(QuestionnaireObject.class, revision)
                .add(property(REFERENCE_PROPERTY).eq(reference))
                .add(property(VERSION_PROPERTY).eq(version))
                .add(property(PUBLISHED_PROPERTY).eq(true))
                .getResultList();

        if (result != null && !result.isEmpty()) {
            orderChainsAndQuestions(result.get(0));
            return result.get(0);
        }

        return null;
    }

    private void savePublishedPicture(QuestionnaireObject publishedQO) {
        final Equipment equipment = publishedQO.getEquipment();
        equipment.getIcon().setPublished(true);

        for (Equipment equipment1 : equipment.getSubobjects()) {
            savePublishedSubObjectIconAndIllustrations(equipment1);
        }

        for (Illustration illustration : equipment.getIllustrations()) {
            if (illustration.getImage() != null) {
                illustration.getImage().setPublished(true);
            }
        }

        List<ProfileType> savedProfileTypes = new ArrayList<>();

        for (Chain chain : publishedQO.getChains()) {
            for (Question question : chain.getQuestions()) {
                saveRulesIllustrationsAndAssociatedProfileTypesIcons(savedProfileTypes, question);
            }
        }
    }

    private void savePublishedSubObjectIconAndIllustrations(Equipment equipment1) {
        equipment1.getIcon().setPublished(true);
        for (Illustration illustration : equipment1.getIllustrations()) {
            if (illustration.getImage() != null) {
                illustration.getImage().setPublished(true);
            }
        }
    }

    private void saveRulesIllustrationsAndAssociatedProfileTypesIcons(List<ProfileType> savedProfileTypes, Question question) {
        for (Rule rule : question.getRules()) {
            for (Illustration illustration : rule.getIllustrations()) {
                if (illustration.getImage() != null) {
                    illustration.getImage().setPublished(true);
                }
            }

            for (RuleImpact ruleImpact : rule.getRuleImpacts()) {
                if (!savedProfileTypes.contains(ruleImpact.getProfileType())) {
                    ruleImpact.getProfileType().getIcon().setPublished(true);
                    savedProfileTypes.add(ruleImpact.getProfileType());
                }
            }
        }
    }
}
