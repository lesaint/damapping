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
import javax.annotation.Nullable;

/**
 * DAType - Represents a reference to a type (a class, an interface, an enum) or to an array or a primitive type.
 *
 * @author Sébastien Lesaint
 */
public interface DAType {
  static final String GENERIC_WILDCARD_SIMPLE_NAME = "?";

  /**
   * The kind of type,
   * <p>
   * If the type is an array, this is actually the type of the type in the array
   * </p>
   *
   * @return a {@link DATypeKind} value
   */
  @Nonnull
  DATypeKind getKind();

  /**
   * The simple name of the type:
   * <ul>
   * <li>for arrays: the name of the type contained in the array</li>
   * <li>for generic wildcard: the constant "?"</li>
   * <li>for primitive types (including void): the lowercase their DATypeKind value</li>
   * </ul>
   *
   * @return a {@link DAName}
   */
  @Nonnull
  DAName getSimpleName();

  /**
   * The qualified name of the type (ie. the simpleName prepended with the package name).
   * <p>
   * It can be {@link null} when the current type referenced is unresolved.
   * </p>
   *
   * @return a {@link DAName} or {@code null}
   */
  @Nullable
  DAName getQualifiedName();

  /**
   * The name of the package if the qualified name is known, or the empty String if the qualified name is unkown or if
   * the type belongs to the unamed package.
   *
   * @return a {@link String}
   */
  @Nonnull
  String getPackageName();

  /**
   * The geneic type argument of the type (ie. what's enclosed between {@literal <} and {@literal >}.
   *
   * @return a {@link List} of {@link DAType}
   */
  @Nonnull
  List<DAType> getTypeArgs();

  /**
   * When the current type is the generic wildcard, the superbound of the wildcard if it defined.
   *
   * @return a {@link DAType} or {@code null}
   */
  @Nullable
  DAType getSuperBound();

  /**
   * When the current type is the generic wildcard, the extends bounds of the wildcard if it defined.
   *
   * @return a {@link DAType} or {@code null}
   */
  @Nullable
  DAType getExtendsBound();

  /**
   * Indicates whether the type if an array.
   *
   * @return a boolean
   */
  boolean isArray();
}
