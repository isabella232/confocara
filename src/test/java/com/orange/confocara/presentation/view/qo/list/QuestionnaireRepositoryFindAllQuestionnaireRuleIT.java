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

import com.orange.confocara.connector.persistence.dto.QuestionnaireRulesDto;
import com.orange.confocara.connector.persistence.model.Chain;
import com.orange.confocara.connector.persistence.model.Question;
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.Subject;
import com.orange.confocara.connector.persistence.repository.CategoryRepository;
import com.orange.confocara.connector.persistence.repository.ChainRepository;
import com.orange.confocara.connector.persistence.repository.EquipmentRepository;
import com.orange.confocara.connector.persistence.repository.ImpactValueRepository;
import com.orange.confocara.connector.persistence.repository.QuestionRepository;
import com.orange.confocara.connector.persistence.repository.QuestionnaireObjectRepository;
import com.orange.confocara.connector.persistence.repository.RuleRepository;
import com.orange.confocara.connector.persistence.repository.RulesCategoryRepository;
import com.orange.confocara.connector.persistence.repository.SubjectRepository;
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
public class QuestionnaireRepositoryFindAllQuestionnaireRuleIT {

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

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Before
    public void init() {
        GeneratorUtil.saveQuestionnaire("name", "QO1", new ArrayList<>(), impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);
    }

    @Test
    public void shouldReturnEmptyRuleListWhenDBContains1QuestionnaireWithORule() {
        List<QuestionnaireRulesDto> allQuestionnaireRulesDto = questionnaireDtoRepository.findAllQuestionnaireRulesDto();
        Assert.assertNotNull(allQuestionnaireRulesDto);
        Assert.assertTrue(allQuestionnaireRulesDto.isEmpty());
    }

    @Test
    public void shouldReturn1RuleWhenDBContains1QuestionnaireWith1Rule() {
        Subject subject = GeneratorUtil.saveSubject("suj1", subjectRepository);
        RulesCategory rulesCategory = rulesCategoryRepository.findAll().get(0);
        List<Rule> rules = new ArrayList<>();
        rules.add(GeneratorUtil.saveRuleWithoutImpacts(rulesCategory, "rule", "ruleRef", ruleRepository));
        List<Question> questions = new ArrayList<>();
        questions.add(GeneratorUtil.saveQuestion(subject, "question1", "qu", rulesCategory, rules, questionRepository));
        List<Chain> chains = new ArrayList<>();
        chains.add(GeneratorUtil.saveChain("chain", "refChain", rulesCategory, questions, chainRepository));
        GeneratorUtil.saveQuestionnaire("questionnaire2", "QO2", chains, impactValueRepository, rulesCategoryRepository, categoryRepository, equipmentRepository, questionnaireObjectRepository);

        List<QuestionnaireRulesDto> allQuestionnaireRulesDto = questionnaireDtoRepository.findAllQuestionnaireRulesDto();
        Assert.assertNotNull(allQuestionnaireRulesDto);
        Assert.assertFalse(allQuestionnaireRulesDto.isEmpty());
        Assert.assertTrue(allQuestionnaireRulesDto.size() == 1);
        Assert.assertNotNull(allQuestionnaireRulesDto.get(0).getRulesNb());
        Assert.assertTrue(allQuestionnaireRulesDto.get(0).getRulesNb() == 1);
    }

    @Test
    public void shouldReturn0QuestionWhenDBContains0Questionnaire() {
        questionnaireObjectRepository.deleteAll();

        List<QuestionnaireRulesDto> allQuestionnaireRulesDto = questionnaireDtoRepository.findAllQuestionnaireRulesDto();
        Assert.assertNotNull(allQuestionnaireRulesDto);
        Assert.assertTrue(allQuestionnaireRulesDto.isEmpty());
    }
}
