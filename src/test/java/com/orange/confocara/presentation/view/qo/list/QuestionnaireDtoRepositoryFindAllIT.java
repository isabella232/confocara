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

package com.orange.confocara.presentation.view.qo.list;

import com.orange.confocara.connector.persistence.dto.QuestionnaireDto;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
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
public class QuestionnaireDtoRepositoryFindAllIT {

    private static final String NAME = "name";

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionnaireObjectRepository questionnaireObjectRepository;

    @Autowired
    private QuestionnaireDtoRepository questionnaireDtoRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    @Before
    public void init() {
        GeneratorUtil.saveQuestionnaire(NAME, "qo1", new ArrayList<>(), impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);
    }

    @Test
    public void shouldReturnSameQuestionnaireAttributesAsQuestionnaireWhenDBContains1Questionnaire() {

        // Given

        // When
        List<QuestionnaireDto> allQuestionnaireDto = questionnaireDtoRepository.findAll();

        // When
        Assert.assertFalse(allQuestionnaireDto.isEmpty());

        QuestionnaireObject questionnaireObject = questionnaireObjectRepository.findByName(NAME);
        Assert.assertEquals(allQuestionnaireDto.get(0).getEquipmentName(), questionnaireObject.getEquipment().getName());
        Assert.assertNotEquals(allQuestionnaireDto.get(0).getEquipmentName(), "objectNamee");
        Assert.assertEquals(allQuestionnaireDto.get(0).getReference(), questionnaireObject.getReference());
        Assert.assertEquals(allQuestionnaireDto.get(0).getName(), questionnaireObject.getName());
        Assert.assertEquals(allQuestionnaireDto.get(0).getPublished(), questionnaireObject.isPublished());
        Assert.assertEquals(allQuestionnaireDto.get(0).getId(), questionnaireObject.getId());
        Assert.assertEquals(allQuestionnaireDto.get(0).getRulesCategoryName(), questionnaireObject.getRulesCategory().getName());
        Assert.assertEquals(allQuestionnaireDto.get(0).getVersion(), questionnaireObject.getVersion());
        Assert.assertEquals(allQuestionnaireDto.get(0).getDate().getTime(), questionnaireObject.getDate().getTime());
        Assert.assertEquals(allQuestionnaireDto.get(0).getUsername(), "");
    }

    @Test
    public void shouldReturnEmptyQuestionnaireDtoWhenQuestionnaireDbIsEmpty() {
        // Given


        // When
        List<QuestionnaireDto> allQuestionnaireDto = questionnaireDtoRepository.findAll();

        // When
        Assert.assertFalse(allQuestionnaireDto.isEmpty());
        Assert.assertTrue(allQuestionnaireDto.size() == 1);

        long id = allQuestionnaireDto.get(0).getId();

        questionnaireObjectRepository.delete(id);

        List<QuestionnaireDto> allQuestionnaireDtoAfterDelete = questionnaireDtoRepository.findAll();
        Assert.assertTrue(allQuestionnaireDtoAfterDelete.isEmpty());
    }
}
