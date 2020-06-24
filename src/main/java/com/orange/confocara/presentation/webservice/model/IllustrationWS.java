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

package com.orange.confocara.presentation.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orange.confocara.connector.persistence.model.Illustration;
import java.text.SimpleDateFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IllustrationWS {
    private String reference;
    private String title;
    private String image;
    private String comment;
    private String date;

    public IllustrationWS(Illustration illustration) {
        reference = illustration.getReference();
        image = illustration.getImage() != null ? illustration.getImage().getFileNameWithExtension() : "";
        comment = illustration.getComment();
        date = illustration.getDate() != null ? new SimpleDateFormat("yyyyMMddHHmmss")
                .format(illustration.getDate()) : "";
    }
}
