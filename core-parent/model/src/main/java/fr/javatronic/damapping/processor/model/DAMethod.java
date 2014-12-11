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

import fr.javatronic.damapping.processor.model.visitor.DAModelVisitable;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DAMethod -
 *
 * @author Sébastien Lesaint
 */
public interface DAMethod extends DAModelVisitable {
  /**
   * Name of the method.
   *
   * @return a {@link DAName}
   */
  @Nonnull
  DAName getName();

  /**
   * List of the annotations on the method as {@link DAAnnotation} objects.
   *
   * @return a {@link List} of {@link DAAnnotation}
   */
  @Nonnull
  List<DAAnnotation> getAnnotations();

  /**
   * Set of the modifiers applies to the method.
   *
   * @return a {@link Set} of {@link DAModifier}
   */
  @Nonnull
  Set<DAModifier> getModifiers();

  /**
   * The DAType representing the return type of the method or {@code null} when the method returns
   * void.
   *
   * @return a {@link DAType} or {@code null}
   */
  @Nullable
  DAType getReturnType();

  /**
   * The list of parameters of the methods represented as DAParameter objects.
   *
   * @return a {@link List} of {@link DAParameter}
   */
  @Nonnull
  List<DAParameter> getParameters();

  /**
   * Indicates that the method is a constructor.
   *
   * @return a boolean
   */
  boolean isConstructor();

  /**
   * Indicates that the method is annotated with the {@link fr.javatronic.damapping.annotation.MapperMethod}
   * annotation.
   *
   * @return a boolean
   */
  boolean isMapperMethod();

  /**
   * Indicates that the method is annotated with the {@link fr.javatronic.damapping.annotation.MapperFactory}
   * annotation.
   *
   * @return a boolean
   */
  boolean isMapperFactoryMethod();

  /**
   * Indicates that the method has been identified as a mapper method.
   *
   * @return a boolean
   */
  boolean isImplicitMapperMethod();

  /**
   * Indicates that the method override the {@code apply} method of the Guava's {@code Function}
   * interface.
   *
   * @return a boolean
   */
  boolean isGuavaFunctionApplyMethod();
}
