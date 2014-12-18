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
package fr.javatronic.damapping.processor;

import javax.tools.JavaFileObject;

import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * ConstructorValidationTest -
 *
 * @author Sébastien Lesaint
 */
public class ConstructorValidationTest extends AbstractCompilationTest {

  private static final String NO_MODIFIER = "";
  private static final String NO_PARAMETERS = "";
  private static final String ONE_PARAMETER = "String t";

  @Test
  public void compilation_fails_if_constructor_is_private() throws Exception {
    JavaFileObject fileObject = constructorMapper("private", NO_PARAMETERS);

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining("Class does not expose an accessible default constructor")
        .in(fileObject)
        .onLine(4);
  }

  @Test(dataProvider = "nonPrivateModifiers")
  public void compilation_succeeds_if_constructor_is_not_private(String modifier) throws Exception {
    assertThat(constructorMapper(modifier, NO_PARAMETERS)).compilesWithoutError();
  }

  @Test(dataProvider = "nonPrivateModifiers")
  public void compilation_succeeds_if_constructor_has_one_parameter(String modifier) throws Exception {
    assertThat(constructorMapper(modifier, ONE_PARAMETER)).compilesWithoutError();
  }

  @DataProvider
  public Object[][] nonPrivateModifiers() {
    return new Object[][] {
        { "public" },
        { "protected" },
        { NO_MODIFIER }
    };
  }

  @Test
  public void compilation_fails_if_mapper_has_more_than_one_constructor() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(
        "ConstructorMapper",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class ConstructorMapper {",
        "  public ConstructorMapper() {",
        "  }",
        "  public ConstructorMapper(String t) {",
        "  }",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining("Mapper can not define more than one constructor")
        .in(fileObject)
        .onLine(7);
  }

  private static JavaFileObject constructorMapper(String constructorModifier, String constructorParameters) {
    return JavaFileObjects.forSourceLines(
        "ConstructorMapper",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class ConstructorMapper {",
        "  " + constructorModifier + " ConstructorMapper(" + constructorParameters + ") {",
        "  }",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );
  }
}
