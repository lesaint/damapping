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

import fr.javatronic.damapping.processor.impl.javaxparsing.JavaxDANameFactory;
import fr.javatronic.damapping.processor.model.DAName;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor6;

/**
 * QualifiedDANameExtractor - {@link javax.lang.model.element.ElementVisitor} that extracts the
 * {@link fr.javatronic.damapping.processor.model.DAName} representing the qualified name of the
 * {@link javax.lang.model.element.Element} if is has one.
 * <p>
 * Usage:
 * <pre>
 *  DAName qualifiedName = eleemnt.accept(QualifiedDANameExtractor.QUALIFIED_DANAME_EXTRACTOR, null);
 * </pre>
 * </p>
 *
 * @author Sébastien Lesaint
 */
public class QualifiedDANameExtractor extends SimpleElementVisitor6<DAName, Void> {
  public static final QualifiedDANameExtractor QUALIFIED_DANAME_EXTRACTOR = new QualifiedDANameExtractor();

  private QualifiedDANameExtractor() {
    super();
  }

  @Override
  public DAName visitPackage(PackageElement e, Void aVoid) {
    return JavaxDANameFactory.from(e.getQualifiedName());
  }

  @Override
  public DAName visitType(TypeElement e, Void aVoid) {
    return JavaxDANameFactory.from(e.getQualifiedName());
  }
}
