package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractFileGenerator.INDENT;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DATypeFactory.declared;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.daParameter;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAMethodWriterTest -
 *
 * TODO compléter les tests unitaires DAMethodWriter
 * <ul>
 *     <li>tester l'objet retourné par end()</li>
 *     <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAMethodWriterTest {
    private static final DAParameter STRING_TOTO_PARAMETER = daParameter("java.lang.String", "toto");
    private static final DAParameter STRING_TITI_PARAMETER = daParameter("java.lang.String", "titi");
    private static final DAParameter SUPER_COMPLEXE_PARAMETER = superComplexeParameter("complexeParam");

    /**
     * Un paramètre de type tableau de Function<String, Integer>
     */
    private static DAParameter superComplexeParameter(String name) {
        DAParameter res = new DAParameter();
        res.name = DANameFactory.from(name);
        DAType parameterType = declared("com.google.common.base.Function",
                ImmutableList.of(DATypeFactory.declared("java.lang.String"), DATypeFactory.declared("java.lang.Integer")));
        parameterType.kind = TypeKind.ARRAY;
        res.type = parameterType;
        return res;
    }

    @Test
    public void empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void public_static_empty_method() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public static String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void empty_method_one_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(STRING_TOTO_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String toto) {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void empty_method_two_parameters() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withParams(ImmutableList.of(STRING_TITI_PARAMETER, SUPER_COMPLEXE_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "String name(String titi, Function<String, Integer>[] complexeParam) {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    @Test
    public void public_static_empty_method_with_parameter() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter("name", "java.lang.String", testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .withParams(ImmutableList.of(SUPER_COMPLEXE_PARAMETER))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public static String name(Function<String, Integer>[] complexeParam) {" + LINE_SEPARATOR
                        + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
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
                .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR
                        + INDENT + "String name() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
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
                .isEqualTo(INDENT + "String name() {" + LINE_SEPARATOR
                        + INDENT + INDENT + "return this;" + LINE_SEPARATOR
                        + INDENT + "}" + LINE_SEPARATOR + LINE_SEPARATOR
                );
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAMethodWriter<DAWriter> classWriter = new DAMethodWriter<DAWriter>("name", DATypeFactory.declared("java.lang.String"), testWriters.bw, 1, parent);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    private static DAMethodWriter<DAWriter> methodWriter(String name, String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAMethodWriter<DAWriter>(name, DATypeFactory.declared(returnType), testWriters.bw, 1, parent);
    }


}
