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

package com.orange.confocara;

import com.orange.confocara.business.service.FileService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CliConfig {

    @Bean
    CommandLineRunner init() {
        return args -> {
            Path parent = Paths.get(FileService.ROOT);
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        };
    }
}
