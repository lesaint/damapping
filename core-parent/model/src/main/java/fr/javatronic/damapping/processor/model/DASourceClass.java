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
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DASourceClass - Represents a class or an enum annotated with {@link fr.javatronic.damapping.annotation.Mapper}.
 *
 * @author Sébastien Lesaint
 */
public interface DASourceClass extends DAModelVisitable {
  /**
   * The type of the source class.
   *
   * @return a {@link DAType}
   */
  @Nonnull
  DAType getType();

  /**
   * The name of the package the source class belongs to or {@code null} if the
   * source class belongs to the default/unamed package.
   *
   * @return a {@link DAName} or {@code null}
   */
  @Nullable
  DAName getPackageName();

  /**
   * The annotations directly annotating the source class.
   *
   * @return a {@link List} of {@link DAAnnotation}
   */
  @Nonnull
  List<DAAnnotation> getAnnotations();

  /**
   * Convenience method to retrieve the {@link DAAnnotation} of the {@code javax.inject.Injectable} annotation
   * on the source class if it exists.
   *
   * @return a {@link Optional} of {@link DAAnnotation}
   */
  @Nonnull
  Optional<DAAnnotation> getInjectableAnnotation();

  /**
   * The modifiers on the source class.
   *
   * @return a {@link Set} of {@link DAModifier}
   */
  @Nonnull
  Set<DAModifier> getModifiers();

  /**
   * The interfaces directly implemented by the source class.
   *
   * @return a {@linkn List} of {@link DAInterface}
   */
  @Nonnull
  List<DAInterface> getInterfaces();

  /**
   * The methods directly defined in the source class (including constructors).
   *
   * @return a {@link List} of {@link DAMethod}
   */
  @Nonnull
  List<DAMethod> getMethods();

  /**
   * Convenience method to retrieve the accessible (ie. non-private) constructors in the
   * list returned by {@link #getMethods()}.
   *
   * @return a {@link List} of {@link DAMethod}
   */
  @Nonnull
  List<DAMethod> getAccessibleConstructors();

  /**
   * If the source class is actually an enum, the list of enum values, otherwise an empty List.
   *
   * @return a {@link List} of {@link DAEnumValue}
   */
  @Nonnull
  List<DAEnumValue> getEnumValues();

  /**
   * Enum indicating how this class/enum can be instanciated.
   *
   * @return a {@linkn InstantiationType}
   */
  @Nonnull
  InstantiationType getInstantiationType();
}
