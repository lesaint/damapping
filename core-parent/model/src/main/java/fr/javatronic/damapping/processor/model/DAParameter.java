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
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * DAParameter - Represents a parameter of a method with name, type, modifiers and annotations.
 *
 * @author Sébastien Lesaint
 */
public interface DAParameter extends DAElement {
  /**
   * the name of the parameter.
   *
   * @return a {@link DAName}
   */
  @Nonnull
  DAName getName();

  /**
   * The type of the parameter.
   *
   * @return a {@link DAType}
   */
  @Nonnull
  DAType getType();

  /**
   * The modifiers of the parameter.
   *
   * @return a {@link Set} of {@link DAModifier}
   */
  @Nonnull
  Set<DAModifier> getModifiers();

  /**
   * The annotations on the parameter.
   *
   * @return a {@link List} of {@link DAAnnotation}
   */
  @Nonnull
  List<DAAnnotation> getAnnotations();
}
