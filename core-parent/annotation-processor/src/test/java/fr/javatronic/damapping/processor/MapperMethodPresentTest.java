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
 * MapperMethodPresentTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperMethodPresentTest extends AbstractCompilationTest {

  private static final String MISSING_MAPPER_METHOD_ERROR_MSG = "Mapper must have one and only one method qualifying " +
      "as mapper method";

  @Test(dataProvider = "classes_without_public_mapper_method")
  public void compiling_annotated_class_without_mapper_method_causes_compilation_error(String fqn, String[] source)
      throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(fqn, source);

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(MISSING_MAPPER_METHOD_ERROR_MSG)
        .in(fileObject)
        .onLine(6);
  }

  @DataProvider
  public Object[][] classes_without_public_mapper_method() {
    return new Object[][]{
        {
            "test.Empty",
            new String[]{
                "package test;",
                "",
                "import fr.javatronic.damapping.annotation.Mapper;",
                "",
                "@Mapper",
                "public class Empty {}"
            }
        },
        {
            "test.OnlyDefaultProtected",
            new String[]{"package test;",
                "",
                "import fr.javatronic.damapping.annotation.Mapper;",
                "",
                "@Mapper",
                "public class OnlyDefaultProtected {",
                "  Integer apply(String input) {",
                "    return null;",
                "  }",
                "}"}
        },
        {
            "test.OnlyProtected",
            new String[]{
                "package test;",
                "",
                "import fr.javatronic.damapping.annotation.Mapper;",
                "",
                "@Mapper",
                "public class OnlyProtected {",
                "  protected Integer apply(String input) {",
                "    return null;",
                "  }",
                "}"
            }
        },
        {
            "test.OnlyPrivate",
            new String[]{
                "package test;",
                "",
                "import fr.javatronic.damapping.annotation.Mapper;",
                "",
                "@Mapper",
                "public class OnlyPrivate {",
                "  private Integer apply(String input) {",
                "    return null;",
                "  }",
                "}"
            }
        }
    };
  }

  @Test
  public void compiling_empty_annotated_class_implementing_Function_is_successfull() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(
        "test.EmptyImplementingFunction",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import com.google.common.base.Function;",
        "",
        "@Mapper",
        "public class EmptyImplementingFunction implements Function<String, Integer> {",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject).compilesWithoutError();
  }

  @Test
  public void compiling_function_with_public_method_fails() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(
        "test.ImplementingFunctionWithPublicMethod",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import com.google.common.base.Function;",
        "",
        "@Mapper",
        "public class ImplementingFunctionWithPublicMethod implements Function<String, Integer> {",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "  public Integer map(String input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject).failsToCompile()
        .withErrorContaining("Mapper must either implement Guava's Function interface or define public method(s), it can not do both")
        .in(fileObject)
        .onLine(11);
  }

}
