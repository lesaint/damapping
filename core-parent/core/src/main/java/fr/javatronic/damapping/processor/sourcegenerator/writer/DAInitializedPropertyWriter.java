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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Lists;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

/**
 * DAPropertyWriter - Writes a property initialized by a statement.
 *
 * @author Sébastien Lesaint
 */
public class DAInitializedPropertyWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  private final String name;
  private final DAType type;
  private List<DAAnnotation> annotations = Collections.emptyList();
  @Nullable
  private DAModifier[] modifiers;

  DAInitializedPropertyWriter(String name, DAType type, BufferedWriter bw, T parent, int indentOffset) {
    super(bw, parent, indentOffset);
    this.name = name;
    this.type = type;
  }

  public DAInitializedPropertyWriter<T> withAnnotations(@Nullable List<DAAnnotation> annotations) {
    this.annotations = annotations == null ? Collections.<DAAnnotation>emptyList() : Lists.copyOf(annotations);
    return this;
  }

  public DAInitializedPropertyWriter<T> withModifiers(@Nullable DAModifier... modifiers) {
    this.modifiers = modifiers;
    return this;
  }

  public DAStatementWriter<DAInitializedPropertyWriter<T>> initialize() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    commons.appendType(type);
    commons.append(" ").append(name);
    commons.append(" = ");
    return new DAStatementWriter<DAInitializedPropertyWriter<T>>(commons.getBufferedWriter(), this, commons.getIndentOffset());
  }

  public T end() throws IOException {
    // ";" and newLine are written written by the statement
    commons.newLine();
    return parent;
  }
}
