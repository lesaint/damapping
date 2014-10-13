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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.tools.JavaFileObject;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.testng.annotations.Test;
import org.truth0.Truth;

/**
 * MapperMethodPresentTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperFactoryMethodReturnTypeTest {
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
        "  public String create() {",
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
        .withErrorContaining("Method annotated with @MapperFactory must return type of the class annotated with @Mapper")
        .in(fileObject)
        .onLine(7);
  }

  private CompileTester assertThat(JavaFileObject fileObject) {
    return Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                                       .that(fileObject)
                                       .processedWith(new DAAnnotationProcessor());
  }
}
