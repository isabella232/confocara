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
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Audited
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    @Length(max = 16)
    private String username;

    @NotNull
    private String passwordHash;

    @Length(max = 64)
    private String name;

    @Length(max = 64)
    private String firstname;

    @NotEmpty
    @Column(unique = true)
    @Length(max = 254, message = "The field must be less than 254 characters")
    private String email;

    // account image is not published : orphanRemoval can be used
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
    private Image image;

    @Length(max = 160)
    private String function;

    private String tel;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @NotAudited
    private Set<UserRole> userRoles = new HashSet<>();

    @Transient
    private String confirmPasswordHash;

    @Transient
    private List<String> rolesIds;

    @Transient
    private boolean shouldDeleteIcon = false;

    @Transient
    private MultipartFile imageData;
}
