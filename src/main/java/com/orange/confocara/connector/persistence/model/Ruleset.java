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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

/** an entity for {@link ChaptersGroup} */
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
public class Ruleset implements ChaptersGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String app;

    private Integer version;

    private String language;

    @Column(unique = true)
    @NotNull
    private String reference;

    private String state;

    @Column(unique = true)
    @Length(max = 160)
    private String type;

    @Type(type = "text")
    private String comment;

    private boolean published;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private RulesCategory rulesCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<QuestionnaireObject> questionnaireObjects;

    @ElementCollection(targetClass = String.class)
    private List<String> publishedQuestionnairesVersionNames;

    private java.util.Date date;

    @Transient
    private List<String> qoIds;

    @Transient
    private String versionName;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "RS" + getId();
        }
    }

    private void setAssociatedQuestionnaireVersionNames() {
        List<String> versionNames = new ArrayList<>();
        if (questionnaireObjects != null) {
            for (QuestionnaireObject qo : questionnaireObjects) {
                // the last published version is current working version - 1
                int versionNumber = qo.getVersion() - 1;
                versionNames.add(qo.getReference() + "_" + versionNumber);
            }
        }
        publishedQuestionnairesVersionNames = versionNames;
    }

    public void markAsPublished() {
        date = new Date();
        published = true;
        setAssociatedQuestionnaireVersionNames();
    }

    public void markAsNewDraft() {
        published = false;
        version++;
    }

    @Override
    public Date getLastPublishingDate() {
        return date;
    }

    @Override
    public Date getLastUpdateDate() {
        return null;
    }

    @Override
    public List<Chapter> getChapters() {
        return Lists.newArrayList(questionnaireObjects);
    }
}
