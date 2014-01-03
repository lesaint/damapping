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
package fr.phan.damapping.processor.impl.writer;

import fr.phan.damapping.processor.impl.writer.DAClassWriter;
import fr.phan.damapping.processor.impl.writer.DAWriter;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import javax.lang.model.element.Modifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.testng.annotations.Test;

import static fr.phan.damapping.processor.impl.writer.CommonMethodsImpl.INDENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAClassWriterTest -
 *
 * TODO finir les test U de DAClassWriter
 * <ul>
 *     <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriterTest {

    @Test
    public void empty_empty_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_public_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withModifiers(ImmutableSet.of(Modifier.PUBLIC)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_public_final_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public final class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_annoted_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + DAWriterTestUtil.LINE_SEPARATOR +
                        INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_annoted_twice_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION, DAWriterTestUtil.NULLABLE_ANNOTATION))
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + DAWriterTestUtil.LINE_SEPARATOR +
                        INDENT + "@Nullable" + DAWriterTestUtil.LINE_SEPARATOR +
                        INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_implements_once() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withImplemented(ImmutableList.of(DAWriterTestUtil.SERIALIZABLE_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name implements Serializable {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_implements_twice_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withImplemented(ImmutableList.of(DAWriterTestUtil.SERIALIZABLE_INTERFACE, DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name implements Serializable, Function<Integer, String> {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_extends() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withExtended(DAWriterTestUtil.DAWRITER_ABSTACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name extends DAWriter {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_class_extends_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters).withExtended(DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name extends Bidon<Integer, String> {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void empty_public_final_class_annoted_with_extends_and_implements() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL))
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION))
                .withImplemented(ImmutableList.of(DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE))
                .withExtended(DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS)
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "public final class Name extends Bidon<Integer, String> implements Function<Integer, String> {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAClassWriter<DAWriter> classWriter = new DAClassWriter<DAWriter>(DAWriterTestUtil.NAME_DATYPE, testWriters.bw, parent, 1);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    @Test
    public void one_method_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .start()
                .newMethod("methodName", DATypeFactory.from(String.class)).start().end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "String methodName() {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void explicite_default_constructor_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .start()
                .newConstructor().start().end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "Name() {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void constructor_with_params_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .start()
                .newConstructor()
                    .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER))
                    .start()
                    .newStatement()
                        .start()
                        .append("this.toto = toto")
                        .end()
                    .end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "Name(String titi) {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + INDENT + "this.toto = toto;" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void one_property_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters)
                .start()
                .newProperty("variableName", DATypeFactory.from(String.class))
                    .withAnnotations(ImmutableList.of(DAWriterTestUtil.NULLABLE_ANNOTATION))
                    .write()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "@Nullable" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "String variableName;" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    private DAClassWriter<DAWriter> daClassWriter(TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAClassWriter<DAWriter>(DAWriterTestUtil.NAME_DATYPE, testWriters.bw, parent, 1);
    }

}
