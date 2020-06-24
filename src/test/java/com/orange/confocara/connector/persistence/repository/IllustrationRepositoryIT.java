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

package com.orange.confocara.connector.persistence.repository;

import com.orange.confocara.connector.persistence.model.Illustration;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IllustrationRepositoryIT {

    @Autowired
    private IllustrationRepository illustrationRepository;

    private String title = "porte";
    private String image = "porte.png";
    private String comment = "porte coulissante";

    @Test
    public void createIllustration() {
        // do
        final Illustration illustration = new Illustration();
        illustration.setReference("");
        illustration.setTitle(title);
        //illustration.setImage(image);
        illustration.setComment(comment);
        illustrationRepository.save(illustration);
        final Illustration illustration1 = new Illustration();
        illustration1.setReference("a");
        illustration1.setTitle(title + "1");
        //illustration1.setImage(image);
        illustration1.setComment(comment);
        illustrationRepository.save(illustration1);

        //then
        illustrationRepository.filterWithTitleByOrderByIdDesc("1");
        illustrationRepository.filterWithTitleByOrderByIdDesc("por");

        Assert.assertTrue(((List<Illustration>) illustrationRepository.findAll()).size() == 2);
    }
}
