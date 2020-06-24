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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/** the entity for a {@link CriteriaGroup} */
@Entity
@NoArgsConstructor
@Data
@Audited
public class Question implements CriteriaGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    @NotNull
    private String reference;

    @Type(type = "text")
    @NotNull
    private String label;

    @NotNull
    private String state;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private RulesCategory rulesCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Rule> rules;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private List<Chain> chain;

    private java.util.Date date;

    @Transient
    private List<String> ruleIds;

    @Transient
    Integer revisionNb;

    @Column
    private String rulesOrder;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "Q" + getId();
        }
    }

    @PreUpdate
    private void checkRulesOrder() {
        if (rulesOrder == null && rules != null && !rules.isEmpty()) {
            setRulesOrder(rules.stream().map(Rule::getReference).collect(Collectors.toList()));
        } else if (rules == null || rules.isEmpty()) {
            setRulesOrder(Collections.emptyList());
        }
    }

    public void resetRules() {
        this.setRulesOrder(Collections.emptyList());
        this.setRules(Collections.emptyList());
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;

        if (!rules.isEmpty()) {

            StringBuilder b = new StringBuilder();
            rules
                    .stream()
                    .map(Rule::getReference)
                    .forEach(ref -> b.append(ref).append(","));

            // initializing the rules'order
            this.rulesOrder = b.toString();

            this.state = State.ACTIVE.toString().toLowerCase();
        } else {
            this.rulesOrder = "";
            this.state= State.INACTIVE.toString().toLowerCase();
        }
    }

    public void addRule(Rule rule) {
        if (this.rules == null) {
            this.rules = Lists.newArrayList();
        }
        rule.addQuestion(this);
        this.rules.add(rule);
    }

    public void setRulesOrder(List<String> references) {
        if (references != null) {
            StringBuilder b = new StringBuilder();
            references.forEach(ref -> b.append(ref).append(","));
            this.rulesOrder = b.toString();
        } else {
            this.rulesOrder = "";
        }
    }

    public boolean hasRules() {
        return this.rules != null && !this.rules.isEmpty();
    }

    public boolean hasRulesOrder() {
        return this.rulesOrder != null && !this.rulesOrder.isEmpty();
    }

    public static Question newEntity() {
        Question entity = new Question();
        entity.setReference("");
        entity.setDate(new Date());
        return entity;
    }

    @Override
    public boolean isActive() {
        return !State.INACTIVE.toString().equalsIgnoreCase(getState());
    }

    @Override
    public Date getLastUpdateDate() {
        return date;
    }
}
