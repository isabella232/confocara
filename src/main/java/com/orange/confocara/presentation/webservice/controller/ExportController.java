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

package com.orange.confocara.presentation.webservice.controller;

import com.orange.confocara.business.service.RulesetExportService;
import com.orange.confocara.business.service.UserService;
import com.orange.confocara.business.service.utils.DateUtils;
import com.orange.confocara.connector.persistence.dto.RulesetDtoWrapper;
import com.orange.confocara.connector.persistence.model.User;
import com.orange.confocara.presentation.webservice.model.RuleSetInfo;
import com.orange.confocara.presentation.webservice.model.UserCredentialsWS;
import com.orange.confocara.presentation.webservice.model.UserInfoWS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Types that carry this annotation are treated as controllers where @RequestMapping
 * methods assume @ResponseBody semantics by default, ie return json body.
 */
@RestController
@Slf4j
@RequestMapping(path = "/ws")
/** Rest controller: returns a json body */
public class ExportController {

    @Autowired
    private UserService userService;

    @Autowired
    private RulesetExportService rulesetService;

    @GetMapping(value = "/users")
    @ResponseBody
    public List<UserCredentialsWS> users() {
        List<User> all = userService.all();
        List<UserCredentialsWS> userCredentialsArrayList = new ArrayList<>();
        for (User user : all) {
            UserCredentialsWS userCredentials = getUserCredentials(user);
            userCredentialsArrayList.add(userCredentials);
        }

        return userCredentialsArrayList;
    }

    @PostMapping("/userinfo")
    @ResponseBody
    public UserInfoWS userInfo(@RequestParam long id) {
        User user = userService.withId(id);
        if (user != null) {
            UserInfoWS userInfo = new UserInfoWS();
            userInfo.setUsername(user.getUsername());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setFirstname(user.getFirstname());
            userInfo.setFunction(user.getFunction());
            userInfo.setId(user.getId());
            userInfo.setTel(user.getTel());
            userInfo.setImage(user.getImage() != null ? user.getImage().getFileNameWithExtension() : "");
            userInfo.setUserRoles(user.getUserRoles());
            return userInfo;
        }
        return null;
    }

    @GetMapping("/rulesetlist")
    @ResponseBody
    public List<RuleSetInfo> rulesetList() {
        List<RulesetDtoWrapper> all = rulesetService.getPublishedRulesetDtoWrappers();
        Map<String, Integer> versionMap = new HashMap<>();
        for (RulesetDtoWrapper ruleSet : all) {
            final Integer integer = versionMap.get(ruleSet.getDto().getReference());
            if (integer == null || ruleSet.getDto().getVersion() > integer) {
                versionMap.put(ruleSet.getDto().getReference(), ruleSet.getDto().getVersion());
            }
        }
        List<RuleSetInfo> ruleSetInfos = new ArrayList<>();
        for (RulesetDtoWrapper ruleSet : all) {
            final Integer integer = versionMap.get(ruleSet.getDto().getReference());
            if (integer.equals(ruleSet.getDto().getVersion())) {
                ruleSetInfos.add(getRuleSetInfo(ruleSet));
            }
        }
        return ruleSetInfos;
    }

    private RuleSetInfo getRuleSetInfo(RulesetDtoWrapper ruleSet) {
        RuleSetInfo ruleSetInfo = new RuleSetInfo();
        ruleSetInfo.setReference(ruleSet.getDto().getReference());
        ruleSetInfo.setType(ruleSet.getDto().getName());
        ruleSetInfo.setVersion(ruleSet.getDto().getVersion());
        ruleSetInfo.setComment(ruleSet.getDto().getComment());
        ruleSetInfo.setDate(DateUtils.format(ruleSet.getDto().getDate()));
        ruleSetInfo.setAuthor(setAuthor(ruleSet.getDto().getUsername()));
        ruleSetInfo.setRuleCategoryName(ruleSet.getDto().getRulesCategoryName());

        return ruleSetInfo;
    }

    private String setAuthor(String author) {
        if (author == null || author.isEmpty()) {
            return "";
        } else {
            return author;
        }
    }

    private UserCredentialsWS getUserCredentials(User user) {
        final UserCredentialsWS userCredentials = new UserCredentialsWS();
        if (user != null) {
            userCredentials.setUsername(user.getUsername());
        }

        return userCredentials;
    }
}
