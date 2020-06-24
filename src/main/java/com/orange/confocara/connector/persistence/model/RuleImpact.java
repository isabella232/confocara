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
import org.hibernate.envers.Audited;

import javax.persistence.*;

/** an entity that is association between an {@link ImpactValue} and a {@link ProfileType} */
@Entity
@NoArgsConstructor
@Data
@Audited
public class RuleImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ImpactValue impact;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileType profileType;

    @Transient
    private RulesCategory rulesCategory;
}
