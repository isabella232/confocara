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
import com.orange.confocara.connector.persistence.model.Rule;
import com.orange.confocara.connector.persistence.model.RulesCategory;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.connector.persistence.repository.utils.GeneratorUtil;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
public class RuleDtoQueryRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private IllustrationRepository illustrationRepository;

    @Autowired
    private ImpactValueRepository impactValueRepository;

    @Autowired
    private RulesCategoryRepository rulesCategoryRepository;

    private String username1 = "Duchess";
    private String password1 = "1234";

    private String reference = "R1";
    private String label = "Est-ce qu'l fait beau?";

    private String title = "porte";
    private String image = "porte.png";
    private String comment = "c'est une porte";

    //test for creating rule
    @Test
    public void createRule() {

        // given
        final User user1 = new User();
        user1.setUsername(username1);
        user1.setPasswordHash(password1);
        user1.setEmail("e");
        User userFromDB = userRepository.save(user1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        // do
        Rule rule = new Rule();
        rule.setReference(reference);
        rule.setLabel(label);
        rule.setRulesCategory(rulesCategory);
        rule.setUser(userFromDB);
        ruleRepository.save(rule);

        Assert.assertNotNull(ruleRepository.findByReference(reference));

    }

    //test for last rule
    @Test
    public void createRuleAndFindLastId() {

        // given
        final User user1 = new User();
        user1.setUsername(username1);
        user1.setPasswordHash(password1);
        user1.setEmail("e");
        User userFromDB = userRepository.save(user1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);

        // do
        Rule rule = new Rule();
        rule.setReference(reference);
        rule.setLabel(label);
        rule.setUser(userFromDB);
        Rule rule2 = new Rule();
        rule2.setReference(reference + "2");
        rule2.setLabel(label);
        rule.setRulesCategory(rulesCategory);
        rule2.setUser(userFromDB);
        rule2.setRulesCategory(rulesCategory);
        Rule rule3 = new Rule();
        rule3.setReference(reference + "3");
        rule3.setRulesCategory(rulesCategory);
        rule3.setLabel(label);
        rule3.setUser(userFromDB);

        ruleRepository.save(rule);
        ruleRepository.save(rule2);
        ruleRepository.save(rule3);

        // then
        ruleRepository.delete(rule3);
        Rule rule4 = ruleRepository.findTopByOrderByIdDesc();

        Assertions.assertThat(rule4.getReference()).isEqualTo(reference + "2");

    }

    //test for creating rule associated to illustration
    @Test
    public void createRuleWithIllustration() {

        // given
        final User user1 = new User();
        user1.setUsername(username1);
        user1.setPasswordHash(password1);
        user1.setEmail("e");
        final Illustration illustration1 = new Illustration();
        illustration1.setReference("");
        illustration1.setTitle(title);
        //illustration1.setImage(image);
        illustration1.setComment(comment);
        Illustration illustration = illustrationRepository.save(illustration1);
        RulesCategory rulesCategory = GeneratorUtil.saveRulesCategoryWithDefaultImpact("category", impactValueRepository, rulesCategoryRepository);
        User userFromDB = userRepository.save(user1);

        // do
        Rule rule = new Rule();
        rule.setReference(reference);
        rule.setLabel(label);
        rule.setUser(userFromDB);
        rule.setRulesCategory(rulesCategory);
        List<Illustration> ills = new ArrayList<>();
        ills.add(illustration);
        rule.setIllustrations(ills);
        ruleRepository.save(rule);

        Assert.assertNotNull(ruleRepository.findByReference(reference));
    }

}
