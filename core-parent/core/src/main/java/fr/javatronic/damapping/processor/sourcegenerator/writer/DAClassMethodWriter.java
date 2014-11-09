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
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Lists;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

/**
 * DAClassMethodWriter - Writer pour les méthodes d'une classe ou enum
 *
 * @author Sébastien Lesaint
 */
public class DAClassMethodWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  private final String name;
  private final DAType returnType;
  @Nullable
  private DAModifier[] modifiers;
  private List<DAAnnotation> annotations = Collections.emptyList();
  private List<DAParameter> params = Collections.<DAParameter>emptyList();

  public DAClassMethodWriter(String name, DAType returnType, FileContext fileContext, int indentOffset, T parent) {
    super(fileContext, parent, indentOffset);
    this.name = name;
    this.returnType = returnType;
  }

  public DAClassMethodWriter<T> withModifiers(@Nullable DAModifier... modifiers) {
    this.modifiers = modifiers;
    return this;
  }

  public DAClassMethodWriter<T> withAnnotations(@Nullable List<DAAnnotation> annotations) {
    this.annotations = annotations == null ? Collections.<DAAnnotation>emptyList() : Lists.copyOf(annotations);
    return this;
  }

  public DAClassMethodWriter<T> withParams(@Nullable List<DAParameter> params) {
    this.params = params == null ? Collections.<DAParameter>emptyList() : Lists.copyOf(params);
    return this;
  }

  public DAClassMethodWriter<T> start() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    appendReturnType();
    commons.append(name);
    appendParams(commons, params);
    commons.append(" {");
    commons.newLine();
    return this;
  }

  private void appendReturnType() throws IOException {
    commons.appendType(returnType);
    commons.append(" ");
  }

  /**
   * Ajoute les parenthèses et les paramètres d'une méthode, les paramètres étant représentés, dans l'ordre
   * par la liste de DAType en argument.
   */
  private void appendParams(CommonMethods commons, List<DAParameter> params) throws IOException {
    if (params.isEmpty()) {
      commons.append("()");
      return;
    }

    commons.append("(");
    Iterator<DAParameter> it = params.iterator();
    while (it.hasNext()) {
      DAParameter parameter = it.next();
      this.commons.appendInlineAnnotations(parameter.getAnnotations());
      this.commons.appendType(parameter.getType());
      commons.append(" ").append(parameter.getName());
      if (it.hasNext()) {
        commons.append(", ");
      }
    }
    commons.append(")");
  }

  public DAStatementWriter<DAClassMethodWriter<T>> newStatement() {
    return new DAStatementWriter<DAClassMethodWriter<T>>(commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  public T end() throws IOException {
    commons.appendIndent();
    commons.append("}");
    commons.newLine();
    return parent;
  }
}
