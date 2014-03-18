package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.annotation.MapperFactoryMethod;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.util.Collections;

import com.google.common.collect.ImmutableList;

import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.google.common.collect.ImmutableSet.of;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isDefaultConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isGuavaFunction;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isStatic;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.notPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * DAMethodPredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAMethodPredicatesTest {
  @Test(expectedExceptions = NullPointerException.class)
  public void isConstructor_does_not_support_null() throws Exception {
    isConstructor().apply(null);
  }

  @Test(dataProvider = "isConstructor_uses_ElementKind_DP")
  public void isConstructor_uses_ElementKind(boolean constructor, boolean expected) throws Exception {
    DAMethod mock = Mockito.mock(DAMethod.class);
    when(mock.isConstructor()).thenReturn(constructor);
    assertThat(isConstructor().apply(mock)).isEqualTo(expected);

    verify(mock).isConstructor();
    verifyNoMoreInteractions(mock);
  }

  @DataProvider
  public Object[][] isConstructor_uses_ElementKind_DP() {
    return new Object[][]{
        {true, true},
        {false, false}
    };
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void isDefaultConstructor_does_not_support_null() throws Exception {
    isDefaultConstructor().apply(null);
  }

  @Test
  public void isDefaultConstructor_uses_ElementKind_and_getParameters() throws Exception {
    DAMethod mock = Mockito.mock(DAMethod.class);
    when(mock.isConstructor()).thenReturn(true);
    when(mock.getParameters()).thenReturn(Collections.<DAParameter>emptyList());
    assertThat(isDefaultConstructor().apply(mock)).isTrue();

    verify(mock).isConstructor();
    verify(mock).getParameters();
    verifyNoMoreInteractions(mock);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void isStatic_does_not_support_null() throws Exception {
    isStatic().apply(null);
  }

  @Test
  public void isStatic_returns_false_for_empty_modifier_set() throws Exception {
    DAMethod daMethod = DAMethod.methodBuilder().build();
    assertThat(isStatic().apply(daMethod)).isFalse();
  }

  @Test
  public void isStatic_returns_true_for_modifier_set_contains_STATIC() throws Exception {
    DAMethod.Builder builder = DAMethod.methodBuilder();
    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.STATIC)).build())
    ).isTrue();

    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.FINAL, DAModifier.STATIC, DAModifier.PUBLIC)).build())
    ).isTrue();
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void notPrivate_does_not_support_null() throws Exception {
    notPrivate().apply(null);
  }

  @Test
  public void notPrivate_returns_true_for_empty_modifier_set() throws Exception {
    DAMethod daMethod = DAMethod.methodBuilder().build();
    assertThat(notPrivate().apply(daMethod)).isTrue();
  }

  @Test
  public void notPrivate_returns_false_for_modifier_set_contains_STATIC() throws Exception {
    DAMethod.Builder builder = DAMethod.methodBuilder();
    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.PRIVATE)).build())
    ).isFalse();

    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.FINAL, DAModifier.STATIC, DAModifier.PRIVATE)).build())
    ).isTrue();
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void isMapperFactoryMethod_does_not_support_null() throws Exception {
    isMapperFactoryMethod().apply(null);
  }

  @Test
  public void isMapperFactoryMethod_uses_mapperFactoryProperty() throws Exception {
    DAMethod.Builder builder = DAMethod.methodBuilder();
    assertThat(
        isMapperFactoryMethod().apply(
          builder.withAnnotations(ImmutableList.of(new DAAnnotation(DATypeFactory.declared("com.acme.Foo")))).build()
        )
    ).isFalse();
    assertThat(
        isMapperFactoryMethod().apply(
            builder.withAnnotations(ImmutableList.of(new DAAnnotation(DATypeFactory.declared(MapperFactoryMethod.class.getName())))).build()
        )
    ).isTrue();
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void isGuavaFunction_does_not_support_null() throws Exception {
    isGuavaFunction().apply(null);
  }

  @Test
  public void isGuavaFunction_uses_name_and_kind_properties() throws Exception {
    DAMethod.Builder builder = DAMethod.methodBuilder();
    assertThat(isGuavaFunction().apply(builder.build())).isFalse();
    assertThat(isGuavaFunction().apply(builder.withName(DANameFactory.from("toto")).build())).isFalse();
    assertThat(isGuavaFunction().apply(builder.withName(DANameFactory.from("apply")).build())).isTrue();
  }

}
