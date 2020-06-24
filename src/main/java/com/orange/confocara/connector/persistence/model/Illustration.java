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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Audited
public class Illustration implements Clarification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String reference;

    @Column(unique = true)
    @Length(max = 160)
    private String title;

    private String origin;

    //orphanRemoval is not used, so unpublished orphans must be removed manually from database
    @OneToOne(cascade = CascadeType.ALL)
    private ImageIllustration image;

    @Type(type = "text")
    private String comment;

    private java.util.Date date;

    @ManyToOne
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private List<Rule> rule;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private List<Equipment> object;

    @Transient
    private boolean shouldDeleteIcon = false;

    @Transient
    Integer revisionNb;

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "Ill" + getId();
        }
    }
}
