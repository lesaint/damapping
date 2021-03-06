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

import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.constants.JavaLangConstants;
import fr.javatronic.damapping.processor.model.constants.Jsr305Constants;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAParameterImpl;

import org.testng.annotations.Test;

import static com.google.common.collect.ImmutableList.of;
import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.model.factory.DATypeFactory.declared;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.FUNCTION_STRING_INTEGER_ARRAY_PARAMETER;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.STRING_TITI_PARAMETER;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.STRING_TOTO_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAClassMethodWriterTest -
 * <p/>
 * TODO compléter les tests unitaires DAClassMethodWriter
 * <ul>
 * <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassMethodWriterTest {

  @Test
  public void empty_method() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void public_static_empty_method() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withModifiers(DAModifier.PUBLIC, DAModifier.STATIC)
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "public static String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_method_one_parameter() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withParams(of(STRING_TOTO_PARAMETER))
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "String name(String toto) {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_method_two_parameters() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withParams(of(STRING_TITI_PARAMETER,
                FUNCTION_STRING_INTEGER_ARRAY_PARAMETER
            )
        )
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "String name(String titi, Function<String, Integer>[] complexeParam) {" +
                LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void public_static_empty_method_with_parameter() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withModifiers(DAModifier.PUBLIC, DAModifier.STATIC)
        .withParams(of(FUNCTION_STRING_INTEGER_ARRAY_PARAMETER))
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "public static String name(Function<String, Integer>[] complexeParam) {" + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void annotated_empty_method_with() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withAnnotations(of(OVERRIDE_ANNOTATION))
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR
                + INDENT + "String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_method_with_annotated_parameter() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .withParams(of(
                DAParameterImpl.builder(DANameFactory.from("toto"), declared("java.lang.String"))
                               .withAnnotations(
                                   of(
                                       JavaLangConstants.OVERRIDE_ANNOTATION,
                                       Jsr305Constants.NONNULL_ANNOTATION
                                   )
                               )
                               .build()
            ))
        .start()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "String name(@Override @Nonnull String toto) {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void method_with_statement() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    methodWriter("name", "java.lang.String", fileContext)
        .start()
        .newStatement().start().append("return this").end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "String name() {" + LINE_SEPARATOR
            + INDENT + INDENT + "return this;" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void end_returns_parent_writer() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    DAWriter parent = new DAWriter() {

    };
    DAClassMethodWriter<DAWriter> classWriter = new DAClassMethodWriter<DAWriter>("name",
        DATypeFactory.from(String.class), fileContext, 1, parent
    );

    assertThat(classWriter.end()).isSameAs(parent);
  }

  private static DAClassMethodWriter<DAWriter> methodWriter(String name, String returnType, FileContextTestImpl fileContext) {
    DAWriter parent = new DAWriter() {

    };
    return new DAClassMethodWriter<DAWriter>(name, DATypeFactory.declared(returnType), fileContext, 1, parent);
  }

}
