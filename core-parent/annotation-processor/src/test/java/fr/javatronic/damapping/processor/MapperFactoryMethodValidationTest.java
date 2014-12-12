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
public class MapperFactoryMethodValidationTest extends AbstractCompilationTest {
  @Test
  public void compilation_fails_when_mapperfactory_method_does_not_return_mapper_type() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.TrivalMapperFactory",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class TrivalMapperFactory {",
        "  @MapperFactory",
        "  public static String create() {",
        "    return null; // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(
            "Method annotated with @MapperFactory must return type of the class annotated with @Mapper"
        )
        .in(fileObject)
        .onLine(8);
  }

  @Test(dataProvider = "invalidMethodFactories")
  public void compilation_fails_when_mapperfactory_is_non_public_static_method(String simpleName, String qualifiers)
      throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test." + simpleName, getMapperFactorySource(simpleName, qualifiers));

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(
            "Method annotated with @MapperFactory must either be a public constructor or a public static method"
        )
        .in(fileObject)
        .onLine(8);
  }

  @DataProvider
  public Object[][] invalidMethodFactories() {
    return new Object[][]{
        { "PublicNonStaticMethod", "public" },
        { "PrivateNonStaticMethod", "private" },
        { "ProtectedNonStaticMethod", "protected" },
        { "DefaultProtectedNonStaticMethod", "" },
        { "PrivateStaticMethod", "private static" },
        { "ProtectedStaticMethod", "protected static" },
        { "DefaultProtectedStaticMethod", "static" },
    };
  }

  private String[] getMapperFactorySource(String fqn, String qualifiers) {
    return new String[]{
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class " + fqn + " {",
        "  @MapperFactory",
        "  " + qualifiers + " " + fqn + " create() {",
        "    return null; // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    };
  }

  @Test
  public void compilation_fails_when_mixing_mapperfactory_method_and_constructor() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.ConstructorAndMethodMixMapperFactory",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class ConstructorAndMethodMixMapperFactory {",
        "  @MapperFactory",
        "  public ConstructorAndMethodMixMapperFactory() {",
        "    // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public static ConstructorAndMethodMixMapperFactory create() {",
        "    return null; // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(
            "Dedicated class can have both constructor(s) and static method(s) annotated with @MapperFactory"
        )
        .in(fileObject)
        .onLine(7);
  }

  @Test
  public void compilation_succeeds_when_mapperfactory_method_is_public_static_and_return_dedicated_class() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.StaticMethodMapperFactory",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class StaticMethodMapperFactory {",
        "  @MapperFactory",
        "  public static StaticMethodMapperFactory create() {",
        "    return null; // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject).compilesWithoutError();
  }

  @Test
  public void compilation_succeeds_when_mapperfactory_method_is_constructor_and_return_dedicated_class() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.ConstructorMapperFactory",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class ConstructorMapperFactory {",
        "  @MapperFactory",
        "  public ConstructorMapperFactory() {",
        "    // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject).compilesWithoutError();
  }

}
