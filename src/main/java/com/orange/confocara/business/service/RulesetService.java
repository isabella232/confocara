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

import static com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper.newRulesetDtoWrapper;

import com.orange.confocara.business.service.helper.PublishedRulesetHelper;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.dto.RulesetQuestionnaireDto;
import com.orange.confocara.connector.persistence.dto.impl.RulesetDtoImpl;
import com.orange.confocara.connector.persistence.dto.impl.RulesetQuestionnaireDtoImpl;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.model.Ruleset;
import com.orange.confocara.connector.persistence.model.utils.State;
import com.orange.confocara.connector.persistence.repository.RulesetRepository;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.JoinType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service that deals with {@link Ruleset}s
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RulesetService {

    private static final String REFERENCE_PROPERTY = "reference";

    private static final String VERSION_PROPERTY = "version";

    private static final String PUBLISHED_PROPERTY = "published";

    @Autowired
    private RulesetRepository rulesetRepository;

    private final QuestionnaireObjectService questionnaireObjectService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * finds all rulesets. Used in RulesetServiceTest only.
     */
    @Transactional
    public List<Ruleset> all() {
        return (List<Ruleset>) rulesetRepository.findAll();
    }

    @Transactional
    public Ruleset withId(Long id) {
        return rulesetRepository.findOne(id);
    }

    /**
     * @param reference unique identifier of the ruleset
     * @return a {@link List} of {@link RulesetQuestionnaireDto}s that match the given reference
     */
    public List<RulesetQuestionnaireDto> findQuestionnairesNameAndEquipmentByReference(
            @NonNull String reference) {
        return rulesetRepository.findQuestionnairesNameAndEquipmentByReference(reference);
    }

    @Transactional
    public boolean isAvailable(String name) {
        Ruleset ruleset = rulesetRepository.findByType(name.trim().replaceAll("\\s+", " "));
        return ruleset == null;
    }

    @Transactional
    public Ruleset create(Ruleset ruleset) {

        return rulesetRepository.save(ruleset);
    }

    @Transactional
    public void delete(long id) {
        rulesetRepository.delete(id);
    }

    @Transactional
    public Ruleset update(Ruleset ruleset) {
        if (ruleset == null) {
            return null;
        } else {
            return rulesetRepository.save(ruleset);
        }
    }

    /**
     * Returns all relevant information of draft rulesets (dto, optimized)
     *
     * @return all relevant information of draft rulesets (= rulesets from database)
     */
    public List<RulesetDtoWrapper> getRulesetDtos() {

        return rulesetRepository
                .findAllRulesetDto()
                .stream()
                .map(RulesetDtoWrapper::newRulesetDtoWrapper)
                .collect(Collectors.toList());
    }

    /**
     * Generates the RulesetDtoWrapper of all published rulesets
     *
     * @return the RulesetDtoWrapper of all published rulesets
     */
    public List<RulesetDtoWrapper> getPublishedRulesetDtoWrappers() {
        // get the list of published questionnaires revision number
        List publishedRulesetRevisionsQueryResult = getPublishedRulesetsVersions();

        // now, for each published questionnaire, we need to get dto properties
        // we use forEntitiesAtRevision because JOIN are not available with forRevisionsOfEntity
        return getRulesetDtoWrappers(publishedRulesetRevisionsQueryResult);
    }

    /**
     * Find the ruleset questionnaire names and objects that match the given {@link String}
     *
     * @param versionName the version name of published rulesets
     * @return the ruleset questionnaire names and objects
     */
    public List<RulesetQuestionnaireDto> findQuestionnairesNameAndEquipmentByVersionName(
            @NonNull String versionName) {
        log.info("Retrieving published questionnaire objects for Ruleset with version {}",
                versionName);

        return Stream.of(versionName)
                /**
                 * Check that input is correct
                 */
                .filter(x -> versionName.split("_").length == 2)

                /**
                 * Convert input into an Object by using {@link org.hibernate.envers.query.AuditQuery}
                 */
                .map(x -> {
                    String[] versionAndNameArray = x.split("_");
                    String reference = versionAndNameArray[0];
                    String version = versionAndNameArray[1];
                    Object result = null;
                    try {
                        result = AuditReaderFactory.get(entityManager).createQuery()
                                .forRevisionsOfEntity(Ruleset.class, false, false)
                                .add(AuditEntity.property(REFERENCE_PROPERTY).eq(reference))
                                .add(AuditEntity.property(VERSION_PROPERTY)
                                        .eq(Integer.parseInt(version)))
                                .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                                .getSingleResult();
                    } catch (NoResultException e) {
                        log.error(
                                "No published ruleset with reference=" + reference + " and version="
                                        + version + " found", e);
                    }
                    return result;
                })

                /**
                 * Check that the result of the query is valid
                 */
                .filter(x -> x != null && x instanceof Object[] && ((Object[]) x)[0] != null
                        && ((Object[]) x)[0] instanceof Ruleset)

                /**
                 * Convert {@link Object} into {@link Ruleset}
                 */
                .map(x -> (Ruleset) ((Object[]) x)[0])

                /**
                 * Convert the {@link Ruleset} into a bunch of {@link RulesetQuestionnaireDto}
                 */
                .flatMap(x -> x.getQuestionnaireObjects()
                        .stream()
                        .map(RulesetQuestionnaireDtoImpl::new))

                /**
                 * Using {@link Collator} helps on comparing strings with accent, contrary to
                 * {@link Comparator}
                 */
                .sorted((o1, o2) -> Collator.getInstance()
                        .compare(o1.getQuestionnaireName(), o2.getQuestionnaireName()))

                /**
                 * Produce the {@link List} based on the configuration of the {@link Stream}
                 */
                .collect(Collectors.toList());
    }


    /**
     * Find the published ruleset with version and reference
     *
     * @param reference the reference
     * @param version the version number
     * @return ruleset with reference and version number
     */
    public Ruleset findPublishedRulesetByReferenceAndVersion(@NonNull String reference,
            int version) {

        Object result = null;

        try {
            result = AuditReaderFactory.get(entityManager).createQuery()
                    .forRevisionsOfEntity(Ruleset.class, false, false)
                    .add(AuditEntity.property(REFERENCE_PROPERTY).eq(reference))
                    .add(AuditEntity.property(VERSION_PROPERTY).eq(version))
                    .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("No published ruleset with reference=" + reference + " and version=" + version
                    + " found", e);
        }

        if (result instanceof Object[] && ((Object[]) result)[0] != null
                && ((Object[]) result)[0] instanceof Ruleset) {
            /**
             * The result is an array of 3 elements :
             * - result[0] : the {@link Ruleset}
             * - result[1] : the {@link org.hibernate.envers.DefaultRevisionEntity}
             * - result[2] : the {@link org.hibernate.envers.RevisionType}. May be ADD, MOD or DEL.
             */
            Ruleset ruleset = (Ruleset) ((Object[]) result)[0];
            log.info(
                    "Message=Found 1 published ruleset;Ruleset[0].reference={};Ruleset[0].version={}",
                    ruleset.getReference(), ruleset.getVersion());

            ruleset.setVersionName(
                    generateVersionName(ruleset.getReference(), ruleset.getVersion()));
            Map<Integer, QuestionnaireObject> revNumberQuestionnaireMap = questionnaireObjectService
                    .withVersionNames(ruleset.getPublishedQuestionnairesVersionNames());
            List<QuestionnaireObject> questionnairesWithRelevantData = new PublishedRulesetHelper()
                    .checkQuestionnairesData(revNumberQuestionnaireMap);
            ruleset.setQuestionnaireObjects(questionnairesWithRelevantData);

            return ruleset;
        }

        return null;
    }

    /**
     * Returns all revision nb for published rulesets
     *
     * @return all revision nb for published rulesets
     */
    private List getPublishedRulesetsVersions() {
        return AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(Ruleset.class, false, false)
                .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                .addProjection(AuditEntity.revisionNumber())
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();
    }

    /**
     * generates the ruleset dto wrappers containing all the needed information of given rulesets
     *
     * @param publishedRulesetRevisionsQueryResult all revision nb for published rulesets
     * @return the ruleset dto wrappers
     */
    private List<RulesetDtoWrapper> getRulesetDtoWrappers(
            List publishedRulesetRevisionsQueryResult) {
        List<RulesetDtoWrapper> publishedRulesets = new ArrayList<>();

        for (Object ruleset : publishedRulesetRevisionsQueryResult) {
            int rulesetRevNb = (int) ruleset;

            List publishedRulesetDtoQueryResult = getRulesetPropertiesResultList(rulesetRevNb);

            List publishedRulesetDtoUsernameQueryResult = getRulesetUsernameResultList(
                    rulesetRevNb);

            Map<Integer, String> usernameMap = new HashMap<>();

            for (Object usernameArray : publishedRulesetDtoUsernameQueryResult) {
                if (usernameArray instanceof Object[]) {
                    usernameMap.put((Integer) ((Object[]) usernameArray)[0],
                            (String) ((Object[]) usernameArray)[1]);
                }
            }

            for (Object rulesetDto : publishedRulesetDtoQueryResult) {
                if (rulesetDto instanceof Object[]) {
                    RulesetDtoWrapper rulesetDtoWrapper = getRulesetDtoWrapper(usernameMap,
                            (Object[]) rulesetDto);

                    publishedRulesets.add(rulesetDtoWrapper);
                }
            }
        }

        return publishedRulesets;
    }

    /**
     * select ruleset's properties that must be displayed on ruleset's page
     *
     * @param rulesetRevNb the revision nb
     * @return the ruleset properties
     */
    private List getRulesetPropertiesResultList(int rulesetRevNb) {
        return AuditReaderFactory.get(entityManager).createQuery()
                .forEntitiesAtRevision(Ruleset.class, rulesetRevNb)
                .traverseRelation("rulesCategory", JoinType.INNER)
                .addProjection(AuditEntity.property("name"))
                .up()
                .add(AuditEntity.property(PUBLISHED_PROPERTY).eq(true))
                .addProjection(AuditEntity.property("id"))
                .addProjection(AuditEntity.property(REFERENCE_PROPERTY))
                .addProjection(AuditEntity.property("type"))
                .addProjection(AuditEntity.property(VERSION_PROPERTY))
                .addProjection(AuditEntity.property("date"))
                .addProjection(AuditEntity.property("comment"))
                .addProjection(AuditEntity.property("language"))
                .addProjection(AuditEntity.revisionNumber())
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();
    }

    /**
     * Returns the ruleset username
     *
     * @param rulesetRevNb the revision nb
     * @return the ruleset username
     */
    private List getRulesetUsernameResultList(int rulesetRevNb) {
        return AuditReaderFactory.get(entityManager).createQuery()
                .forEntitiesAtRevision(Ruleset.class, rulesetRevNb)
                .addProjection(AuditEntity.revisionNumber())
                .traverseRelation("user", JoinType.LEFT)
                .addProjection(AuditEntity.property("username"))
                .up()
                .add(AuditEntity.property("user").isNotNull())
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();
    }

    /**
     * Returns RulesetDtoWrapper with query result
     *
     * @param usernameMap the user usernames mapped by ruleset revision number
     * @param rulesetDto query result
     * @return RulesetDtoWrapper
     */
    private RulesetDtoWrapper getRulesetDtoWrapper(Map<Integer, String> usernameMap,
            Object[] rulesetDto) {
        RulesetDtoWrapper rulesetDtoWrapper = new RulesetDtoWrapper();
        RulesetDtoImpl dto = new RulesetDtoImpl();
        String reference = (String) rulesetDto[2];
        Integer version = (Integer) rulesetDto[4];
        String username = "";

        if (usernameMap.containsKey((Integer) rulesetDto[8])) {
            username = usernameMap.get((Integer) rulesetDto[8]);
        }

        dto.setRulesCategoryName((String) rulesetDto[0]);
        dto.setId((long) rulesetDto[1]);
        dto.setReference(reference);
        dto.setName((String) rulesetDto[3]);
        dto.setVersion(version);
        dto.setDate((Date) rulesetDto[5]);
        dto.setPublished(true);
        dto.setVersionName(generateVersionName(reference, version));
        dto.setComment((String) rulesetDto[6]);
        dto.setLanguage((String) rulesetDto[7]);
        dto.setUsername(username);

        rulesetDtoWrapper.setDto(dto);
        rulesetDtoWrapper.setState(State.ACTIVE.toString());

        return rulesetDtoWrapper;
    }

    private String generateVersionName(String reference, int version) {
        return reference + "_" + version;
    }
}
