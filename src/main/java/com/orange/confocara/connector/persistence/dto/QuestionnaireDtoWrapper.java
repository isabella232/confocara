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

package com.orange.confocara.connector.persistence.dto;

import com.orange.confocara.connector.persistence.model.utils.PublishingState;
import com.orange.confocara.connector.persistence.model.utils.State;

public class QuestionnaireDtoWrapper {

    private QuestionnaireDto dto;

    private long questionsNb;

    private long rulesNb;

    private long subObjectsNb;

    private String state;

    private String publishingState;

    public QuestionnaireDtoWrapper(QuestionnaireDto dto, long questionsNb, long rulesNb, long subObjectsNb, String state, String publishingState) {
        this.dto = dto;
        this.questionsNb = questionsNb;
        this.rulesNb = rulesNb;
        this.subObjectsNb = subObjectsNb;
        this.state = state;
        this.publishingState = publishingState;
    }

    public QuestionnaireDtoWrapper() {
    }

    public Long getDtoId() {
        return dto.getId();
    }

    public void setPublishingState(PublishingState state) {
        this.publishingState = state.toString();
    }

    public void setState(State state) {
        this.state = state.toString();
    }

    public QuestionnaireDto getDto() {
        return dto;
    }

    public void setDto(QuestionnaireDto dto) {
        this.dto = dto;
    }

    public long getQuestionsNb() {
        return questionsNb;
    }

    public void setQuestionsNb(long questionsNb) {
        this.questionsNb = questionsNb;
    }

    public long getRulesNb() {
        return rulesNb;
    }

    public void setRulesNb(long rulesNb) {
        this.rulesNb = rulesNb;
    }

    public long getSubObjectsNb() {
        return subObjectsNb;
    }

    public void setSubObjectsNb(long subObjectsNb) {
        this.subObjectsNb = subObjectsNb;
    }

    public String getState() {
        return state;
    }

    public String getPublishingState() {
        return publishingState;
    }

    public boolean isNewPublishable() {
        return this.publishingState.equals(PublishingState.PUBLISHABLE.toString()) && dto.getVersion() == 1;
    }

    public boolean isExistingPublishable() {
        return this.publishingState.equals(PublishingState.PUBLISHABLE.toString()) && dto.getVersion() > 1;
    }

    public boolean isNewAlmostPublishable() {
        return this.publishingState.equals(PublishingState.ALMOST_PUBLISHABLE.toString()) && dto.getVersion() == 1;
    }

    public boolean isExistingAlmostPublishable() {
        return this.publishingState.equals(PublishingState.ALMOST_PUBLISHABLE.toString()) && dto.getVersion() > 1;
    }

    public static QuestionnaireDtoWrapperBuilder builder() {
        return new QuestionnaireDtoWrapperBuilder();
    }

    public static class QuestionnaireDtoWrapperBuilder {
        private QuestionnaireDto dto;
        private long questionsNb;
        private long rulesNb;
        private long subObjectsNb;
        private String state;
        private String publishingState;

        QuestionnaireDtoWrapperBuilder() {
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder dto(QuestionnaireDto dto) {
            this.dto = dto;
            return this;
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder questionsNb(long questionsNb) {
            this.questionsNb = questionsNb;
            return this;
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder rulesNb(long rulesNb) {
            this.rulesNb = rulesNb;
            return this;
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder subObjectsNb(long subObjectsNb) {
            this.subObjectsNb = subObjectsNb;
            return this;
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder state(String state) {
            this.state = state;
            return this;
        }

        public QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder publishingState(String publishingState) {
            this.publishingState = publishingState;
            return this;
        }

        public QuestionnaireDtoWrapper build() {
            return new QuestionnaireDtoWrapper(this.dto, this.questionsNb, this.rulesNb, this.subObjectsNb, this.state, this.publishingState);
        }

        public String toString() {
            return "QuestionnaireDtoWrapper.QuestionnaireDtoWrapperBuilder(dto=" + this.dto + ", questionsNb=" + this.questionsNb + ", rulesNb=" + this.rulesNb + ", subObjectsNb=" + this.subObjectsNb + ", state=" + this.state + ", publishingState=" + this.publishingState + ")";
        }
    }
}
