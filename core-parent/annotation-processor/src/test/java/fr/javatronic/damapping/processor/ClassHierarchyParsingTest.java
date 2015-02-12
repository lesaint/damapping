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
 * ClassHierarchyParsingTest -
 *
 * @author Sébastien Lesaint
 */
public class ClassHierarchyParsingTest extends AbstractCompilationTest {

  @Test
  public void compilation_succeeds_even_if_mapper_method_is_defined_in_superclass() throws Exception {
    JavaFileObject superclass = JavaFileObjects.forSourceLines(
        "MapperSuperClass",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class MapperSuperClass {",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );
    JavaFileObject mapper = JavaFileObjects.forSourceLines(
        "InheritingMapper",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class InheritingMapper extends MapperSuperClass {",
        "}"
    );

    assertThat(superclass, mapper).compilesWithoutError();
  }

  @Test
  public void compilation_succeeds_even_if_mapper_method_is_defined_in_super_superclass() throws Exception {
    JavaFileObject superSuperclass = JavaFileObjects.forSourceLines(
        "SuperSuperclass",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class SuperSuperclass {",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );
    JavaFileObject superclass = JavaFileObjects.forSourceLines(
        "MapperSuperClass",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class MapperSuperClass extends SuperSuperclass {",
        "}"
    );
    JavaFileObject mapper = JavaFileObjects.forSourceLines(
        "InheritingMapper",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class InheritingMapper extends MapperSuperClass {",
        "}"
    );

    assertThat(superSuperclass, superclass, mapper).compilesWithoutError();
  }
}
