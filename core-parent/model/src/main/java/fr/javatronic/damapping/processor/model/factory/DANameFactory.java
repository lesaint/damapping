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
package fr.javatronic.damapping.processor.model.factory;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.impl.DANameImpl;
import fr.javatronic.damapping.processor.model.DATypeKind;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Preconditions.checkArgument;

/**
 * DANameFactory - final class exposing static factory methods for DAName class
 *
 * @author Sébastien Lesaint
 */
public final class DANameFactory {

  private static final DAName WILCARD = from(DAType.GENERIC_WILDCARD_SIMPLE_NAME);
  private static final DAName VOID = from(DATypeKind.VOID.name().toLowerCase());

  private DANameFactory() {
    // prevents instantiation
  }

  /**
   * Crée un objet DAName à partir d'une String non {@code null}
   *
   * @param string un {@link String}
   *
   * @return un {@link fr.javatronic.damapping.processor.model.impl.DANameImpl}
   */
  @Nonnull
  public static DANameImpl from(@Nonnull String string) {
    return new DANameImpl(string);
  }

  /**
   * Crée un objet DAName à partir d'un DATypeKind représentant un type primitif
   * <p>
   * TOIMPROVE : DAName for each DATypeKind with flag primitive = true can be
   * cached into a Map and used as constants
   * </p>
   *
   * @param kind un {@link DATypeKind} primitif
   *
   * @return a {@link fr.javatronic.damapping.processor.model.impl.DANameImpl}
   *
   * @throws IllegalArgumentException si {@code kink.isPrimitive()} retourne false
   */
  @Nonnull
  public static DAName fromPrimitiveKind(@Nonnull DATypeKind kind) {
    checkArgument(kind.isPrimitive());
    return from(kind.name().toLowerCase(Locale.US));
  }

  /**
   * Crée un objet DAName contenant un simpleName à partir du DAName spécifié.
   * <p>
   * En pratique, cela consiste à parser le name de {@code daName} et extraire tout ce qui suit le dernier point
   * (s'il y en a un).
   * </p>
   *
   * @param daName a {@link fr.javatronic.damapping.processor.model.impl.DANameImpl}
   *
   * @return a {@link fr.javatronic.damapping.processor.model.impl.DANameImpl}
   */
  @Nonnull
  public static DAName simpleFromQualified(@Nonnull DAName daName) {
    return simpleFromQualified(daName.getName());
  }

  /**
   * Crée un objet DAName contenant un simpleName à partir de la String spécifiée représentant un qualifiedName ou
   * un name..
   * <p>
   * En pratique, cela consiste à parser la String {@code qualifiedName} et extraire tout ce qui suit le dernier point
   * (s'il y en a un).
   * </p>
   *
   * @param qualifiedName a {@link String}
   *
   * @return a {@link fr.javatronic.damapping.processor.model.impl.DANameImpl}
   */
  @Nonnull
  public static DAName simpleFromQualified(@Nonnull String qualifiedName) {
    return from(qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1));
  }

  @Nullable
  public static DAName packageNameFromQualified(@Nullable DAName qualifiedName) {
    if (qualifiedName == null) {
      return null;
    }


    String str = qualifiedName.getName();
    int dotIndex = str.lastIndexOf(".");
    if (dotIndex < 0) {
      return null;
    }
    return from(str.substring(0, dotIndex));
  }

  /**
   * Le DAName représentant le wildcard générique "?".
   */
  @Nonnull
  public static DAName wildcard() {
    return WILCARD;
  }

  /**
   * Le DAName représentant le wildcard void "void".
   */
  @Nonnull
  public static DAName voidDAName() {
    return VOID;
  }
}
