package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAClassWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriterTest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final DAType OVERRIDE_ANNOTATION = daType("java.lang.Override");
    private static final DAType NULLABLE_ANNOTATION = daType("javax.annotation.Nullable");
    private static final DAType SERIALIZABLE_INTERFACE = daType("java.io.Serializable");
    private static final DAType FUNCTION_INTEGER_TO_STRING_INTERFACE = daType("com.google.common.base.Function",
            ImmutableList.of(daType("java.lang.Integer"), daType("java.lang.String"))
    );
    private static final DAType DAWRITER_ABSTACT_CLASS = daType("com.ekino.lesaint.dozerannihilation.processor.impl.DAWriter");
    private static final DAType BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS = daType("com.acme.Bidon",
            ImmutableList.of(daType("java.lang.Integer"), daType("java.lang.String"))
    );

    @Test
    public void empty_empty_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "class name {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_public_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withModifiers(ImmutableSet.of(Modifier.PUBLIC)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "public class name {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_public_final_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "public final class name {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_annoted_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "@Override" + LINE_SEPARATOR +
                        AbstractFileGenerator.INDENT + "class name {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_annoted_twice_class() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name")
                .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION, NULLABLE_ANNOTATION))
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "@Override" + LINE_SEPARATOR +
                        AbstractFileGenerator.INDENT + "@Nullable" + LINE_SEPARATOR +
                        AbstractFileGenerator.INDENT + "class name {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_implements_once() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withImplemented(ImmutableList.of(SERIALIZABLE_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "class name implements Serializable {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_implements_twice_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withImplemented(ImmutableList.of(SERIALIZABLE_INTERFACE, FUNCTION_INTEGER_TO_STRING_INTERFACE)).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "class name implements Serializable, Function<Integer, String> {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_extends() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withExtended(DAWRITER_ABSTACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "class name extends DAWriter {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_class_extends_one_with_types() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name").withExtended(BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "class name extends Bidon<Integer, String> {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void empty_public_final_class_annoted_with_extends_and_implements() throws Exception {
        TestWriters testWriters = new TestWriters();
        daClassWriter(testWriters, "name")
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.FINAL))
                .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION))
                .withImplemented(ImmutableList.of(FUNCTION_INTEGER_TO_STRING_INTERFACE))
                .withExtended(BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS)
                .start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(AbstractFileGenerator.INDENT + "@Override" + LINE_SEPARATOR +
                        AbstractFileGenerator.INDENT + "public final class name extends Bidon<Integer, String> implements Function<Integer, String> {" + LINE_SEPARATOR + AbstractFileGenerator.INDENT + "}");
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter();
        DAClassWriter<DAWriter> classWriter = new DAClassWriter<DAWriter>("name", testWriters.bw, 1, parent);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    private static DAType daType(String qualifiedName) {
        return daType(qualifiedName, Collections.<DAType>emptyList());
    }

    private static DAType daType(String qualifiedName, List<DAType> typeArgs) {
        DAType annotationDAType = new DAType();
        annotationDAType.simpleName = DANameFactory.from(qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1));
        annotationDAType.qualifiedName = DANameFactory.from(qualifiedName);
        annotationDAType.typeArgs = typeArgs;
        return annotationDAType;
    }

    private DAClassWriter<DAWriter> daClassWriter(TestWriters testWriters, String className) {
        return new DAClassWriter<DAWriter>(className, testWriters.bw, 1, new DAWriter());
    }

    private static class TestWriters {
        final StringWriter out = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(out);

        String getRes() throws IOException {
            bw.flush();
            return out.getBuffer().toString();
        }
    }
}
