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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.impl.DATypeImpl;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.io.BufferedWriter;
import java.io.StringWriter;
import javax.annotation.Nullable;

import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * CommonMethodsImplTest -
 *
 * @author Sébastien Lesaint
 */
public class CommonMethodsImplTest {

  @Test(dataProvider = "array_or_declared_DP")
  public void appendType_type_from_current_package_without_import_uses_simple_reference(DAType type) throws Exception {
    FileContextTestImpl fileContext = fileContext("com.acme", false, false);
    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);

    assertThat(fileContext.getRes()).isEqualTo(type.isArray() ? "Name[]" : "Name");
  }

  @Test(dataProvider = "array_or_declared_DP")
  public void appendType_type_from_current_package_with_import_uses_simple_reference(DAType type) throws Exception {
    FileContextTestImpl fileContext = fileContext("com.foo", true, false);
    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);

    assertThat(fileContext.getRes()).isEqualTo(type.isArray() ? "Name[]" : "Name");
  }

  @Test(dataProvider = "array_or_declared_DP", expectedExceptions = IllegalArgumentException.class)
  public void appendType_type_from_other_package_without_import_is_invalid(DAType type) throws Exception {
    FileContextTestImpl fileContext = fileContext("com.foo", false, false);
    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);
  }

  @Test(dataProvider = "array_or_declared_DP")
  public void appendType_type_from_other_package_with_import_uses_simple_reference(DAType type) throws Exception {
    FileContextTestImpl fileContext = fileContext("com.foo", true, false);
    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);

    assertThat(fileContext.getRes()).isEqualTo(type.isArray() ? "Name[]" : "Name");
  }

  @Test(dataProvider = "array_or_declared_DP")
  public void appendType_type_from_other_package_without_import_but_with_homonymous_uses_qualified_reference(
      DAType type) throws Exception {
    FileContextTestImpl fileContext = fileContext("com.foo", false, true);
    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);

    assertThat(fileContext.getRes()).isEqualTo(type.isArray() ? "com.acme.Name[]" : "com.acme.Name");
  }

  private static FileContextTestImpl fileContext(final String packageName,
                                                 final boolean explicitImport,
                                                 final boolean hasHomonymousImport) {
    return new FileContextTestImpl(packageName) {
      @Override
      public boolean hasExpliciteImport(@Nullable DAType type) {
        return explicitImport;
      }

      @Override
      public boolean hasHomonymousImport(@Nullable DAType type) {
        return hasHomonymousImport;
      }
    };
  }

  @DataProvider
  public Object[][] array_or_declared_DP() {
    return new Object[][]{
        {DAWriterTestUtil.NAME_DATYPE},
        {DATypeImpl.arrayBuilder(DATypeKind.DECLARED, DAWriterTestUtil.NAME_DATYPE.getSimpleName())
               .withQualifiedName(DAWriterTestUtil.NAME_DATYPE.getQualifiedName())
               .build()}
    };
  }

  @Test(dataProvider = "appendType_non_declared_nor_array_type_always_uses_simple_reference_DP")
  public void appendType_non_declared_nor_array_type_always_uses_simple_reference(DATypeKind kind) throws Exception {
    StringWriter out = new StringWriter();
    FileContext fileContext = Mockito.mock(FileContext.class);

    BufferedWriter bufferedWriter = new BufferedWriter(out);
    when(fileContext.getWriter()).thenReturn(bufferedWriter);

    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(DATypeImpl.typeBuilder(kind, DANameFactory.from("Toto")).build());

    bufferedWriter.flush();
    assertThat(out.toString()).isEqualTo("Toto");

    verify(fileContext).getWriter();
    verifyNoMoreInteractions(fileContext);
  }

  @DataProvider
  public Object[][] appendType_non_declared_nor_array_type_always_uses_simple_reference_DP() {
    Object[][] res = new Object[DATypeKind.values().length - 1][1];
    int i = 0;
    for (DATypeKind kind : DATypeKind.values()) {
      if (kind != DATypeKind.DECLARED) {
        res[i++][0] = kind;
      }
    }
    return res;
  }

  @Test(dataProvider = "appendType_java_lang_type_uses_simple_reference_DP")
  public void appendType_java_lang_type_uses_simple_reference(DAType type) throws Exception {
    StringWriter out = new StringWriter();
    FileContext fileContext = Mockito.mock(FileContext.class);

    BufferedWriter bufferedWriter = new BufferedWriter(out);
    when(fileContext.getWriter()).thenReturn(bufferedWriter);

    CommonMethods commonMethods = new CommonMethodsImpl(fileContext, 0);

    commonMethods.appendType(type);

    bufferedWriter.flush();
    assertThat(out.toString()).isEqualTo(type.isArray() ? "String[]" : "String");

    verify(fileContext, atLeastOnce()).getWriter();
    verifyNoMoreInteractions(fileContext);
  }

  @DataProvider
  public Object[][] appendType_java_lang_type_uses_simple_reference_DP() {
    return new Object[][]{
        {DATypeFactory.from(String.class)},
        {DATypeImpl.arrayBuilder(DATypeKind.DECLARED, DANameFactory.from(String.class.getSimpleName()))
               .withQualifiedName(DANameFactory.from(String.class.getName()))
               .build()}
    };
  }
}
