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
        "  public static String create(@MapperDependency Boolean top) {",
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
            "Only parameters of a method annotated with @MapperFactory can be annotated with @MapperDependency"
        )
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
            "Only parameters of a method annotated with @MapperFactory can be annotated with @MapperDependency"
        )
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
        .withErrorContaining(
            "Only parameters of a method annotated with @MapperFactory can be annotated with @MapperDependency"
        )
        .in(fileObject)
        .onLine(7);
  }
}
