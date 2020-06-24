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

package com.orange.confocara.common.binding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class BizException extends RuntimeException {

    /**
     * the error code
     */
    private final ErrorCode code;

    /**
     * extra information on the exception
     */
    private final String detail;

    /**
     * instantiate with a code and a message
     *
     * @param message extra information on the exception
     */
    public BizException(ErrorCode code, String message) {

        super(message, null);
        this.code = code;
        this.detail = message;
    }

    /**
     * instantiate with a code
     *
     * @param code the {@link ErrorCode}.
     */
    public BizException(ErrorCode code) {

        super("", null);
        this.code = code;
        detail = null;
    }

    /**
     * The enumeration of all the possible error codes for the business exceptions.
     */
    @RequiredArgsConstructor
    @Getter
    public enum ErrorCode {

        /**
         * The code for an error that is not known, or for which users must not be provided
         * information.
         */
        UNEXPECTED,

        /**
         * The code for an error that occurs when retrieving an object that does not exist.
         */
        NOT_FOUND,

        /**
         * The code for an error that occurs when trying to execute a forbidden action.
         */
        FORBIDDEN,

        /**
         * The code for an error that occurs when creating an object that already exists.
         */
        EXISTING,

        /**
         * The code for an error that occurs when the object on which the action is to be done does
         * not respect the preconditions for the action.
         */
        INVALID,

        /**
         * The code for an error thrown when an invalid argument is passed to a method
         */
        INVALID_ARGUMENT,

        /**
         * The code for an error thrown when a conflict is discovered between update actions
         */
        CONFLICT,

        /**
         * The code for an error thrown when a task is executing and a request to execute another
         * task is made
         */
        BUSY,

        /**
         * The code for an error thrown when too many requests are done on a service
         */
        TOO_MANY_REQUESTS,

        /**
         * The code for an error thrown when a sub system failure occurs
         */
        SUB_SYSTEM_FAILURE;

    }
}
