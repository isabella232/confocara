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

import com.orange.confocara.connector.persistence.model.ImageProfileType;
import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
public class ProfileTypeServiceIT {

    private static final String REF = "REF1";
    private static final String NAME = "profileName";

    @Autowired
    private ProfileTypeService profileTypeService;
    @Autowired
    private RulesCategoryService rulesCategoryService;
    @Autowired
    private ImpactValueService impactValueService;


    @Test
    public void createProfileType() {
        generateProfileType(NAME, impactValueService, rulesCategoryService, profileTypeService);
        ProfileType byReference = profileTypeService.withReference(REF);
        Assertions.assertThat(byReference).isNotNull();
    }

    @Test
    public void updateProfileType() {
        ProfileType profileType1 = generateProfileType(NAME, impactValueService, rulesCategoryService, profileTypeService);
        String nameUp = NAME + "UP";
        profileType1.setName(nameUp);
        profileTypeService.update(profileType1);

        ProfileType byReference = profileTypeService.withReference(REF);
        Assertions.assertThat(byReference).isNotNull();
        Assertions.assertThat(byReference.getName()).isEqualTo(nameUp);
    }

    @Test
    public void shouldNotDeleteProfileTypeWhenARulesCategoryContainsOnlyThisProfileType() {
        generateProfileType(NAME, impactValueService, rulesCategoryService, profileTypeService);
        ProfileType byReference = profileTypeService.withReference(REF);
        Assertions.assertThat(byReference).isNotNull();
        profileTypeService.delete(byReference.getId());
        ProfileType byReferenceDel = profileTypeService.withReference(REF);
        Assertions.assertThat(byReferenceDel).isNotNull();
    }

    @Test
    public void shouldDeleteProfileTypeWhenARulesCategoryContainsSeveralProfileTypes() {
        ProfileType profileType1 = generateProfileType(NAME, impactValueService, rulesCategoryService, profileTypeService);

        ProfileType secondProfileType = ProfileType.builder()
                .reference("ref2")
                .name(NAME+"2")
                .build();

        ImageProfileType image = new ImageProfileType();
        image.setImageName("second.jpg");
        image.setExtension("jpg");

        ProfileType profileType2 = profileTypeService.create(secondProfileType);

        List<RulesCategory> all = rulesCategoryService.all();
        RulesCategory rulesCategory = all.get(0);

        List<ProfileType> profileTypes = new ArrayList<>();
        profileTypes.add(profileType1);
        profileTypes.add(profileType2);

        rulesCategory.setProfileTypes(profileTypes);
        rulesCategoryService.update(rulesCategory);

        ProfileType byReference = profileTypeService.withReference(REF);
        Assertions.assertThat(byReference).isNotNull();
        profileTypeService.delete(byReference.getId());
        ProfileType byReferenceDel = profileTypeService.withReference(REF);
        Assertions.assertThat(byReferenceDel).isNull();
    }

    public static ProfileType generateProfileType(String name,
                                                  ImpactValueService impactValueService,
                                                  RulesCategoryService rulesCategoryService,
                                                  ProfileTypeService profileTypeService) {

        RulesCategory rulesCategory = RulesCategoryServiceIT
                .createRulesCategory("HANDICAP", impactValueService, rulesCategoryService);
        List<RulesCategory> rulesCategories = new ArrayList<>();
        rulesCategories.add(rulesCategory);
        ProfileType profileType = ProfileType.builder().build();
        profileType.setRulesCategories(rulesCategories);
        profileType.setReference(REF);
        profileType.setName(name);

        ImageProfileType imageProfileType = new ImageProfileType();
        imageProfileType.setImageName("profile.jpg");
        imageProfileType.setExtension("jpg");
        profileType.setIcon(imageProfileType);

        return profileTypeService.create(profileType);
    }
}
