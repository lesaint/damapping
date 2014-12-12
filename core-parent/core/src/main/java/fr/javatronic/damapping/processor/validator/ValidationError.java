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
package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAElement;
import fr.javatronic.damapping.processor.model.DASourceClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ValidationError - Check exception representing a validation error
 *
 * @author Sébastien Lesaint
 */
public class ValidationError extends Exception {
  @Nonnull
  private final DASourceClass sourceClass;
  @Nullable
  private final DAElement element;
  @Nullable
  private final DAAnnotation annotation;

  public ValidationError(String message,
                         @Nonnull DASourceClass sourceClass,
                         @Nullable DAElement element, @Nullable DAAnnotation annotation) {
    super(message);
    this.sourceClass = sourceClass;
    this.element = element;
    this.annotation = annotation;
  }

  @Nonnull
  public DASourceClass getSourceClass() {
    return sourceClass;
  }

  @Nullable
  public DAElement getElement() {
    return element;
  }

  @Nullable
  public DAAnnotation getAnnotation() {
    return annotation;
  }
}
