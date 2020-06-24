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

import com.orange.confocara.connector.persistence.model.ProfileType;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
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
public class ProfileTypeRepositoryFilterWithNameIT {

    @Autowired
    private ProfileTypeRepository profileTypeRepository;

    @Before
    public void initialize() {
        GeneratorUtil.saveProfileType("Ref1", "prof", "icon", profileTypeRepository);
        GeneratorUtil.saveProfileType("Ref2", "profileType", "icon2", profileTypeRepository);
    }

    @Test
    public void shouldReturn2ProfileTypesFilterWithNameProfWhenDBContains2ProfileTypesContainingThisName() {
        List<ProfileType> profileTypes = profileTypeRepository.filterWithName("prof");
        Assert.assertTrue(profileTypes.size() == 2);
    }

    @Test
    public void shouldReturn1ProfileTypeFilterWithNameProfileWhenDBContains1ProfileTypeContainingThisName() {
        List<ProfileType> profileTypes = profileTypeRepository.filterWithName("profile");
        Assert.assertTrue(profileTypes.size() == 1);
    }

    @Test
    public void shouldReturn0ProfileTypeWhenDBContains0ProfileTypeWithThisName() {
        List<ProfileType> profileTypes = profileTypeRepository.filterWithName("hello");
        Assert.assertTrue(profileTypes.size() == 0);
    }

    @Test
    public void shouldReturn2ProfileTypesFilterWithEmptyFilterWhenDBContains2ProfileTypes() {
        List<ProfileType> profileTypes = profileTypeRepository.filterWithName("");
        Assert.assertTrue(profileTypes.size() == 2);
    }
}
