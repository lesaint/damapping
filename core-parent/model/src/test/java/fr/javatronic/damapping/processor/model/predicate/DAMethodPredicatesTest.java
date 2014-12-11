/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAMethodImpl;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAParameterImpl;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.util.Collections;

import com.google.common.collect.ImmutableList;

import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.google.common.collect.ImmutableSet.of;
import static fr.javatronic.damapping.processor.model.DAMethodImpl.methodBuilder;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isDefaultConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isApplyWithSingleParam;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isMapperFactoryMethod;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isStatic;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isNotPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * DAMethodPredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAMethodPredicatesTest {

  private static final DAParameter PARAM_2 = DAParameterImpl.builder(DANameFactory.from("param2"),
      DATypeFactory.from(String.class)
  ).build();
  private static final DAParameter PARAM_1 = DAParameterImpl.builder(DANameFactory.from("param1"),
      DATypeFactory.from(String.class)
  ).build();

  @Test
  public void isConstructor_fails_if_null() throws Exception {
    assertThat(isConstructor().apply(null)).isFalse();
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

  @Test
  public void isDefaultConstructor_fails_if_null() throws Exception {
    assertThat(isDefaultConstructor().apply(null)).isFalse();
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
    DAMethod daMethod = methodBuilder().build();
    assertThat(isStatic().apply(daMethod)).isFalse();
  }

  @Test
  public void isStatic_returns_true_for_modifier_set_contains_STATIC() throws Exception {
    DAMethodImpl.Builder builder = methodBuilder();
    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.STATIC)).build())
    ).isTrue();

    assertThat(
        isStatic().apply(builder.withModifiers(of(DAModifier.FINAL, DAModifier.STATIC, DAModifier.PUBLIC)).build())
    ).isTrue();
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void notPrivate_does_not_support_null() throws Exception {
    isNotPrivate().apply(null);
  }

  @Test
  public void notPrivate_returns_true_for_empty_modifier_set() throws Exception {
    DAMethod daMethod = methodBuilder().build();
    assertThat(isNotPrivate().apply(daMethod)).isTrue();
  }

  @Test
  public void notPrivate_returns_false_for_modifier_set_contains_STATIC() throws Exception {
    DAMethodImpl.Builder builder = methodBuilder();
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
    DAMethodImpl.Builder builder = methodBuilder();
    assertThat(
        isMapperFactoryMethod().apply(
          builder.withAnnotations(ImmutableList.<DAAnnotation>of(new DAAnnotationImpl(DATypeFactory.declared("com.acme.Foo")))).build()
        )
    ).isFalse();
    assertThat(
        isMapperFactoryMethod().apply(
            builder.withAnnotations(
                ImmutableList.<DAAnnotation>of(new DAAnnotationImpl(DATypeFactory.declared(MapperFactory.class.getName())))
            ).build()
        )
    ).isTrue();
  }

  @Test
  public void isApplyWithSingleParam_fails_if_null() throws Exception {
    assertThat(isApplyWithSingleParam().apply(null)).isFalse();
  }

  @Test
  public void isApplyWithSingleParam_fails_if_name_is_not_apply() throws Exception {
    assertThat(isApplyWithSingleParam().apply(mockDAMethod("toto"))).isFalse();
  }

  @Test
  public void isApplyWithSingleParam_fails_if_name_is_apply_nut_no_param() throws Exception {
    assertThat(isApplyWithSingleParam().apply(mockDAMethod("apply"))).isFalse();
  }

  @Test
  public void isApplyWithSingleParam_fails_if_name_is_apply_but_more_than_one_param() throws Exception {
    DAMethod method = mockDAMethod("apply");
    when(method.getParameters()).thenReturn(ImmutableList.of(mock(DAParameter.class), mock(DAParameter.class)));

    assertThat(isApplyWithSingleParam().apply(method)).isFalse();
  }

  @Test
  public void isApplyWithSingleParam_success_if_name_is_apply_and_one_param() throws Exception {
    DAMethod method = mockDAMethod("apply");
    when(method.getParameters()).thenReturn(ImmutableList.of(mock(DAParameter.class)));

    assertThat(isApplyWithSingleParam().apply(method)).isTrue();
  }

  private static DAMethod mockDAMethod(String name) {
    DAMethod method = mock(DAMethod.class);
    when(method.getName()).thenReturn(DANameFactory.from(name));
    return method;
  }

  @Test
  public void isNotConstructor_fails_if_null() throws Exception {
    assertThat(DAMethodPredicates.isNotConstructor().apply(null)).isFalse();
  }

  @Test
  public void isNotConstructor_fails_if_flag_constructor_is_true() throws Exception {
    DAMethod method = mockDAMethod("toto");
    when(method.isConstructor()).thenReturn(true);

    assertThat(DAMethodPredicates.isNotConstructor().apply(method)).isFalse();
  }

  @Test
  public void isNotConstructor_succedd_if_flag_constructor_is_false() throws Exception {
    DAMethod method = mockDAMethod("toto");
    when(method.isConstructor()).thenReturn(false);

    assertThat(DAMethodPredicates.isNotConstructor().apply(method)).isTrue();
  }

  @Test
  public void isGuavaFunctionApply_fails_if_null() throws Exception {
    assertThat(DAMethodPredicates.isGuavaFunctionApply().apply(null)).isFalse();
  }

  @Test
  public void isGuavaFunctionApply_fails_if_flag_isGuavaFunctionApply_is_false() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isGuavaFunctionApplyMethod()).thenReturn(false);

    assertThat(DAMethodPredicates.isGuavaFunctionApply().apply(daMethod)).isFalse();
  }

  @Test
  public void isGuavaFunctionApply_succeeds_if_flag_isGuavaFunctionApply_is_true() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isGuavaFunctionApplyMethod()).thenReturn(true);

    assertThat(DAMethodPredicates.isGuavaFunctionApply().apply(daMethod)).isTrue();
  }

  @Test
  public void isMapperMethod_fails_if_null() throws Exception {
    assertThat(DAMethodPredicates.isMapperMethod().apply(null)).isFalse();
  }

  @Test
  public void isMapperMethod_fails_if_flag_mapperMethod_is_false() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isMapperMethod()).thenReturn(false);

    assertThat(DAMethodPredicates.isMapperMethod().apply(daMethod)).isFalse();
  }

  @Test
  public void isMapperMethod_succeeds_if_flag_mapperMethod_is_true() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isMapperMethod()).thenReturn(true);

    assertThat(DAMethodPredicates.isMapperMethod().apply(daMethod)).isTrue();
  }

  @Test
  public void isImpliciteMapperMethod_fails_if_null() throws Exception {
    assertThat(DAMethodPredicates.isImpliciteMapperMethod().apply(null)).isFalse();
  }

  @Test
  public void isImpliciteMapperMethod_fails_if_flag_false() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isImplicitMapperMethod()).thenReturn(false);

    assertThat(DAMethodPredicates.isImpliciteMapperMethod().apply(daMethod)).isFalse();
  }

  @Test
  public void isImpliciteMapperMethod_succeeds_if_flag_true() throws Exception {
    DAMethod daMethod = mockDAMethod("toto");
    when(daMethod.isImplicitMapperMethod()).thenReturn(true);

    assertThat(DAMethodPredicates.isImpliciteMapperMethod().apply(daMethod)).isTrue();
  }
}
