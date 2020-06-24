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
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

/** an entity for {@link Stuff} */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Audited
public class Equipment implements Stuff {

    public static final String OBJECT_TYPE = "object";
    public static final String SUBOBJECT_TYPE = "subobject";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(unique = true)
    private String reference;

    @NotNull
    @Column(unique = true)
    @Length(max = 160)
    private String name;

    //orphanRemoval is not used, so unpublished orphans must be removed manually from database
    @OneToOne(cascade = CascadeType.ALL)
    private ImageEquipment icon;

    @Type(type = "text")
    private String definition;

    @NotNull
    private String type;

    @ManyToOne
    private User user;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Category> categories;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Illustration> illustrations;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Equipment> subobjects;

    private java.util.Date date;

    @Transient
    private List<String> illustrationIds = null;
    @Transient
    private List<String> subobjectIds = null;
    @Transient
    private List<String> categoryIds = null;

    @Transient
    Integer revisionNb;

    /**
     * questionnaireRef is used when a questionnaire object is published :
     * questionnaireRef represents the questionnaire to associate to the subObject
     */
    @Transient
    private String questionnaireRef = null;

    /**
     * questionnairesRefs is used when a questionnaire object is published :
     * questionnairesRefs represents the questionnaires associated to the subObject
     */
    @Transient
    private List<String> questionnairesRefs;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "Obj" + getId();
        }
    }
}

