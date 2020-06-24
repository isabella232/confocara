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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/** the entity for a {@link Criterion} */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Audited
public class Rule implements Criterion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    @NotNull
    private String reference;

    @Type(type = "text")
    private String label;

    @Type(type = "text")
    private String origin;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private RulesCategory rulesCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Illustration> illustrations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RuleImpact> ruleImpacts;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private List<Question> question;

    private java.util.Date date;

    @Transient
    private List<String> illustrationIds = null;

    @Transient
    private Long rulesCategoryId = null;

    @Transient
    Integer revisionNb;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "R" + getId();
        }
    }

    public void addQuestion(Question question) {
        if (this.question == null) {
            this.question = Lists.newArrayList();
        }
        this.question.add(question);
    }

    @Override
    public String getRuleCategoryName() {
        return rulesCategory != null ? rulesCategory.getName() : "";
    }
}
