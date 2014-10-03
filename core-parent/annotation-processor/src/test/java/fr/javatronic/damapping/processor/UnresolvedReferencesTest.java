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

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.testng.annotations.Test;
import org.truth0.Truth;

/**
 * UnresolvedReferencesTest -
 *
 * @author Sébastien Lesaint
 */
public class UnresolvedReferencesTest {
  @Test
  public void does_not_generate_source_classes_when_property_has_unresovled_type() throws Exception {
    JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("UnresolvedTypeInProperty",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class UnresolvedTypeInProperty {",
        "  private final Toto toto;",
        "  public String map(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    );
    assertThat(javaFileObject)
        .failsToCompile()
        .withErrorContaining("cannot find symbol").in(javaFileObject).onLine(5);
  }
  @Test
  public void does_not_generate_source_classes_when_variable_has_unresovled_type() throws Exception {
    JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("UnresolvedTypeInProperty",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class UnresolvedTypeInProperty {",
        "  public String map(Integer input) {",
        "    Toto toto;",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    );
    assertThat(javaFileObject)
        .failsToCompile()
        .withErrorContaining("cannot find symbol").in(javaFileObject).onLine(6);
  }

  private CompileTester assertThat(String fullyQualifiedName, String... sourceLines) {
    return assertThat(JavaFileObjects.forSourceLines(fullyQualifiedName, sourceLines));
  }

  private CompileTester assertThat(JavaFileObject fileObject) {
    return Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(fileObject)
                .processedWith(new DAAnnotationProcessor());
  }
}
