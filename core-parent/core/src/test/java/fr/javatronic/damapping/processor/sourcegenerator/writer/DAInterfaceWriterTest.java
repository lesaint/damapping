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

import static fr.javatronic.damapping.processor.model.constants.JavaIOConstants.SERIALIZABLE_TYPE;
import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NULLABLE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfaceWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceWriterTest {
  @Test
  public void empty_interface() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name").start().end();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "interface name {" + LINE_SEPARATOR
            + INDENT + "}"
    );
  }

  @Test
  public void public_empty_interface() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name")
        .withModifiers(DAModifier.PUBLIC)
        .start().end();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "public interface name {" + LINE_SEPARATOR
            + INDENT + "}"
    );
  }

  @Test
  public void annoted_empty_interface() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name")
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION))
        .start().end();

    assertThat(fileContext.getRes()).isEqualTo(INDENT + "@Nullable" + LINE_SEPARATOR
        + INDENT + "interface name {" + LINE_SEPARATOR
        + INDENT + "}"
    );
  }

  @Test
  public void empty_interface_extends_once() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name")
        .withExtended(ImmutableList.of(FUNCTION_INTEGER_TO_STRING_INTERFACE))
        .start().end();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "interface name extends Function<Integer, String> {" + LINE_SEPARATOR
            + INDENT + "}"
    );
  }

  @Test
  public void empty_interface_extends_twice() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name")
        .withExtended(ImmutableList.of(SERIALIZABLE_TYPE, FUNCTION_INTEGER_TO_STRING_INTERFACE))
        .start().end();

    assertThat(fileContext.getRes()).isEqualTo(
        INDENT + "interface name extends Serializable, Function<Integer, String> {" + LINE_SEPARATOR
            + INDENT + "}"
    );
  }

  @Test
  public void one_method_interface() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daInterfaceWriter(fileContext, "name")
        .start()
        .newMethod("methodName", FUNCTION_INTEGER_TO_STRING_INTERFACE)
        .withAnnotations(OVERRIDE_ANNOTATION)
        .write()
        .end();

    assertThat(fileContext.getRes()).isEqualTo(INDENT + "interface name {" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + INDENT + INDENT + "@Override" + LINE_SEPARATOR
        + INDENT + INDENT + "Function<Integer, String> methodName();" + LINE_SEPARATOR
        + INDENT + "}"
    );
  }

  private DAInterfaceWriter<DAWriter> daInterfaceWriter(FileContextTestImpl fileContext, String interfaceName) {
    DAWriter parent = new DAWriter() {

    };
    return new DAInterfaceWriter<DAWriter>(interfaceName, fileContext, parent, 1);
  }
}
