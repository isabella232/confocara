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

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.Illustration;
import com.orange.confocara.connector.persistence.model.ImageIllustration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = "test")
public class IllustrationServiceIT {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IllustrationService illustrationService;
    @Autowired
    private EquipmentService equipmentService;

    private String title = "porte";
    private String image = "porte.png";
    private String comment = "c'est une porte";

    @Test
    public void createIllustration() {
        Illustration illustration1 = new Illustration();
        illustration1.setReference("illustration1");
        illustration1.setTitle(title);
        addIconToIllustration(illustration1, image, "png");
        illustration1.setComment(comment);
        Illustration illustration = illustrationService.create(illustration1);
        Assert.assertNotNull(illustration);
    }

    @Test
    public void updateIllustration() {
        Illustration ref1 = new Illustration();
        ref1.setReference("ref12");
        ref1.setTitle(title);
        addIconToIllustration(ref1, image, "png");
        ref1.setComment(comment);

        Illustration illustration = illustrationService.create(ref1);

        String updateTitleName = title + "update";
        illustration.setTitle(updateTitleName);

        Illustration illustrationDB = illustrationService.update(illustration, null);
        Assert.assertNotNull(illustrationDB);
        Assert.assertTrue(illustrationDB.getTitle().equals(updateTitleName));
    }

    @Test
    public void deleteIllustration() {
        Illustration ref1 = new Illustration();
        ref1.setReference("ref13");
        ref1.setTitle(title);
        addIconToIllustration(ref1, image, "png");
        ref1.setComment(comment);
        Illustration illustration = illustrationService.create(ref1);
        long id = illustration.getId();
        illustrationService.delete(id);

        Assert.assertNull(illustrationService.withId(id));
    }

    private void addIconToIllustration(Illustration illustration, String name, String extension) {
        ImageIllustration imageToSave = new ImageIllustration();
        imageToSave.setImageName(name);
        imageToSave.setExtension(extension);
        illustration.setImage(imageToSave);
    }
}
