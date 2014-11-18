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

import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * DAStatementWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAStatementWriterTest {
  @Mock
  private CommonMethods commonMethods;

  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void start_delegates_to_CommonMethods() throws Exception {
    TestWriters testWriters = new TestWriters();
    methodWriterWithMock(testWriters).start();

    verify(commonMethods).appendIndent();
    verifyNoInteraction(testWriters);
  }

  @Test
  public void append_CharSequence_delegates_to_CommonMethods() throws Exception {
    TestWriters testWriters = new TestWriters();
    String s = "ss";

    methodWriterWithMock(testWriters).append(s);

    verify(commonMethods).append(s);
    verifyNoInteraction(testWriters);
  }

  @Test
  public void append_Char_delegates_to_CommonMethods() throws Exception {
    TestWriters testWriters = new TestWriters();
    char s = 's';

    methodWriterWithMock(testWriters).append(s);

    verify(commonMethods).append(s);
    verifyNoInteraction(testWriters);
  }

  @Test
  public void appendType_delegates_to_CommonMethods() throws Exception {
    TestWriters testWriters = new TestWriters();
    DAType daType = DATypeFactory.from(String.class);

    methodWriterWithMock(testWriters).appendType(daType);

    verify(commonMethods).appendType(daType);
    verifyNoInteraction(testWriters);
  }

  @Test
  public void appendTypeArgs_delegates_to_CommonMethods() throws Exception {
    TestWriters testWriters = new TestWriters();
    List<DAType> daTypes = ImmutableList.of(DATypeFactory.from(String.class));

    methodWriterWithMock(testWriters).appendTypeArgs(daTypes);

    verify(commonMethods).appendTypeArgs(daTypes);
    verifyNoInteraction(testWriters);
  }

  @Test
  public void end_returns_parent_writer() throws Exception {
    TestWriters testWriters = new TestWriters();
    DAWriter parent = new DAWriter() {

    };
    DAStatementWriter<DAWriter> classWriter = new DAStatementWriter<DAWriter>(testWriters, parent, 1);

    assertThat(classWriter.end()).isSameAs(parent);
  }

  @Test
  public void end_adds_semicolon_and_newline() throws Exception {
    TestWriters testWriters = new TestWriters();

    methodWriterWithMock(testWriters).end();

    verify(commonMethods).append(";");
    verify(commonMethods).newLine();
    verifyNoInteraction(testWriters);
  }

  @Test
  public void appendParamValues_add_empty_brackets_if_empty() throws Exception {
    TestWriters testWriters = new TestWriters();
    methodWriter(testWriters).appendParamValues(Collections.<DAParameter>emptyList());

    assertThat(testWriters.getRes()).isEqualTo("()");
  }

  @Test
  public void appendParamValues_with_only_one_parameter() throws Exception {
    TestWriters testWriters = new TestWriters();
    methodWriter(testWriters).appendParamValues(
        Collections.singletonList(
            DAParameter.builder(DANameFactory.from("param"), DATypeFactory.from(String.class))
                       .build()
        )
    );

    assertThat(testWriters.getRes()).isEqualTo("(param)");
  }

  @Test
  public void appendParamValues_with_multiple_parameters() throws Exception {
    TestWriters testWriters = new TestWriters();
    methodWriter(testWriters).appendParamValues(
        ImmutableList.of(
            DAParameter.builder(DANameFactory.from("param1"), DATypeFactory.from(String.class))
                       .build(),
            DAParameter.builder(DANameFactory.from("param2"), DATypeFactory.from(String.class))
                       .build()
        )
    );

    assertThat(testWriters.getRes()).isEqualTo("(param1, param2)");
  }

  private void verifyNoInteraction(TestWriters testWriters) throws IOException {
    verifyNoMoreInteractions(commonMethods);
    assertThat(testWriters.getRes()).isEmpty();
  }

  private DAStatementWriter<DAWriter> methodWriterWithMock(TestWriters testWriters) {
    DAWriter parent = new DAWriter() {

    };
    return new DAStatementWriter<DAWriter>(commonMethods, parent);
  }

  private static DAStatementWriter<DAWriter> methodWriter(TestWriters testWriters) {
    DAWriter parent = new DAWriter() {

    };
    return new DAStatementWriter<DAWriter>(testWriters, parent, 1);
  }
}
