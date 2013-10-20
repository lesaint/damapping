package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractFileGenerator.INDENT;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfaceMethodWriterTest -
 *
 * TODO compléter les tests unitaires DAInterfaceMethodWriter
 * <ul>
 *     <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceMethodWriterTest {

    @Test
    public void method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters).write();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name();" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void method_one_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TOTO_PARAMETER))
                .write();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String toto);" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void method_two_parameters() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER, DAWriterTestUtil.FUNCTION_STRING_INTEGER_ARRAY_PARAMETER))
                .write();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String titi, Function<String, Integer>[] complexeParam);" + LINE_SEPARATOR
                        + LINE_SEPARATOR);
    }

    @Test
    public void annoted_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.OVERRIDE_ANNOTATION))
                .write();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR
                        + INDENT + "String name();" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void write_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAInterfaceMethodWriter<DAWriter> classWriter = new DAInterfaceMethodWriter<DAWriter>("name", DATypeFactory.declared("java.lang.String"), testWriters.bw, 1, parent);

        assertThat(classWriter.write()).isSameAs(parent);
    }

    private static DAInterfaceMethodWriter<DAWriter> methodWriter(String name, String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAInterfaceMethodWriter<DAWriter>(name, DATypeFactory.declared(returnType), testWriters.bw, 1, parent);
    }


}
