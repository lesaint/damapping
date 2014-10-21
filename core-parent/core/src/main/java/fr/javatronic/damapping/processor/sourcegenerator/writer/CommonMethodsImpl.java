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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nullable;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
class CommonMethodsImpl implements CommonMethods {
  protected static final String INDENT = "    ";

  private final BufferedWriter bw;
  private final int indentOffset;

  public CommonMethodsImpl(BufferedWriter bw, int indentOffset) {
    this.bw = bw;
    this.indentOffset = indentOffset;
  }

  @Override
  public BufferedWriter getBufferedWriter() {
    return bw;
  }

  @Override
  public int getIndentOffset() {
    return indentOffset;
  }

  @Override
  public void appendIndent() throws IOException {
    for (int i = 0; i < indentOffset; i++) {
      bw.append(INDENT);
    }
  }

  @Override
  public void appendModifiers(@Nullable DAModifier[] modifiers) throws IOException {
    if (modifiers == null) {
      return;
    }
    for (DAModifier modifier : new TreeSet<DAModifier>(Arrays.asList(modifiers))) {
      appendModifier(modifier);
    }
  }

  private void appendModifier(@Nullable DAModifier modifier) throws IOException {
    if (modifier != null) {
      bw.append(modifier.toString()).append(" ");
    }
  }

  @Override
  public void appendAnnotations(Collection<DAAnnotation> annotations) throws IOException {
    if (annotations.isEmpty()) {
      return;
    }

    for (DAAnnotation annotation : annotations) {
      appendIndent();
      bw.append("@").append(annotation.getType().getSimpleName());
      bw.newLine();
    }
  }

  @Override
  public void appendInlineAnnotations(Collection<DAAnnotation> annotations) throws IOException {
    if (annotations.isEmpty()) {
      return;
    }

    Iterator<DAAnnotation> iterator = annotations.iterator();
    while (iterator.hasNext()) {
      bw.append("@").append(iterator.next().getType().getSimpleName());
      if (iterator.hasNext()) {
        bw.append(",");
      }
      bw.append(" ");
    }
  }

  @Override
  public void appendType(DAType type) throws IOException {
    bw.append(type.getSimpleName());
    if (type.getExtendsBound() != null) {
      bw.append(" extends ");
      appendType(type.getExtendsBound());
    }
    else if (type.getSuperBound() != null) {
      bw.append(" super ");
      appendType(type.getExtendsBound());
    }
    appendTypeArgs(type.getTypeArgs());
    if (type.isArray()) {
      bw.append("[]");
    }
  }

  @Override
  public void appendTypeArgs(List<DAType> typeArgs) throws IOException {
    if (!typeArgs.isEmpty()) {
      Iterator<DAType> iterator = typeArgs.iterator();
      bw.append("<");
      while (iterator.hasNext()) {
        DAType arg = iterator.next();
        appendType(arg);
        if (iterator.hasNext()) {
          bw.append(", ");
        }
      }
      bw.append(">");
    }
  }

  @Override
  public void newLine() throws IOException {
    bw.newLine();
  }

  @Override
  public void flush() throws IOException {
    bw.flush();
  }

  @Override
  public void close() throws IOException {
    bw.close();
  }

  @Override
  public Writer append(CharSequence csq) throws IOException {
    return bw.append(csq);
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) throws IOException {
    return bw.append(csq, start, end);
  }

  @Override
  public Writer append(char c) throws IOException {
    return bw.append(c);
  }
}
