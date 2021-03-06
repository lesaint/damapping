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

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NULLABLE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAPropertyWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAPropertyWriterTest {

  @Test
  public void property() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daPropertyWriter(fileContext).write();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "Function<Integer, String> name;" + LINE_SEPARATOR
    );
  }

  @Test
  public void private_property() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daPropertyWriter(fileContext)
        .withModifiers(DAModifier.PRIVATE)
        .write();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "private Function<Integer, String> name;" + LINE_SEPARATOR
    );
  }

  @Test
  public void annoted_property() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daPropertyWriter(fileContext)
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION))
        .write();

    assertThat(fileContext.getRes()).isEqualTo(INDENT + "@Nullable" + LINE_SEPARATOR
        + INDENT + "Function<Integer, String> name;" + LINE_SEPARATOR
    );
  }

  private static DAPropertyWriter<DAWriter> daPropertyWriter(FileContextTestImpl fileContext) {
    DAWriter parent = new DAWriter() {

    };
    return new DAPropertyWriter<DAWriter>("name", DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE, fileContext,
        parent, 1
    );
  }
}
