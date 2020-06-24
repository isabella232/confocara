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
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@Audited
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The stored name of the image
     * eg : c28dde8a-f51d-44be-b8fd-9ad289e455bf
     */
    @NotNull
    private String uuid;

    @NotNull
    private String extension;

    /**
     * The name of the image imported by the user
     * eg : table.png
     */
    @NotNull
    private String imageName;

    private boolean published;

    public String getFileNameWithExtension() {
        return uuid + '.' + extension;
    }

    @PrePersist
    public void initializeUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
