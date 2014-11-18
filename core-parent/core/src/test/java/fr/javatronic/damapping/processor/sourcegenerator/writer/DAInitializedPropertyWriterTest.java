/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAModifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.constants.JavaxConstants.NULLABLE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.NAME_DATYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInitializedPropertyWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInitializedPropertyWriterTest {

  @Test
  public void empty_statement_writes_invalide_property_line() throws Exception {
    TestWriters testWriters = new TestWriters();
    daPropertyWriter(testWriters).initialize().end().end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "Name name = ;" + LINE_SEPARATOR
            + LINE_SEPARATOR
    );
  }

  @Test
  public void private_property_with_coonstructor_call() throws Exception {
    TestWriters testWriters = new TestWriters();
    daPropertyWriter(testWriters)
        .withModifiers(DAModifier.PRIVATE)
        .initialize()
          .append("new ").appendType(NAME_DATYPE).append("()").end()
        .end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "private Name name = new Name();" + LINE_SEPARATOR
            + LINE_SEPARATOR
    );
  }

  @Test
  public void annoted_property_with_constructor_call() throws Exception {
    TestWriters testWriters = new TestWriters();
    daPropertyWriter(testWriters)
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION))
        .initialize()
          .append("new ").appendType(NAME_DATYPE).append("()").end()
        .end();

    assertThat(testWriters.getRes()).isEqualTo(INDENT + "@Nullable" + LINE_SEPARATOR
        + INDENT + "Name name = new Name();" + LINE_SEPARATOR
        + LINE_SEPARATOR
    );
  }

  private static DAInitializedPropertyWriter<DAWriter> daPropertyWriter(TestWriters testWriters) {
    DAWriter parent = new DAWriter() {

    };
    return new DAInitializedPropertyWriter<DAWriter>("name", NAME_DATYPE, testWriters,
        parent, 1
    );
  }
}
