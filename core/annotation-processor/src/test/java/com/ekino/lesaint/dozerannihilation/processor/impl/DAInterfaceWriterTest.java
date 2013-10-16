package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static com.ekino.lesaint.dozerannihilation.processor.impl.AbstractDAWriter.*;
import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfaceWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceWriterTest {
    @Test
    public void empty_interface() throws Exception {
        TestWriters testWriters = new TestWriters();
        daInterfaceWriter(testWriters, "name").start().end();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "interface name {" + LINE_SEPARATOR + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR);
    }

    @Test
    public void public_empty_interface() throws Exception {
        TestWriters testWriters = new TestWriters();
        daInterfaceWriter(testWriters, "name")
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start().end();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "public interface name {" + LINE_SEPARATOR + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR);
    }

    @Test
    public void annoted_empty_interface() throws Exception {
        TestWriters testWriters = new TestWriters();
        daInterfaceWriter(testWriters, "name")
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.NULLABLE_ANNOTATION))
                .start().end();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "@Nullable" + LINE_SEPARATOR
                + INDENT + "interface name {" + LINE_SEPARATOR + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR);
    }

    @Test
    public void empty_interface_extends_once() throws Exception {
        TestWriters testWriters = new TestWriters();
        daInterfaceWriter(testWriters, "name")
                .withExtended(ImmutableList.of(DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE))
                .start().end();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "interface name extends Function<Integer, String> {" + LINE_SEPARATOR + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR);
    }

    @Test
    public void empty_interface_extends_twice() throws Exception {
        TestWriters testWriters = new TestWriters();
        daInterfaceWriter(testWriters, "name")
                .withExtended(ImmutableList.of(DAWriterTestUtil.SERIALIZABLE_INTERFACE, DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE))
                .start().end();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "interface name extends Serializable, Function<Integer, String> {" + LINE_SEPARATOR + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR);
    }

    private DAInterfaceWriter<DAWriter> daInterfaceWriter(TestWriters testWriters, String interfaceName) {
        DAWriter parent = new DAWriter() {

        };
        return new DAInterfaceWriter<DAWriter>(interfaceName, testWriters.bw, parent, 1);
    }
}
