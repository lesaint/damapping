package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractFileGenerator.INDENT;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.daParameter;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.daType;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAMethodWriterTest -
 *
 * TODO compléter les tests unitaires DAMethodWriter
 * <ul>
 *     <li>tester plusieurs arguments</li>
 *     <li>tester arguments avec génériques</li>
 *     <li>tester arguments tableau</li>
 *     <li>tester combinaison modifier + arguments</li>
 *     <li>tester l'objet retourné par end()</li>
 *     <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAMethodWriterTest {
    private static final DAParameter STRING_TOTO_PARAMETER = daParameter("java.lang.String", "toto");

    @Test
    public void empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    @Test
    public void empty_method_one_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(STRING_TOTO_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String toto) {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    @Test
    public void public_static_empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public static String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    private static DAMethodWriter<DAWriter> methodWriter(String name, String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAMethodWriter<DAWriter>(name, daType(returnType), testWriters.bw, 1, parent);
    }


}
