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
import fr.javatronic.damapping.processor.model.impl.DANameImpl;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.impl.DATypeImpl;
import fr.javatronic.damapping.processor.model.DATypeKind;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DATypeFactory - final class exposing static Factory methods for DAType class
 *
 * @author Sébastien Lesaint
 */
public final class DATypeFactory {
  private static final DAType VOID_DATYPE = DATypeImpl.typeBuilder(DATypeKind.VOID, DANameFactory.voidDAName()).build();

  private DATypeFactory() {
    // prevents instantiation
  }

  @Nonnull
  public static DAType voidDaType() {
    return VOID_DATYPE;
  }

  /**
   * Créer un objet DAType à partir de la Class d'une enum/classe/annotation.
   * <p>
   * La liste <code>typeArgs</code> est vide.
   * </p>
   *
   * @param clazz une {@link Class}
   *
   * @return un objet {@link fr.javatronic.damapping.processor.model.DAType}
   */
  @Nonnull
  public static DAType from(@Nonnull Class<?> clazz) {
    return instance(clazz.getSimpleName(), clazz.getCanonicalName(), Collections.<DAType>emptyList());
  }

  /**
   * Créer un objet DAType à partir de la Class d'une enum/classe/annotation et la liste typeArgs.
   *
   * @param clazz    une {@link Class}
   * @param typeArgs une {@link List} de {@link DAType}
   *
   * @return un objet {@link DAType}
   */
  @Nonnull
  public static DAType from(@Nonnull Class<?> clazz, @Nonnull List<DAType> typeArgs) {
    return instance(clazz.getSimpleName(), clazz.getCanonicalName(), typeArgs);
  }

  /**
   * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une
   * enum/classe/interface.
   * <p>
   * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
   * au caractère après le dernier point.
   * </p>
   * <p>
   * La liste <code>typeArgs</code> est vide.
   * </p>
   *
   * @param qualifiedName un {@link String}
   *
   * @return un objet {@link DAType}
   */
  @Nonnull
  public static DAType declared(@Nonnull String qualifiedName) {
    return declared(qualifiedName, Collections.<DAType>emptyList());
  }

  /**
   * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une enum/classe/interface
   * et la liste typeArgs.
   * <p>
   * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
   * au caractère après le dernier point
   * </p>
   *
   * @param qualifiedName un {@link String}
   * @param typeArgs      une {@link List} de {@link DAType}
   *
   * @return un objet {@link DAType}
   */
  @Nonnull
  public static DAType declared(@Nonnull String qualifiedName, @Nonnull List<DAType> typeArgs) {
    String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    return instance(simpleName, qualifiedName, typeArgs);
  }

  @Nonnull
  public static DAType wildcardWithSuperBound(@Nonnull DAType superbound) {
    return DATypeImpl.typeBuilder(DATypeKind.WILDCARD, DANameFactory.wildcard())
                 .withSuperBound(superbound)
                 .build();
  }

  @Nonnull
  public static DAType wildcardWithExtendsBound(@Nonnull DAType extendsBound) {
    return DATypeImpl.typeBuilder(DATypeKind.WILDCARD, DANameFactory.wildcard())
                 .withExtendsBound(extendsBound)
                 .build();
  }

  @Nonnull
  private static DAType instance(String simpleName, String qualifiedName, List<DAType> typeArgs) {
    return instance(DANameFactory.from(simpleName), DANameFactory.from(qualifiedName), typeArgs);
  }

  @Nonnull
  private static DAType instance(DAName simpleName, DANameImpl qualifiedName, List<DAType> typeArgs) {
    return DATypeImpl.typeBuilder(DATypeKind.DECLARED, simpleName)
                 .withQualifiedName(qualifiedName)
                 .withTypeArgs(checkNotNull(typeArgs))
                 .build();
  }
}
