package fr.phan.damapping.processor.model.predicate;

import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import java.util.Collections;

import static fr.phan.damapping.processor.model.predicate.DAMethodPredicates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * DAMethodPredicatesTest -
 *
 * @author: Sébastien Lesaint
 */
public class DAMethodPredicatesTest {
    @Test(expectedExceptions = NullPointerException.class)
    public void isConstructor_does_not_support_null() throws Exception {
        isConstructor().apply(null);
    }

    @Test(dataProvider = "isConstructor_uses_ElementKind_DP")
    public void isConstructor_uses_ElementKind(ElementKind constructor, boolean expected) throws Exception {
        DAMethod mock = Mockito.mock(DAMethod.class);
        when(mock.getKind()).thenReturn(constructor);
        assertThat(isConstructor().apply(mock)).isEqualTo(expected);

        verify(mock).getKind();
        verifyNoMoreInteractions(mock);
    }

    @DataProvider
    public Object[][] isConstructor_uses_ElementKind_DP() {
        return new Object[][] {
                {ElementKind.CONSTRUCTOR, true },
                {ElementKind.METHOD, false }
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isDefaultConstructor_does_not_support_null() throws Exception {
        isDefaultConstructor().apply(null);
    }

    @Test
    public void isDefaultConstructor_uses_ElementKind_and_getParameters() throws Exception {
        DAMethod mock = Mockito.mock(DAMethod.class);
        when(mock.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        when(mock.getParameters()).thenReturn(Collections.<DAParameter>emptyList());
        assertThat(isDefaultConstructor().apply(mock)).isTrue();

        verify(mock).getKind();
        verify(mock).getParameters();
        verifyNoMoreInteractions(mock);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isStatic_does_not_support_null() throws Exception {
        isStatic().apply(null);
    }

    @Test
    public void isStatic_returns_false_for_empty_modifier_set() throws Exception {
        DAMethod daMethod = DAMethod.builder(ElementKind.METHOD).build();
        assertThat(isStatic().apply(daMethod)).isFalse();
    }

    @Test
    public void isStatic_returns_true_for_modifier_set_contains_STATIC() throws Exception {
        DAMethod.Builder builder = DAMethod.builder(ElementKind.METHOD);
        assertThat(isStatic().apply(
                builder.withModifiers(ImmutableSet.of(Modifier.STATIC)).build())
        ).isTrue();

        assertThat(isStatic().apply(
                builder.withModifiers(ImmutableSet.of(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)).build())

        ).isTrue();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void notPrivate_does_not_support_null() throws Exception {
        notPrivate().apply(null);
    }

    @Test
    public void notPrivate_returns_true_for_empty_modifier_set() throws Exception {
        DAMethod daMethod = DAMethod.builder(ElementKind.METHOD).build();
        assertThat(notPrivate().apply(daMethod)).isTrue();
    }

    @Test
    public void notPrivate_returns_false_for_modifier_set_contains_STATIC() throws Exception {
        DAMethod.Builder builder = DAMethod.builder(ElementKind.METHOD);
        assertThat(isStatic().apply(
                builder.withModifiers(ImmutableSet.of(Modifier.PRIVATE)).build())
        ).isFalse();

        assertThat(isStatic().apply(
                builder.withModifiers(ImmutableSet.of(Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)).build())

        ).isTrue();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isMapperFactoryMethod_does_not_support_null() throws Exception {
        isMapperFactoryMethod().apply(null);
    }

    @Test
    public void isMapperFactoryMethod_uses_mapperFactoryProperty() throws Exception {
        DAMethod.Builder builder = DAMethod.builder(ElementKind.METHOD);
        assertThat(isMapperFactoryMethod().apply(builder.withMapperFactoryMethod(false).build())).isFalse();
        assertThat(isMapperFactoryMethod().apply(builder.withMapperFactoryMethod(true).build())).isTrue();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void isGuavaFunction_does_not_support_null() throws Exception {
        isGuavaFunction().apply(null);
    }

    @Test
    public void isGuavaFunction_uses_name_and_kind_properties() throws Exception {
        DAMethod.Builder builder = DAMethod.builder(ElementKind.METHOD);
        assertThat(isGuavaFunction().apply(builder.build())).isFalse();
        assertThat(isGuavaFunction().apply(builder.withName(DANameFactory.from("toto")).build())).isFalse();
        assertThat(isGuavaFunction().apply(builder.withName(DANameFactory.from("apply")).build())).isTrue();
    }

}
