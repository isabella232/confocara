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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

/**
 * Entity that is an implementation of {@link Concern}
 * It can be a deficiency, a handicap or a risk.
 */
@Entity
@Data
@Audited
@Builder
@EqualsAndHashCode(exclude = "rulesCategories")
public class ProfileType implements Concern {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String reference;

    @NotNull
    @Length(max = 160)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<RulesCategory> rulesCategories;

    //orphanRemoval is not used, so unpublished orphans must be removed manually from database
    @OneToOne(cascade = CascadeType.ALL)
    private ImageProfileType icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotAudited
    private Ruleset ruleset;

    @Transient
    Integer revisionNb;

    @Tolerate
    public ProfileType() {
        // needed when builder is used
    }

    @PostPersist
    private void setReferenceAfterPersist() {
        if (reference == null || reference.isEmpty()) {
            reference = "P" + getId();
        }
    }

    @Override
    public String toString() {
        return "ProfileType [id=" + id + ", reference=" + reference
                + ", name=" + name + "]";
    }
}
