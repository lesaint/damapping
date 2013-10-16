package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractFileGenerator.INDENT;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.daType;
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
        daClassWriter(testWriters, "name").start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_public_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withModifiers(ImmutableSet.of(Modifier.PUBLIC)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public class name {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_public_final_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public final class name {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_annoted_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR +
                        INDENT + "class name {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_annoted_twice_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name")
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION, DAWriterTestUtil.NULLABLE_ANNOTATION))
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR +
                        INDENT + "@Nullable" + LINE_SEPARATOR +
                        INDENT + "class name {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_implements_once() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withImplemented(ImmutableList.of(DAWriterTestUtil.SERIALIZABLE_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name implements Serializable {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_implements_twice_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withImplemented(ImmutableList.of(DAWriterTestUtil.SERIALIZABLE_INTERFACE, DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name implements Serializable, Function<Integer, String> {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_extends() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withExtended(DAWriterTestUtil.DAWRITER_ABSTACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name extends DAWriter {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_class_extends_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withExtended(DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name extends Bidon<Integer, String> {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void empty_public_final_class_annoted_with_extends_and_implements() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name")
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL))
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION))
                .withImplemented(ImmutableList.of(DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE))
                .withExtended(DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS)
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR +
                        INDENT + "public final class name extends Bidon<Integer, String> implements Function<Integer, String> {" + LINE_SEPARATOR + INDENT + "}");
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAClassWriter<DAWriter> classWriter = new DAClassWriter<DAWriter>("name", testWriters.bw, 1, parent);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    @Test
    public void one_method_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name")
                .start()
                .newMethod("methodName", daType("java.lang.String")).start().end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "class name {" + LINE_SEPARATOR
                        + INDENT + INDENT + "String methodName() {" + LINE_SEPARATOR
                        + INDENT + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                        + INDENT + "}");
    }

    private DAClassWriter<DAWriter> daClassWriter(TestWriters testWriters, String className) {
        DAWriter parent = new DAWriter() {

        };
        return new DAClassWriter<DAWriter>(className, testWriters.bw, 1, parent);
    }

}
