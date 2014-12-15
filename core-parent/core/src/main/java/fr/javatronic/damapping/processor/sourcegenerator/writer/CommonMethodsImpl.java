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
import fr.javatronic.damapping.processor.model.DAAnnotationMember;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.predicate.DATypePredicates;
import fr.javatronic.damapping.util.Preconditions;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Optional.fromNullable;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
class CommonMethodsImpl implements CommonMethods {
  protected static final String INDENT = "    ";

  private final FileContext fileContext;
  private final int indentOffset;

  public CommonMethodsImpl(@Nonnull FileContext fileContext, int indentOffset) {
    this.fileContext = Preconditions.checkNotNull(fileContext);
    this.indentOffset = indentOffset;
  }

  @Override
  public FileContext getFileContext() {
    return fileContext;
  }

  @Override
  public int getIndentOffset() {
    return indentOffset;
  }

  @Override
  public void appendIndent() throws IOException {
    for (int i = 0; i < indentOffset; i++) {
      append(INDENT);
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
      append(modifier.toString()).append(" ");
    }
  }

  @Override
  public void appendAnnotations(Collection<DAAnnotation> annotations) throws IOException {
    if (annotations.isEmpty()) {
      return;
    }

    for (DAAnnotation annotation : annotations) {
      appendIndent();
      append("@").append(annotation.getType().getSimpleName());
      newLine();
    }
  }

  @Override
  public void appendInlineAnnotations(Collection<DAAnnotation> annotations) throws IOException {
    if (annotations.isEmpty()) {
      return;
    }

    Iterator<DAAnnotation> iterator = annotations.iterator();
    while (iterator.hasNext()) {
      DAAnnotation annotation = iterator.next();
      append("@").append(annotation.getType().getSimpleName());
      appendAnnotationMembers(annotation.getAnnotationMembers());
      if (iterator.hasNext()) {
        append(",");
      }
      append(" ");
    }
  }

  private void appendAnnotationMembers(Collection<DAAnnotationMember> annotationMembers) throws IOException {
    if (annotationMembers.isEmpty()) {
      return;
    }
    append('(');
    if (annotationMembers.size() == 1) {
      DAAnnotationMember member = annotationMembers.iterator().next();
      if (!member.getName().equals("value")) {
         append(member.getName()).append(" = ");
      }
      append(member.getValue());
    }
    else {
      Iterator<DAAnnotationMember> memberIterator = annotationMembers.iterator();
      while (memberIterator.hasNext()) {
        DAAnnotationMember member = memberIterator.next();
        append(member.getName()).append(" = ").append(member.getValue());
        if (memberIterator.hasNext()) {
          append(", ");
        }
      }
    }
    append(')');
  }

  @Override
  public void appendType(DAType type) throws IOException {
    append(useQualifiedReference(type) ? type.getQualifiedName() : type.getSimpleName());
    if (type.getExtendsBound() != null) {
      append(" extends ");
      appendType(type.getExtendsBound());
    }
    else if (type.getSuperBound() != null) {
      append(" super ");
      appendType(type.getExtendsBound());
    }
    appendTypeArgs(type.getTypeArgs());
    if (type.isArray()) {
      append("[]");
    }
  }

  /**
   * Compute whether a qualified reference should be used for the specified {@link fr.javatronic.damapping.processor.model.impl.DATypeImpl}.
   * <p>
   *   Compute uses the following test, in order:
   *   <ul>
   *     <li>if the specified type does not represent a declared type or a array of declared types, use simple reference</li>
   *     <li>if the specified type is a type or an array of type from "java.lang", use simple reference</li>
   *     <li>if the specified type has an explicite import, use simple reference</li>
   *     <li>if the specified type has an homonymous amough the explicite imports, use qualified reference</li>
   *     <li>if the specified type belongs to the current package, use implicit reference</li>
   *     <li>if the specified type did not match any of the above rules, raise an error, there is something wrong</li>
   *   </ul>
   * </p>
   *
   * @param type a {@link fr.javatronic.damapping.processor.model.impl.DATypeImpl}
   * @return a flag indicating to use a qualified reference or not²
   */
  private boolean useQualifiedReference(DAType type) {
    if (type.getKind() != DATypeKind.DECLARED) {
      return false;
    }
    if (DATypePredicates.isJavaLangType().apply(type)) {
      return false;
    }
    if (fileContext.hasExpliciteImport(type)) {
      return false;
    }
    if (fileContext.hasHomonymousImport(type)) {
      return true;
    }
    if (type.getPackageName() == null && fileContext.getPackageName().isEmpty()) {
      return false;
    }
    if (type.getPackageName() != null && type.getPackageName().equals(fileContext.getPackageName())) {
      return false;
    }
    // unless import are incomplete (and generated class will not compile), this should not happen
    throw new IllegalArgumentException(
        fromNullable(type.getQualifiedName()).or(type.getSimpleName()) + " is neither imported nor in the " +
            "current package. Can not print reference without creating compilation error");
  }

  @Override
  public void appendTypeArgs(List<DAType> typeArgs) throws IOException {
    if (!typeArgs.isEmpty()) {
      Iterator<DAType> iterator = typeArgs.iterator();
      append("<");
      while (iterator.hasNext()) {
        DAType arg = iterator.next();
        appendType(arg);
        if (iterator.hasNext()) {
          append(", ");
        }
      }
      append(">");
    }
  }

  @Override
  public void newLine() throws IOException {
    fileContext.getWriter().newLine();
  }

  @Override
  public void flush() throws IOException {
    fileContext.getWriter().flush();
  }

  @Override
  public void close() throws IOException {
    fileContext.getWriter().close();
  }

  @Override
  public Writer append(CharSequence csq) throws IOException {
    return fileContext.getWriter().append(csq);
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) throws IOException {
    return fileContext.getWriter().append(csq, start, end);
  }

  @Override
  public Writer append(char c) throws IOException {
    return fileContext.getWriter().append(c);
  }
}
