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
package fr.phan.damapping.processor.impl.sourcegenerator.writer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static fr.phan.damapping.processor.impl.sourcegenerator.writer.CommonMethodsImpl.INDENT;
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
                .isEqualTo(INDENT + "TotoClass() {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
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
                .isEqualTo(INDENT + "public TotoClass() {" + DAWriterTestUtil.LINE_SEPARATOR + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
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
                .isEqualTo(INDENT + "private TotoClass(String titi) {" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + INDENT + "Preconditions.checkNotNull(titi);" + DAWriterTestUtil.LINE_SEPARATOR
                        + INDENT + "}" + DAWriterTestUtil.LINE_SEPARATOR
                        + DAWriterTestUtil.LINE_SEPARATOR
                );
    }

    @Test
    public void constructor_with_multiple_params() throws Exception {
        TestWriters testWriters = new TestWriters();
        constructorWriter("com.acme.TotoClass", testWriters)
                .withParams(ImmutableList.of(DAWriterTestUtil.STRING_TITI_PARAMETER, DAWriterTestUtil.STRING_TOTO_PARAMETER))
                .start();


        assertThat(testWriters.getRes())
                .isEqualTo(INDENT + "TotoClass(String titi, String toto) {" + DAWriterTestUtil.LINE_SEPARATOR);
    }

    private static DAConstructorWriter<DAWriter> constructorWriter(String returnType, TestWriters testWriters) {
        DAWriter parent = new DAWriter() {

        };
        return new DAConstructorWriter<DAWriter>(DATypeFactory.declared(returnType), testWriters.bw, parent, 1);
    }
}
