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
package fr.javatronic.damapping.processor.model;

import javax.annotation.Nonnull;

/**
 * DAAnnotationMember - Represents a Member of an annotation in source code (ie. not the member
 * defined in the annotation class, the member and its value written in source code).
 *
 * @author Sébastien Lesaint
 */
public interface DAAnnotationMember extends DAElement {
  /**
   * The name of the member (ie. the name of the abstract method).
   *
   * @return a {@link String}
   */
  @Nonnull
  String getName();

  /**
   * The return type of the annotation member.
   *
   * @return a {@link DAType}
   */
  @Nonnull
  DAType getType();

  /**
   * The value of the member of the annotation as String suitable to represent the value in source code.
   *
   * @return a {@link String}
   *
   * @see javax.lang.model.element.AnnotationValue#toString()
   */
  @Nonnull
  String getValue();
}
