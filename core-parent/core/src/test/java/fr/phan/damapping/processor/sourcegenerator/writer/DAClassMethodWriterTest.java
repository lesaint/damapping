/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.sourcegenerator.writer;

import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.testng.annotations.Test;

import static fr.phan.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAClassMethodWriterTest -
 *
 * TODO compléter les tests unitaires DAClassMethodWriter
 * <ul>
 *     <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassMethodWriterTest {

    @Test
    public void empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name() {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void public_static_empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withModifiers(ImmutableSet.of(DAModifier.PUBLIC, DAModifier.STATIC))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public static String name() {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_method_one_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TOTO_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String toto) {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_method_two_parameters() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER, DAWriterTestUtil.FUNCTION_STRING_INTEGER_ARRAY_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String titi, Function<String, Integer>[] complexeParam) {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void public_static_empty_method_with_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withModifiers(ImmutableSet.of(DAModifier.PUBLIC, DAModifier.STATIC))
                .withParams(ImmutableList.of(DAWriterTestUtil.FUNCTION_STRING_INTEGER_ARRAY_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public static String name(Function<String, Integer>[] complexeParam) {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void annoted_empty_method_with() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "String name() {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void method_with_statement() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .start()
                .newStatement().start().append("return this").end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name() {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "return this;" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAClassMethodWriter<DAWriter> classWriter = new DAClassMethodWriter<DAWriter>("name", DATypeFactory.from(String.class), testWriters.bw, 1, parent);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    private static DAClassMethodWriter<DAWriter> methodWriter(String name, String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAClassMethodWriter<DAWriter>(name, DATypeFactory.declared(returnType), testWriters.bw, 1, parent);
    }

}
