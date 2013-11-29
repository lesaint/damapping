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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.impl.AbstractDAWriter;
import fr.phan.damapping.processor.impl.DAStatementWriter;
import fr.phan.damapping.processor.impl.DAWriter;

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
