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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

/**
 * description of areas of concerns that can qualify the rules
 *
 * examples : Fauteuil roulant électrique, Petite taille, Mal-Voyant, ...
 *
 * @see Criterion and their default entities, {@link Rule}s
 */
@Immutable
@JsonDeserialize(as = ImmutableConcern.class)
public interface Concern extends ById, WithReference, WithName {

}
