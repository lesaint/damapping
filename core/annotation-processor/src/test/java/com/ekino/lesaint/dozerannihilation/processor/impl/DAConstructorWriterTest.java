package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractDAWriter.INDENT;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAConstructorWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAConstructorWriterTest {

    @Test
    public void empty_constructor() throws Exception {
        TestWriters testWriters = new TestWriters();
        constructorWriter("com.acme.TotoClass", testWriters).start().end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "TotoClass() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void public_empty_constructor() throws Exception {
        TestWriters testWriters = new TestWriters();
        constructorWriter("com.acme.TotoClass", testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "public TotoClass() {" + LINE_SEPARATOR + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }

    @Test
    public void private_constructor_with_params() throws Exception {
        TestWriters testWriters = new TestWriters();
        constructorWriter("com.acme.TotoClass", testWriters)
                .withModifiers(ImmutableSet.of(Modifier.PRIVATE))
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER))
                .start()
                    .newStatement()
                    .start()
                    .append("Preconditions.checkNotNull(titi)")
                    .end()
                .end();

        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "private TotoClass(String titi) {" + LINE_SEPARATOR
                        + INDENT + INDENT + "Preconditions.checkNotNull(titi);" + LINE_SEPARATOR
                        + INDENT + "}" + LINE_SEPARATOR
                        + LINE_SEPARATOR
                );
    }


    private static DAConstructorWriter<DAWriter> constructorWriter(String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAConstructorWriter<DAWriter>(DATypeFactory.declared(returnType), testWriters.bw, parent, 1);
    }
}
