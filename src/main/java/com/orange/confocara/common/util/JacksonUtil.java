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

package com.orange.confocara.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Utils for JSON serialization/deserialization
 */
@FunctionalInterface
public interface JacksonUtil {

    /**
     * @return A configured object mapper
     */
    ObjectMapper buildObjectMapper();

    /**
     * @param util : Utility elements for HTTP requests
     * @return An instance of the default implementation
     */
    static JacksonUtil instance(EscapeUtil util) {
        return new JacksonUtilImpl(util);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class JacksonUtilImpl implements JacksonUtil {

        private final EscapeUtil escapeUtil;

        @Override
        public ObjectMapper buildObjectMapper() {
            ObjectMapper mapper = new ObjectMapper();

            SimpleModule module = new SimpleModule("Deserializer with XSS prevention support",
                    new Version(1, 0, 0, null, null, null));
            module.addDeserializer(String.class, new EscapedStringJsonDeserializer());
            mapper.registerModule(module);
            mapper.disable(
                    MapperFeature.AUTO_DETECT_SETTERS,
                    MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);

            TypeFactory typeFactory = TypeFactory.defaultInstance();
            AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(typeFactory);

            // make deserializer use JAXB annotations (only)
            mapper.getDeserializationConfig().with(introspector);

            // make serializer use JAXB annotations (only)
            mapper.getSerializationConfig().with(introspector);

            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            return mapper;
        }

        private final class EscapedStringJsonDeserializer extends StdScalarDeserializer<String> {

            private static final long serialVersionUID = 1L;

            /**
             * The constructor
             */
            public EscapedStringJsonDeserializer() {
                super(String.class);
            }

            @Override
            public String deserialize(JsonParser jp, DeserializationContext ctx)
                    throws IOException {
                String text = jp.getValueAsString();
                return escapeUtil.escape(text);
            }
        }
    }
}
