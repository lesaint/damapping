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
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAType - Représente class, array, enum, type primitif avec support des générics afin de générer du code source
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAType {
  @Nonnull
  private final DATypeKind kind;
  /**
   * Name du type, sauf :
   * <ul>
   * <li>dans le cas des tableaux où il s'agit du name du type contenu dans le tableau</li>
   * <li>dans le cas des types avec wildcard générique où il s'agit de la constante {@link fr.javatronic.damapping
   * .processor.model.factory.DANameFactory#wildcard()}</li>
   * </ul>
   */
  @Nonnull
  private final DAName simpleName;
  /**
   * QualifiedName du type, sauf dans le cas des tableaux où il s'agit du qualifedName type contenu dans le tableau.
   * De plus, si le type est primitif, qualifiedName est null
   */
  @Nullable
  private final DAName qualifiedName;
  @Nonnull
  private final List<DAType> typeArgs;
  @Nullable
  private final DAType superBound;
  @Nullable
  private final DAType extendsBound;

  private DAType(Builder builder) {
    this.kind = builder.kind;
    this.simpleName = checkNotNull(builder.simpleName);
    this.qualifiedName = builder.qualifiedName;
    this.typeArgs = nonNullFrom(builder.typeArgs);
    this.superBound = builder.superBound;
    this.extendsBound = builder.extendsBound;
  }

  @Nonnull
  public DATypeKind getKind() {
    return kind;
  }

  @Nonnull
  public DAName getSimpleName() {
    return simpleName;
  }

  @Nullable
  public DAName getQualifiedName() {
    return qualifiedName;
  }

  @Nonnull
  public String getPackageName() {
    if (qualifiedName == null) {
      return "";
    }
    String str = qualifiedName.getName();
    return str.substring(0, str.length() - (simpleName.length() + 1));
  }

  @Nonnull
  public List<DAType> getTypeArgs() {
    return typeArgs;
  }

  @Nullable
  public DAType getSuperBound() {
    return superBound;
  }

  @Nullable
  public DAType getExtendsBound() {
    return extendsBound;
  }

  public boolean isArray() {
    return kind == DATypeKind.ARRAY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAType daType = (DAType) o;

    if (!simpleName.equals(daType.simpleName)) {
      return false;
    }
    if (kind != daType.kind) {
      return false;
    }
    if (qualifiedName == null ? daType.qualifiedName != null : !qualifiedName.equals(daType.qualifiedName)) {
      return false;
    }
    if (extendsBound == null ? daType.extendsBound != null : !extendsBound.equals(daType.extendsBound)) {
      return false;
    }
    if (superBound == null ? daType.superBound != null : !superBound.equals(daType.superBound)) {
      return false;
    }
    if (!typeArgs.equals(daType.typeArgs)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = kind.hashCode();
    result = 31 * result + simpleName.hashCode();
    result = 31 * result + (qualifiedName != null ? qualifiedName.hashCode() : 0);
    result = 31 * result + typeArgs.hashCode();
    result = 31 * result + (superBound != null ? superBound.hashCode() : 0);
    result = 31 * result + (extendsBound != null ? extendsBound.hashCode() : 0);
    return result;
  }

  public static Builder builder(@Nonnull DATypeKind kind, @Nonnull DAName simpleName) {
    return new Builder(kind, simpleName);
  }

  public static class Builder {
    private final DATypeKind kind;
    private final DAName simpleName;
    DAName qualifiedName;
    List<DAType> typeArgs;
    DAType superBound;
    DAType extendsBound;

    public Builder(@Nonnull DATypeKind kind, @Nonnull DAName simpleName) {
      this.kind = checkNotNull(kind);
      this.simpleName = checkNotNull(simpleName);
    }

    public Builder withQualifiedName(DAName qualifiedName) {
      this.qualifiedName = qualifiedName;
      return this;
    }

    public Builder withTypeArgs(List<DAType> typeArgs) {
      this.typeArgs = typeArgs;
      return this;
    }

    public Builder withSuperBound(DAType superBound) {
      this.superBound = superBound;
      return this;
    }

    public Builder withExtendsBound(DAType extendsBound) {
      this.extendsBound = extendsBound;
      return this;
    }

    public DAType build() {
      return new DAType(this);
    }
  }
}
