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

package com.orange.confocara;

import static springfox.documentation.builders.PathSelectors.regex;

import com.orange.confocara.presentation.webservice.RestApi;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration for the Swagger tool
 *
 * Currently deactivated, because it ruins the configuration for public resources (every call for
 * js, images, css returns a 404)
 *
 * @see {http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api}
 */
//@Configuration
//@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

    /* Swagger extension */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex(RestApi.WS_ROOT + ".*"))
                .build()
                .apiInfo(apiInfo());
    }

    /* Swagger extension */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "ConfOCARA REST API",
                "ConfOCARA description of API.",
                "API TOS",
                "Terms of service",
                new Contact("name", "localhost", "myaddress@company.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
