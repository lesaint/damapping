/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.lang.model.type.TypeKind;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import static fr.phan.damapping.processor.impl.DATypeFactory.declared;

/**
 * DAWriterTestUtil -
 *
 * @author Sébastien Lesaint
 */
final class DAWriterTestUtil {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static final DAType OVERRIDE_ANNOTATION = DATypeFactory.from(Override.class);
    static final DAType NULLABLE_ANNOTATION = DATypeFactory.from(Nullable.class);
    static final DAType SERIALIZABLE_INTERFACE = DATypeFactory.from(Serializable.class);
    static final DAType FUNCTION_INTEGER_TO_STRING_INTERFACE = DATypeFactory.from(Function.class,
            ImmutableList.of(DATypeFactory.from(Integer.class), DATypeFactory.from(String.class))
    );
    static final DAType DAWRITER_ABSTACT_CLASS = declared("DAWriter");
    static final DAType BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS = declared("com.acme.Bidon",
            ImmutableList.of(declared("java.lang.Integer"), declared("java.lang.String"))
    );
    static final DAParameter STRING_TOTO_PARAMETER = daParameter("java.lang.String", "toto");
    static final DAParameter STRING_TITI_PARAMETER = daParameter("java.lang.String", "titi");
    static final DAParameter FUNCTION_STRING_INTEGER_ARRAY_PARAMETER = functionStringToIntegerArray("complexeParam");
    static final DAType NAME_DATYPE = declared("com.acme.Name");

    private DAWriterTestUtil() {
        // prevents instantiation
    }

    static DAParameter daParameter(String typeQualifiedName, String name) {
        return DAParameter.builder(DANameFactory.from(name), declared(typeQualifiedName)).build();
    }

    /**
     * Un paramètre de type tableau de Function<String, Integer>
     */
    private static DAParameter functionStringToIntegerArray(String name) {
        DAName qualifiedName = DANameFactory.from("com.google.common.base.Function");
        DAType parameterType = DAType.builder(TypeKind.ARRAY)
                .withSimpleName(DANameFactory.simpleFromQualified(qualifiedName))
                .withQualifiedName(qualifiedName)
                .withTypeArgs(
                        ImmutableList.of(declared("java.lang.String"), declared("java.lang.Integer"))
                ).build();
        return DAParameter.builder(DANameFactory.from(name), parameterType).build();
    }
}
