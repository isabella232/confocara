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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

/** an entity for {@link SubChapter} */
@Entity
@NoArgsConstructor
@Data
@Audited
public class Chain implements SubChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String reference;

    @NotNull
    @Length(max = 160)
    private String name;

    @ManyToOne
    @Audited
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private RulesCategory rulesCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private List<QuestionnaireObject> qo;

    @ManyToMany(fetch = FetchType.LAZY)
    @Audited
    private List<Question> questions;

    /*
    The following map is used to save the question order
    Because revision query sorts chains by ids
    */
    @ElementCollection(targetClass = String.class)
    private Map<Integer, String> listPositionAndQuestionRefMap = new HashMap<>();

    private java.util.Date date;

    // selected question ids (checkbox order)
    @Transient
    private List<String> questionIds;

    // // selected chain ids (user order)
    @Transient
    private List<String> orderedQuestionIds;

    @Transient
    Integer revisionNb;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "CH" + getId();
        }
    }

    /**
     * Initializes the questions that need to be checked in edit page
     */
    public void setQuestionIdsFromQuestions() {
        List<String> questionIdsFromQuestion = new ArrayList<>();

        for (Question addedQuestion : questions) {
            questionIdsFromQuestion.add(Long.toString(addedQuestion.getId()));
        }

        this.setQuestionIds(questionIdsFromQuestion);
        this.setOrderedQuestionIds(questionIdsFromQuestion);
    }

    public void setListPositionAndQuestionRefMap() {
        listPositionAndQuestionRefMap = new HashMap<>();
        int position = 0;
        if (questions != null) {
            for (Question question : questions) {
                listPositionAndQuestionRefMap.put(position, question.getReference());
                position++;
            }
        }
    }

    /**
     * Verify if the chain is active
     * A chain is active if all its questions are actives
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        boolean active = true;
        if (this.getQuestions().size() == 0) {
            active = false;
        }
        for (Question question : this.getQuestions()) {
            if (State.INACTIVE.toString().equalsIgnoreCase(question.getState())) {
                active = false;
            }
        }
        return active;
    }

    public void orderQuestions() {
        List<Question> orderedQuestions = new ArrayList<>();
        List<Integer> orderedPositions = new ArrayList<>(listPositionAndQuestionRefMap.keySet());
        Collections.sort(orderedPositions);

        for (Integer position : orderedPositions) {
            String ref = listPositionAndQuestionRefMap.get(position);
            for (Question question : questions) {
                if (question.getReference().equals(ref)) {
                    orderedQuestions.add(question);
                    break;
                }
            }
        }

        questions = orderedQuestions;
    }

    @Override
    public String getState() {
        return isActive() ? State.ACTIVE.toString() : State.INACTIVE.toString();
    }

    @Override
    public Date getLastUpdateDate() {
        return date;
    }

    @Override
    public List<CriteriaGroup> getCriteriaGroups() {
        return Lists.newArrayList(questions);
    }

    /**
     * Function used in chains.html
     *
     * @return an integer
     */
    public int getNbOfRules() {
        if (this.getQuestions() != null) {
            int nb = 0;
            for (Question question : this.getQuestions()) {
                if (question.getRules() != null) {
                    nb += question.getRules().size();
                } else {
                    nb += 0;
                }
            }
            return nb;
        } else {
            return 0;
        }
    }
}
