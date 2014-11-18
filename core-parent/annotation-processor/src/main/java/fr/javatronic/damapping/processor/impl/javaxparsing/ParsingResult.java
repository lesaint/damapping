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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static fr.javatronic.damapping.processor.impl.javaxparsing.ParsingStatus.FAILED;
import static fr.javatronic.damapping.processor.impl.javaxparsing.ParsingStatus.HAS_UNRESOLVED;
import static fr.javatronic.damapping.processor.impl.javaxparsing.ParsingStatus.OK;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * ParsingResult -
 *
 * @author Sébastien Lesaint
 */
public class ParsingResult {
  @Nonnull
  private final TypeElement classElement;
  @Nonnull
  private final ParsingStatus parsingStatus;
  @Nonnull
  private final Set<Element> unresolved;
  @Nullable
  private final DAType type;
  @Nullable
  private final DASourceClass sourceClass;

  private ParsingResult(@Nonnull TypeElement classElement,
                        @Nonnull ParsingStatus parsingStatus,
                        @Nullable Set<Element> unresolved,
                        @Nullable DAType type,
                        @Nullable DASourceClass sourceClass) {
    this.classElement = checkNotNull(classElement);
    this.parsingStatus = checkNotNull(parsingStatus);
    this.unresolved = unresolved == null ? Collections.<Element>emptySet() : Collections.unmodifiableSet(unresolved);
    this.type = type;
    this.sourceClass = sourceClass;
  }

  public static ParsingResult failed(TypeElement classElement) {
    return new ParsingResult(classElement, FAILED, null, null, null);
  }

  public static ParsingResult failed(TypeElement classElement, @Nullable DAType daType) {
    return new ParsingResult(classElement, FAILED, null, daType, null);
  }

  public static ParsingResult ok(TypeElement classElement, DASourceClass sourceClass) {
    return new ParsingResult(classElement, OK, null, sourceClass.getType(), sourceClass);
  }

  public static ParsingResult later(TypeElement classElement, @Nullable DAType daType, Set<Element> unresolved) {
    return new ParsingResult(classElement, HAS_UNRESOLVED, unresolved, daType, null);
  }

  @Nonnull
  public TypeElement getClassElement() {
    return classElement;
  }

  @Nonnull
  public ParsingStatus getParsingStatus() {
    return parsingStatus;
  }

  @Nonnull
  public Set<Element> getUnresolved() {
    return unresolved;
  }

  @Nullable
  public DAType getType() {
    return type;
  }

  @Nullable
  public DASourceClass getSourceClass() {
    return sourceClass;
  }
}
