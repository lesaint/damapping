package com.ekino.lesaint.dozerannihilation.processor.impl;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAStatementWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAStatementWriterTest {
    @Test
    public void empty_statement() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter(testWriters).start().end();

        assertThat(testWriters.getRes()).isEqualTo(AbstractDAWriter.INDENT + ";" + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void some_statement() throws Exception {
        TestWriters testWriters = new TestWriters();
        methodWriter(testWriters).start().append("return this").end();

        assertThat(testWriters.getRes()).isEqualTo(AbstractDAWriter.INDENT + "return this;" + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void end_returns_parent_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        DAWriter parent = new DAWriter() {

        };
        DAStatementWriter<DAWriter> classWriter = new DAStatementWriter<DAWriter>(testWriters.bw, parent, 1);

        assertThat(classWriter.end()).isSameAs(parent);
    }

    private static DAStatementWriter<DAWriter> methodWriter(TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAStatementWriter<DAWriter>(testWriters.bw, parent, 1);
    }
}
