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

package com.orange.confocara.connector.persistence.model.utils;

public enum PublishingState {
    /**
     * l'élément est publisable
     */
    PUBLISHABLE,

    /**
     * l'élément est publiable, sous couvert de pré-requis extérieurs (par exemple, dans le cas d'un
     * questionnaire avec sous-objets, ces derniers doivent être aussi publiables)
     */
    ALMOST_PUBLISHABLE,

    /**
     * les règles ne sont pas remplis pour publier l'élément
     */
    NOT_PUBLISHABLE,

    /**
     * l'élément est déjà publié
     */
    PUBLISHED
}