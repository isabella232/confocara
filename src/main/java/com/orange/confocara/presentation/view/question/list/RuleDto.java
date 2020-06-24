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

package com.orange.confocara.presentation.view.question.list;

import com.orange.confocara.connector.persistence.model.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleDto {

    private String reference;

    private String label;

    public RuleDto(Rule r) {
        reference = r.getReference();
        label = r.getLabel();
    }
}
