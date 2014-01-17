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
package fr.phan.damapping.processor.model;

import fr.phan.damapping.processor.model.factory.DANameFactory;

import javax.lang.model.type.TypeKind;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATypeTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeTest {
    @Test
    public void isArray() throws Exception {
        assertThat(daType(TypeKind.ARRAY).isArray()).isTrue();
        assertThat(daType(TypeKind.DECLARED).isArray()).isFalse();
    }

    private static DAType daType(TypeKind kind) {
        return DAType.builder(kind, DANameFactory.from("simpleName")).build();
    }
}
