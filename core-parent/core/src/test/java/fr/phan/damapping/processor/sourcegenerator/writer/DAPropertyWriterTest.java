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
package fr.phan.damapping.processor.sourcegenerator.writer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static fr.phan.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAPropertyWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAPropertyWriterTest {

    @Test
    public void property() throws Exception {
        TestWriters testWriters = new TestWriters();
        daPropertyWriter(testWriters).write();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "Function<Integer, String> name;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void private_property() throws Exception {
        TestWriters testWriters = new TestWriters();
        daPropertyWriter(testWriters)
                .withModifier(ImmutableSet.of(Modifier.PRIVATE))
                .write();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "private Function<Integer, String> name;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void annoted_property() throws Exception {
        TestWriters testWriters = new TestWriters();
        daPropertyWriter(testWriters)
                .withAnnotations(ImmutableList.of(DAWriterTestUtil.NULLABLE_ANNOTATION))
                .write();

        assertThat(testWriters.getRes()).isEqualTo(INDENT + "@Nullable" + DAWriterTestUtil.LINE_SEPARATOR
                + INDENT + "Function<Integer, String> name;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR);
    }

    private static DAPropertyWriter<DAWriter> daPropertyWriter(TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAPropertyWriter<DAWriter>("name", DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE, testWriters.bw, parent, 1);
    }
}
