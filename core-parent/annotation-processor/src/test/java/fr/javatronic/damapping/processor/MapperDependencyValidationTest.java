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
import org.testng.annotations.Test;

/**
 * MapperDependencyValidationTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperDependencyValidationTest extends AbstractCompilationTest {

  private static final String MAPPER_DEPENDENCY_ON_WRONG_METHOD_ERROR_MSG = "Only parameters of a method annotated " +
      "with @MapperFactory can be annotated with @MapperDependency";
  private static final String INCONSISTENT_MAPPER_DEPENDENCIES_PARAMETERS_ERROR_MSG = "All methods annotated with " +
      "@MapperFactory must have the same set of parameters annotated with" +
      "@MapperDependency (same name, same type, order does not matter)";

  @Test
  public void compilation_fails_when_MapperDependency_annotates_a_param_of_static_method_not_MapperFactory() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.MapperDependencyOnStaticMethod",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "",
        "@Mapper",
        "public class MapperDependencyOnStaticMethod {",
        "  public static MapperDependencyOnStaticMethod create(@MapperDependency Boolean top) {",
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
        .withErrorContaining(MAPPER_DEPENDENCY_ON_WRONG_METHOD_ERROR_MSG)
        .in(fileObject)
        .onLine(7);
  }

  @Test
  public void compilation_fails_when_MapperDependency_annotates_a_param_of_constructor_not_MapperFactory() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.MapperDependencyOnConstructor",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "",
        "@Mapper",
        "public class MapperDependencyOnConstructor {",
        "  public MapperDependencyOnConstructor(@MapperDependency Boolean top) {",
        "    // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(MAPPER_DEPENDENCY_ON_WRONG_METHOD_ERROR_MSG)
        .in(fileObject)
        .onLine(7);
  }

  @Test
  public void compilation_fails_when_MapperDependency_annotates_a_param_of_method() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.MapperDependencyOnMethod",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "",
        "@Mapper",
        "public class MapperDependencyOnMethod {",
        "  public String someMethod(@MapperDependency Boolean top) {",
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
        .withErrorContaining(MAPPER_DEPENDENCY_ON_WRONG_METHOD_ERROR_MSG)
        .in(fileObject)
        .onLine(7);
  }

  @Test
  public void compilation_fails_if_one_MapperFactory_methods_does_not_have_the_same_MapperDependency_parameters() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.InconsistentMapperDependenciesOnStaticMethod",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class InconsistentMapperDependenciesOnStaticMethod {",
        "  @MapperFactory",
        "  public static InconsistentMapperDependenciesOnStaticMethod create(@MapperDependency Boolean top) {",
        "    return null; // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public static InconsistentMapperDependenciesOnStaticMethod create() {",
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
        .withErrorContaining(INCONSISTENT_MAPPER_DEPENDENCIES_PARAMETERS_ERROR_MSG)
        .in(fileObject)
        .onLine(14);
  }

  @Test
  public void compilation_fails_if_one_MapperFactory_constructors_does_not_have_the_same_MapperDependency_parameters() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.InconsistentMapperDependenciesOnConstructor",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class InconsistentMapperDependenciesOnConstructor {",
        "  @MapperFactory",
        "  public InconsistentMapperDependenciesOnConstructor() {",
        "    // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public InconsistentMapperDependenciesOnConstructor(String someParam, @MapperDependency Boolean top, @MapperDependency String label) {",
        "    // content does not matter",
        "  }",
        "",
        "  public String map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining(INCONSISTENT_MAPPER_DEPENDENCIES_PARAMETERS_ERROR_MSG)
        .in(fileObject)
        .onLine(14);
  }

  @Test
  public void compilation_succeeds_even_if_MapperDependency_parameters_order_change_and_if_mixed_with_other_parameters() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines("test.MapperDependenciesMixed",
        "package test;",
        "",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperDependency;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class MapperDependenciesMixed {",
        "  @MapperFactory",
        "  public MapperDependenciesMixed(@MapperDependency Boolean top, @MapperDependency String label) {",
        "    // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public MapperDependenciesMixed(String someParam, @MapperDependency Boolean top, @MapperDependency String label) {",
        "    // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public MapperDependenciesMixed(@MapperDependency Boolean top, Integer intParam, @MapperDependency String label) {",
        "    // content does not matter",
        "  }",
        "",
        "  @MapperFactory",
        "  public MapperDependenciesMixed(@MapperDependency String label, @MapperDependency Boolean top, float someFloat) {",
        "    // content does not matter",
        "  }",
        "",
        "  public MapperDependenciesMixed map(Integer input) {",
        "    return null;",
        "  }",
        "}"
    );

    assertThat(fileObject).compilesWithoutError();
  }
}
