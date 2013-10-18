package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import javax.lang.model.type.TypeKind;
import java.util.Collections;

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
        assertThat(daType.kind).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.simpleName.getName()).isEqualTo("Toto");
        assertThat(daType.qualifiedName.getName()).isEqualTo("Toto");
        assertThat(daType.typeArgs).isEmpty();
    }

    @Test
    public void declared() throws Exception {
        DAType daType = DATypeFactory.declared("com.acme.Toto");
        assertThat(daType.kind).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.simpleName.getName()).isEqualTo("Toto");
        assertThat(daType.qualifiedName.getName()).isEqualTo("com.acme.Toto");
        assertThat(daType.typeArgs).isEmpty();
    }

    @Test
    public void declaredWithTypes() throws Exception {
        ImmutableList<DAType> typeArgs = ImmutableList.of(DATypeFactory.declared("java.lang.String"));
        DAType daType = DATypeFactory.declared("com.acme.Toto", typeArgs);
        assertThat(daType.kind).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.simpleName.getName()).isEqualTo("Toto");
        assertThat(daType.qualifiedName.getName()).isEqualTo("com.acme.Toto");
        assertThat(daType.typeArgs).isSameAs(typeArgs);
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
        assertThat(daType.kind).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.simpleName.getName()).isEqualTo(getClass().getSimpleName());
        assertThat(daType.qualifiedName.getName()).isEqualTo(getClass().getCanonicalName());
        assertThat(daType.typeArgs).isEmpty();
    }

    @Test
    public void fromWithTypes() throws Exception {
        ImmutableList<DAType> typeArgs = ImmutableList.of(DATypeFactory.declared("java.lang.String"));
        DAType daType = DATypeFactory.from(getClass(), typeArgs);
        assertThat(daType.kind).isEqualTo(TypeKind.DECLARED);
        assertThat(daType.simpleName.getName()).isEqualTo(getClass().getSimpleName());
        assertThat(daType.qualifiedName.getName()).isEqualTo(getClass().getCanonicalName());
        assertThat(daType.typeArgs).isSameAs(typeArgs);
    }
}