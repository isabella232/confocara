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

package com.orange.confocara.presentation.webservice.subobject;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import com.orange.confocara.common.logging.Logged;
import com.orange.confocara.connector.persistence.model.ByReference;
import com.orange.confocara.connector.persistence.model.QuestionnaireObject;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/ws/subobjects")
public class SubObjectWebService {

    private final SubObjectsQueryService queryService;

    private final SubObjectsPublishService publishService;

    /**
     * Retrieves a list of subobjects relative to the given {@link QuestionnaireObject}
     *
     * @param questionnaireId an identifier for a {@link QuestionnaireObject}
     *
     * @return a list of equipments
     */
    @GetMapping
    @Logged(message = "Retrieving a list of subobjects")
    public List<EquipmentDto> listAll(@RequestParam("id") Long questionnaireId) {

        return queryService.retrieveSubObjects(questionnaireId);
    }

    /**
     * Publishes a questionnaire and its subobjects
     *
     * @param questionnaireId an identifier for a {@link QuestionnaireObject}
     * @param content a wrapper of some elements to update
     */
    @PostMapping(consumes = "application/json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Logged(message = "Publishing one questionnaire with subobjects")
    public Map<String, String> saveAll(@RequestParam("id") Long questionnaireId, @RequestBody SubObjectSaveRequest content) {

        publishService.publishOneQuestionnaire(questionnaireId, newArrayList(content.getItems()));

        return newHashMap();
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubObjectSaveRequest {

        List<SubObjectByReference> items;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SubObjectByReference implements ByReference {

        private String reference;
    }
}
