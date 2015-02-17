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

import fr.javatronic.damapping.processor.validator.GenericParameterValidationStep;

import javax.tools.JavaFileObject;

import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.Test;

/**
 * GenericParameterTest -
 *
 * @author Sébastien Lesaint
 */
public class GenericParameterTest extends AbstractCompilationTest {

  @Test
  public void compilation_fails_if_dedicated_class_has_a_generic_parameter() throws Exception {
    JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("MostSimpleClass",
        "@fr.javatronic.damapping.annotation.Mapper",
        "public class MostSimpleClass<T> {",
        "  public String map(T input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    );
    assertThat(javaFileObject)
        .failsToCompile()
        .withErrorContaining(GenericParameterValidationStep.DEDICATED_CLASS_CAN_NOT_HAVE_GENERIC_PARAMETER_ERROR_MSG)
        .in(javaFileObject)
        .onLine(1);
  }

  @Test
  public void compilation_fails_if_dedicated_class_mapper_factory_has_a_generic_parameter() throws Exception {
    JavaFileObject javaFileObject = JavaFileObjects.forSourceLines("MostSimpleClass",
        "@fr.javatronic.damapping.annotation.Mapper",
        "public class MostSimpleClass<T> {",
        "  @fr.javatronic.damapping.annotation.MapperFactory",
        "  public MostSimpleClass(boolean flag) {",
        "  ",
        "  }",
        "  public String map(T input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    );
    assertThat(javaFileObject)
        .failsToCompile()
        .withErrorContaining(GenericParameterValidationStep.DEDICATED_CLASS_CAN_NOT_HAVE_GENERIC_PARAMETER_ERROR_MSG)
        .in(javaFileObject)
        .onLine(1);
  }
}
