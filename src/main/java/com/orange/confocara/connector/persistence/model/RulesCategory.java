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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/** an entity for a {@link Context} */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Audited
@EqualsAndHashCode(exclude = "profileTypes")
public class RulesCategory implements Context {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Length(max = 160)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    private List<ImpactValue> impactValues;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProfileType> profileTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private ImpactValue defaultImpact;

    @Transient
    private List<String> acceptedImpactIds;

    @Transient
    private List<String> profileTypeIds;

    @Transient
    private String defaultImpactId;

    @Transient
    Integer revisionNb;

    @Override
    public String toString() {
        return "RulesCategory [id=" + id + ", name=" + name+ "]";
    }
}
