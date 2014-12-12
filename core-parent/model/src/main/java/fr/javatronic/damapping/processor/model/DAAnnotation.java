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

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAAnnotation - Represents a specific annotation in source code.
 * <p>
 * The same annotation (ie. exact same text in source file -- unmeaningful caracters such as blank spaces excluded) on
 * two different elements will be represented with two distinct DAAnnotation objects which
 * may have the exact same content or not (depending on the information which can be collected from the model
 * of the source code for each element).
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface DAAnnotation extends DAElement {
  /**
   * The {@link DAType} of this annotation
   *
   * @return a {@link DAType}
   */
  @Nonnull
  DAType getType();

  /**
   * The {@link DAAnnotation}s representing the annotations on this annotation.
   *
   * @return a {@link List} of {@link DAAnnotation}
   */
  @Nonnull
  List<DAAnnotation> getAnnotations();

  /**
   * A list of DAAnnotationMember representing the member of the annotation explicitly definined
   * for this annotation in source code.
   *
   * @return a {@link List} of {@link DAAnnotationMember}
   */
  @Nonnull
  List<DAAnnotationMember> getAnnotationMembers();
}
