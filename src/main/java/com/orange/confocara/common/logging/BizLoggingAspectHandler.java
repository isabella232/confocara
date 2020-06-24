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

package com.orange.confocara.common.logging;


import com.google.common.collect.ImmutableMap;
import com.orange.confocara.common.binding.BizException;
import com.orange.confocara.common.binding.BizException.ErrorCode;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Logging aspect in connectors to external systems using spring aop
 *
 * see http://www.makeinjava.com/logging-aspect-restful-web-service-using-spring-aop-log-requests-responses/
 */
@Aspect
@Component
@Slf4j
public class BizLoggingAspectHandler {

    private static final Map<String, ErrorCode> CODE_MAP =
            ImmutableMap
                    .<String, ErrorCode>builder()
                    .put("IllegalStateException", ErrorCode.INVALID)
                    .put("IllegalArgumentException", ErrorCode.INVALID_ARGUMENT)
                    .put("BizException", ErrorCode.SUB_SYSTEM_FAILURE)
                    .build();

    /**
     * Pointcut that looks at the annotation {@link Logged}
     */
    @Pointcut("@annotation(Logged)")
    public void annotationPointCutDefinition(){
        // simple declaration. nothing else to do
    }

    /**
     * Pointcut that catches ONLY public method in the package
     */
    @Pointcut("execution(public * *(..)) && within(com.orange.confocara.*)")
    public void atExecution(){
        // simple declaration. nothing else to do
    }


    /**
     * Before any resource identified as a Connector or a Client and all public method
     *
     * @param joinPoint a point of interest during the execution of a program
     */
    @Before("annotationPointCutDefinition() && atExecution()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("ClassName={};Method={};Arguments={}", className, methodName,
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * After all method within resource and return a value
     *
     * @param joinPoint a point of interest during the execution of a program
     */
    @AfterReturning(pointcut = "annotationPointCutDefinition() && atExecution()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }

            if (returnValue.length() > 100) {
                returnValue = returnValue.substring(0, 100) + "...";
            }
        }
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.info("ClassName={};Method={};MethodReturn={}", className, methodName, returnValue);
    }

    /**
     * Around any public method of resources identified as connectors or clients
     */
    @Around("annotationPointCutDefinition() && atExecution()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();

            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - start;

            log.info("ClassName={};Method={};ExecutionTime={}", className, methodName, elapsedTime);

            return result;
        } catch (IllegalArgumentException e) {
            log.error("ErrorMessage=Illegal argument in {};MethodArguments={}",
                    joinPoint.getSignature().getName() + "()",
                    Arrays.toString(joinPoint.getArgs()));
            throw e;
        }
    }

    /**
     * After any method within resource throws an exception
     *
     * @param joinPoint a point of interest during the execution of a program
     * @param ex an exception
     */
    @AfterThrowing(pointcut = "annotationPointCutDefinition() && atExecution()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {

        log.error("ClassName={};MethodName={};ErrorCause={};ErrorMessage={}",
                joinPoint.getTarget().getClass(), joinPoint.getSignature().getName(), ex.getCause(),
                convert(ex));
    }

    private String convert(Throwable ex) {
        String message;
        if (ex instanceof BizException) {
            message = ((BizException) ex).getDetail();
        } else {
            String name = ex.getClass().getSimpleName();
            message = CODE_MAP.getOrDefault(name, ErrorCode.UNEXPECTED).toString();
        }
        return message;
    }
}
