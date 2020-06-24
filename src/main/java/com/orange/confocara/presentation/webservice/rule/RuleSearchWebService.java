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

package com.orange.confocara.presentation.webservice.rule;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.orange.confocara.presentation.webservice.rule.RuleQueryService.RuleDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.immutables.value.Value.Immutable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webservice dedicated to the search of rules
 */
@Api(value = "WebServices for the search for rules")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/ws")
public class RuleSearchWebService {

    private final RuleQueryService queryService;

    /**
     * retrieves a {@link Page} of {@link RuleDto}s
     *
     * @return a bunch of rules
     */
    @ApiOperation(value = "retrieves a bunch of rules")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "rules retrieval is successful",
                    response = Page.class)}
    )
    @GetMapping(value = "/rules", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<RuleDto> allRules(
            @Valid RuleSearchCriteria searchCriteria,
            @Valid RulePageAttributes pageAttributes) {

        log.info(
                "Message=Retrieving a bunch of rules;RuleCategoryIds={};EquipmentIds={};ProfileTypeIds={};Label={}",
                searchCriteria.getContextIds(), searchCriteria.getEquipmentIds(),
                searchCriteria.getConcernIds(),searchCriteria.getLabel());

        return queryService.retrieveRules(searchCriteria, pageAttributes);
    }

    @Immutable
    public interface RuleSearchCriteria {

        /**
         *
         * @return a bunch of ids for {@link com.orange.confocara.connector.persistence.model.Context}s
         * such as {@link com.orange.confocara.connector.persistence.model.RulesCategory}s
         */
        List<Long> getContextIds();

        /**
         *
         * @return a bunch of ids for {@link com.orange.confocara.connector.persistence.model.Stuff}s
         * such {@link com.orange.confocara.connector.persistence.model.Equipment}s
         */
        List<Long> getEquipmentIds();

        /**
         *
         * @return a bunch of ids for {@link com.orange.confocara.connector.persistence.model.Concern}
         * such as {@link com.orange.confocara.connector.persistence.model.ProfileType}s
         */
        List<Long> getConcernIds();

        String getLabel();
    }

    @Immutable
    public interface RulePageAttributes extends Pageable {

        @Override
        default int getPageNumber() {
            return 0;
        }

        @Override
        default int getPageSize() {
            return 1000;
        }

        @Override
        default int getOffset() {
            return 0;
        }

        @Nullable
        @Override
        default Sort getSort() {
            return new Sort(Direction.DESC, "id");
        }

        @Nullable
        @Override
        default Pageable next() {
            return null;
        }

        @Nullable
        @Override
        default Pageable previousOrFirst() {
            return null;
        }

        @Nullable
        @Override
        default Pageable first() {
            return null;
        }

        @Override
        default boolean hasPrevious() {
            return false;
        }
    }
}
