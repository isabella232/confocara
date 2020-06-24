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

package com.orange.confocara.connector.persistence.model;

import com.google.common.collect.Lists;
import com.orange.confocara.connector.persistence.model.utils.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

/** an entity for a {@link Chapter} */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Audited
public class QuestionnaireObject implements Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    @NotNull
    private String reference;

    @NotNull
    @Column(unique = true)
    @Length(max = 160)
    private String name;

    private String state;

    private boolean published;

    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private RulesCategory rulesCategory;

    @ManyToOne
    private User user;

    @ManyToOne
    private Equipment equipment;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Chain> chains;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<QuestionnaireObject> questionnaireSubObjects;

    /*
    The following map is used to save the chains order
    Because revision query sorts chains by ids
     */
    @ElementCollection(targetClass = String.class)
    private Map<Integer, String> listPositionAndChainRefMap;

    private Date date;

    @Column(name = "last_published_on")
    private Date lastPublishingDate;

    @Column(name = "last_updated_on")
    private Date lastUpdateDate;

    // selected chain ids (checkbox order)
    @Transient
    private List<String> chainIds;

    @Transient
    private List<Equipment> associatedSubObjects;

    // selected chain ids (user order)
    @Transient
    private List<String> orderedChainIds;

    @Transient
    private String objectId;

    @Transient
    private String objectName;

    @Transient
    private String versionName;

    @Transient
    private List<String> parentObjectRefs;

    @Transient
    Integer revisionNb;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "QO" + getId();
        }
    }

    public void orderChains() {
        List<Chain> orderedChains = new ArrayList<>();
        List<Integer> orderedPositions = new ArrayList<>(listPositionAndChainRefMap.keySet());
        Collections.sort(orderedPositions);

        for (Integer position : orderedPositions) {
            String ref = listPositionAndChainRefMap.get(position);
            for (Chain chain : chains) {
                if (chain.getReference().equals(ref)) {
                    orderedChains.add(chain);
                    break;
                }
            }
        }

        chains = orderedChains;
    }

    public void setListPositionAndChainRefMap() {
        listPositionAndChainRefMap = new HashMap<>();
        int position = 0;
        if (chains != null) {
            for (Chain chain : chains) {
                chain.setListPositionAndQuestionRefMap();
                listPositionAndChainRefMap.put(position, chain.getReference());
                position++;
            }
        }
    }

    public void initializeChainIds() {
        List<String> chainsIds = new ArrayList<>();
        for (Chain addedChain : chains) {
            chainsIds.add(Long.toString(addedChain.getId()));
        }

        this.chainIds = chainsIds;
        this.orderedChainIds = chainsIds;
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        if (this.getSubChapters() != null) {
            for (Chain chain : this.getChains()) {
                questions.addAll(chain.getQuestions());
            }
        }
        return questions;
    }

    @Override
    public String toString() {
        return "Questionnaire [id=" + id + ", ref=" + reference + ", name=" + name + "]";
    }

    public void markAsPublished() {
        setDate(new Date());
        setPublished(true);
        setLastPublishingDate(new Date());
    }

    public void markAsNewDraft() {
        setPublished(false);
        setVersion(getVersion() + 1);
    }

    public boolean hasChangedSinceLastPublishing() {
        return lastUpdateDate != null && lastPublishingDate != null && lastUpdateDate.after(lastPublishingDate);
    }

    @Override
    public long questionsCount() {
        return this.getChains().stream().flatMap(c -> c.getQuestions().stream()).count();
    }

    @Override
    public long rulesCount() {
        return this.getChains().stream().flatMap(c -> c.getQuestions().stream().flatMap(q -> q.getRules().stream())).count();
    }

    public boolean isActive() {
        String state = getState();
        boolean hasActiveState =
                state == null || !state.equalsIgnoreCase(State.INACTIVE.toString());
        return hasActiveState && rulesCount() > 0;
    }

    @Override
    public List<Chapter> getDependentChapters() {
        return Lists.newArrayList(questionnaireSubObjects);
    }

    @Override
    public List<SubChapter> getSubChapters() {
        return Lists.newArrayList(chains);
    }

    @Override
    public List<CriteriaGroup> getCriteriaGroups() {
        return Lists.newArrayList(getQuestions());
    }
}
