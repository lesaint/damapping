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
package fr.javatronic.damapping.processor.impl.javaxparsing.visitor;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor6;

/**
 * QualifiedNameExtractor - {@link javax.lang.model.element.ElementVisitor} that extracts the qyalified name of a
 * {@link javax.lang.model.element.Element} as a {@link javax.lang.model.element.Name}, if it has one.
 * <p>
 * Usage:
 * <pre>
 * Name qualifiedName = eleemnt.accept(QualifiedNameExtractor.QUALIFIED_NAME_EXTRACTOR, null);
 * </pre>
 * </p>
 *
 * @author Sébastien Lesaint
 */
public class QualifiedNameExtractor extends SimpleElementVisitor6<Name, Void> {
  public static final QualifiedNameExtractor QUALIFIED_NAME_EXTRACTOR = new QualifiedNameExtractor();

  private QualifiedNameExtractor() {
    super();
  }

  @Override
  public Name visitType(TypeElement typeElement, Void aVoid) {
    return typeElement.getQualifiedName();
  }
}
