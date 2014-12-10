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
 * UnresolvedReferencesTest -
 *
 * @author Sébastien Lesaint
 */
public class Jsr330PresentTest extends AbstractCompilationTest {
  @Test
  public void compilation_fails_when_Injectable_is_used_without_JSR330_annotations_in_classpath() throws Exception {
    JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("MostSimpleClass",
        "@fr.javatronic.damapping.annotation.Mapper",
        "@fr.javatronic.damapping.annotation.Injectable",
        "public class MostSimpleClass {",
        "  public String map(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    );
    assertThat(javaFileObject)
        .failsToCompile()
        .withErrorContaining("Class annotated with @Mapper and @Injectable requires JSR 330's annotations (@Named, @Inject, ...) to be available in classpath")
        .in(javaFileObject).onLine(3);
  }

}
