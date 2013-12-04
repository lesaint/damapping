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

import fr.phan.damapping.processor.model.DAType;

import java.util.Collections;
import javax.lang.model.type.TypeKind;
import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATypeFactoryTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeFactoryTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void declared_null_qualifiedname_raises_NPE() throws Exception {
        DATypeFactory.declared(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void declared_with_types_null_qualifiedname_raises_NPE() throws Exception {
        DATypeFactory.declared(null, Collections.<DAType>emptyList());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void declaredWithTypes_null_daType_list_raises_NPE() throws Exception {
        DATypeFactory.declared("sdsd", null);
    }

    @Test
    public void declared_noPoint_in_qualifiedName() throws Exception {
        DAType daType = DATypeFactory.declared("Toto");
        assertThat(daType.getKind()).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.getSimpleName().getName()).isEqualTo("Toto");
        assertThat(daType.getQualifiedName().getName()).isEqualTo("Toto");
        assertThat(daType.getTypeArgs()).isEmpty();
    }

    @Test
    public void declared() throws Exception {
        DAType daType = DATypeFactory.declared("com.acme.Toto");
        assertThat(daType.getKind()).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.getSimpleName().getName()).isEqualTo("Toto");
        assertThat(daType.getQualifiedName().getName()).isEqualTo("com.acme.Toto");
        assertThat(daType.getTypeArgs()).isEmpty();
    }

    @Test
    public void declaredWithTypes() throws Exception {
        ImmutableList<DAType> typeArgs = ImmutableList.of(DATypeFactory.declared("java.lang.String"));
        DAType daType = DATypeFactory.declared("com.acme.Toto", typeArgs);
        assertThat(daType.getKind()).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.getSimpleName().getName()).isEqualTo("Toto");
        assertThat(daType.getQualifiedName().getName()).isEqualTo("com.acme.Toto");
        assertThat(daType.getTypeArgs()).isSameAs(typeArgs);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void from_null_qualifiedname_raises_NPE() throws Exception {
        DATypeFactory.from(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void from_with_types_null_qualifiedname_raises_NPE() throws Exception {
        DATypeFactory.from(null, Collections.<DAType>emptyList());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void fromWithTypes_null_daType_list_raises_NPE() throws Exception {
        DATypeFactory.from(this.getClass(), null);
    }

    @Test
    public void from() throws Exception {
        DAType daType = DATypeFactory.from(getClass());
        assertThat(daType.getKind()).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.getSimpleName().getName()).isEqualTo(getClass().getSimpleName());
        assertThat(daType.getQualifiedName().getName()).isEqualTo(getClass().getCanonicalName());
        assertThat(daType.getTypeArgs()).isEmpty();
    }

    @Test
    public void fromWithTypes() throws Exception {
        ImmutableList<DAType> typeArgs = ImmutableList.of(DATypeFactory.declared("java.lang.String"));
        DAType daType = DATypeFactory.from(getClass(), typeArgs);
        assertThat(daType.getKind()).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.getSimpleName().getName()).isEqualTo(getClass().getSimpleName());
        assertThat(daType.getQualifiedName().getName()).isEqualTo(getClass().getCanonicalName());
        assertThat(daType.getTypeArgs()).isSameAs(typeArgs);
    }
}
