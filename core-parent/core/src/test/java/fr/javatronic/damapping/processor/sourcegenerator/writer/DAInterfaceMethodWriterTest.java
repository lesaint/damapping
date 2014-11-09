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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfaceMethodWriterTest -
 * <p/>
 * TODO compléter les tests unitaires DAInterfaceMethodWriter
 * <ul>
 * <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceMethodWriterTest {

  @Test
  public void method() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext).write();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "String name();" + LINE_SEPARATOR);
  }

  @Test
  public void method_one_parameter() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TOTO_PARAMETER))
        .write();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "String name(String toto);" + LINE_SEPARATOR);
  }

  @Test
  public void method_two_parameters() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER,
            DAWriterTestUtil.FUNCTION_STRING_INTEGER_ARRAY_PARAMETER
        )
        )
        .write();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "String name(String titi, Function<String, Integer>[] complexeParam);" + LINE_SEPARATOR
        );
  }

  @Test
  public void annoted_method() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION))
        .write();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR
            + INDENT + "String name();" + LINE_SEPARATOR
        );
  }

  @Test
  public void write_returns_parent_writer() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    DAWriter parent = new DAWriter() {

    };
    DAInterfaceMethodWriter<DAWriter> classWriter = new DAInterfaceMethodWriter<DAWriter>("name",
        DATypeFactory.from(String.class), fileContext, 1, parent
    );

    assertThat(classWriter.write()).isSameAs(parent);
  }

  private static DAInterfaceMethodWriter<DAWriter> methodWriter(String name, String returnType,
                                                                FileContextTestImpl fileContext) {
    DAWriter parent = new DAWriter() {

    };
    return new DAInterfaceMethodWriter<DAWriter>(name, DATypeFactory.declared(returnType), fileContext, 1, parent);
  }


}
