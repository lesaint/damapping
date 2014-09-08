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

import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.function.DAParameterFunctions.toName;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Predicates.notNull;

/**
 * DAStatementWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAStatementWriter<T extends DAWriter> extends AbstractDAWriter<T> {

  DAStatementWriter(BufferedWriter bw, T parent, int indentOffset) {
    super(bw, parent, indentOffset);
  }

  DAStatementWriter(CommonMethods commonMethods, T parent) {
    super(commonMethods, parent);
  }

  public DAStatementWriter<T> start() throws IOException {
    commons.appendIndent();
    return this;
  }

  public DAStatementWriter<T> append(CharSequence s) throws IOException {
    commons.append(s);
    return this;
  }

  public DAStatementWriter<T> append(char c) throws IOException {
    commons.append(c);
    return this;
  }

  public DAStatementWriter<T> appendType(DAType type) throws IOException {
    commons.appendType(type);
    return this;
  }

  public DAStatementWriter<T> appendTypeArgs(List<DAType> typeArgs) throws IOException {
    commons.appendTypeArgs(typeArgs);
    return this;
  }

  public DAStatementWriter<T> appendParamValues(@Nonnull List<DAParameter> params,
                                                @Nullable Function<DAParameter, String> parameterNameFunction) throws IOException {
    if (params.isEmpty()) {
      commons.append("()");
      return this;
    }

    Function<DAParameter, String> toParameterName = parameterNameFunction == null ? toName() : parameterNameFunction;
    Iterator<String> it = from(params).transform(toParameterName).filter(notNull()).toList().iterator();

    commons.append("(");
    while (it.hasNext()) {
      String parameterName = it.next();
      commons.append(parameterName);
      if (it.hasNext()) {
        commons.append(", ");
      }
    }
    commons.append(")");
    return this;
  }

  public DAStatementWriter<T> appendParamValues(@Nonnull List<DAParameter> params) throws IOException {
    return appendParamValues(params, null);
  }

  public T end() throws IOException {
    commons.append(";");
    commons.newLine();
    return parent;
  }
}
