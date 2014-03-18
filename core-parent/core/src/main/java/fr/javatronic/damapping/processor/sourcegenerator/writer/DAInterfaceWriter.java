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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * DAInterfaceWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  private final String name;
  private List<DAType> annotations = Collections.emptyList();
  private Set<DAModifier> modifiers = Collections.emptySet();
  private List<DAType> extended = Collections.emptyList();

  DAInterfaceWriter(String name, BufferedWriter bw, T parent, int indentOffset) {
    super(bw, parent, indentOffset);
    this.name = name;
  }

  public DAInterfaceWriter<T> withAnnotations(List<DAType> annotations) {
    this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
    return this;
  }

  public DAInterfaceWriter<T> withModifiers(Set<DAModifier> modifiers) {
    this.modifiers = modifiers == null ? Collections.<DAModifier>emptySet() : ImmutableSet.copyOf(modifiers);
    return this;
  }

  public DAInterfaceWriter<T> withExtended(List<DAType> extended) {
    this.extended = extended == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(extended);
    return this;
  }

  public DAInterfaceWriter<T> start() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    commons.append("interface ").append(name).append(" ");
    appendExtended();
    commons.append("{");
    commons.newLine();
    commons.newLine();
    return this;
  }

  private void appendExtended() throws IOException {
    if (extended.isEmpty()) {
      return;
    }

    commons.append("extends ");
    Iterator<DAType> it = extended.iterator();
    while (it.hasNext()) {
      commons.appendType(it.next());
      if (it.hasNext()) {
        commons.append(",");
      }
      commons.append(" ");
    }
  }

  public DAInterfaceMethodWriter<DAInterfaceWriter<T>> newMethod(String name, DAType returnType) {
    return new DAInterfaceMethodWriter<DAInterfaceWriter<T>>(name, returnType, commons.getBufferedWriter(),
        commons.getIndentOffset() + 1, this
    );
  }

  public T end() throws IOException {
    commons.appendIndent();
    commons.append("}");
    commons.newLine();
    return parent;
  }
}
