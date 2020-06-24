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

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class BizExceptionAdviceHandler {

    /**
     * Handles exceptions that deal with business preconditions.
     * <p>
     * The response 412 Precondition Failed (RFC 7232) indicates that the server does not meet one
     * of the preconditions that is required of that request.
     *
     * @param ex A business layer exception
     */
    @ExceptionHandler(BizPreconditionFailedException.class)
    ResponseEntity<Object> bizPreconditionsFailed(RuntimeException ex, final WebRequest request) {
        BizPreconditionFailedException spfEx = (BizPreconditionFailedException) ex;

        log.error("ErrorMessage=Preconditions failed;ErrorCause=" + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(spfEx.getDetail());
    }

    /**
     * Handles business exceptions.
     *
     * A 500 Internal Server Error response is essentially a generic error message. You see these
     * messages when an unexpected condition is encountered and no other 500 message is applicable.
     *
     * @param ex A runtime exception
     */
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Object> internalServerError(RuntimeException ex, final WebRequest request) {
        log.error("ErrorMessage=Internal server error;ErrorCause=" + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
