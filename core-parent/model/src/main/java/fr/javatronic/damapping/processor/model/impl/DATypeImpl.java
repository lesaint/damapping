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
package fr.javatronic.damapping.processor.model.impl;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.util.FluentIterable;
import fr.javatronic.damapping.util.Function;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DATypeImpl - Implementation of DAType as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DATypeImpl implements DAType {
  @Nonnull
  private final DATypeKind kind;
  private final boolean array;
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
  @Nullable
  private final DAName packageName;
  @Nonnull
  private final List<DAType> typeArgs;
  @Nullable
  private final DAType superBound;
  @Nullable
  private final DAType extendsBound;

  private DATypeImpl(Builder builder) {
    this.kind = builder.kind;
    this.array = builder.array;
    this.simpleName = checkNotNull(builder.simpleName);
    this.qualifiedName = builder.qualifiedName;
    this.packageName = DANameFactory.packageNameFromQualified(this.qualifiedName);
    this.typeArgs = builder.typeArgs == null ? Collections.<DAType>emptyList() : FluentIterable
        .from(builder.typeArgs)
        .transform(new Function<DAType, DAType>() {
                     @Nullable
                     @Override
                     public DAType apply(@Nullable DAType daType) {
                       if (daType == SelfDAType.SELF) {
                         return DATypeImpl.this;
                       }
                       return daType;
                     }
                   }
        ).toList();
    this.superBound = builder.superBound;
    this.extendsBound = builder.extendsBound;
  }

  @Override
  @Nonnull
  public DATypeKind getKind() {
    return kind;
  }

  @Override
  @Nonnull
  public DAName getSimpleName() {
    return simpleName;
  }

  @Override
  @Nullable
  public DAName getQualifiedName() {
    return qualifiedName;
  }

  @Override
  @Nonnull
  public DAName getPackageName() {
    return packageName;
  }

  @Override
  @Nonnull
  public List<DAType> getTypeArgs() {
    return typeArgs;
  }

  @Override
  @Nullable
  public DAType getSuperBound() {
    return superBound;
  }

  @Override
  @Nullable
  public DAType getExtendsBound() {
    return extendsBound;
  }

  @Override
  public boolean isArray() {
    return array;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DATypeImpl daType = (DATypeImpl) o;

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

  public static Builder typeBuilder(@Nonnull DATypeKind kind, @Nonnull DAName simpleName) {
    return new Builder(false, kind, simpleName);
  }

  public static Builder arrayBuilder(@Nonnull DATypeKind kind, @Nonnull DAName simpleName) {
    return new Builder(true, kind, simpleName);
  }

  public static class Builder {
    private final boolean array;
    private final DATypeKind kind;
    private final DAName simpleName;
    DAName qualifiedName;
    List<DAType> typeArgs;
    DAType superBound;
    DAType extendsBound;

    public Builder(boolean array, @Nonnull DATypeKind kind, @Nonnull DAName simpleName) {
      this.array = array;
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
      return new DATypeImpl(this);
    }
  }

  @Override
  public String toString() {
    return "DATypeImpl{" + kind + ":" + (qualifiedName == null ? simpleName : qualifiedName)
        + toString(typeArgs)
        + '}';
  }

  private static String toString(List<DAType> typeArgs) {
    if (typeArgs.isEmpty()) {
      return "";
    }

    StringBuilder res = new StringBuilder();
    res.append('<');
    Iterator<DAType> iterator = typeArgs.iterator();
    while (iterator.hasNext()) {
      res.append(iterator.next().getSimpleName());
      if (iterator.hasNext()) {
        res.append(',');
      }
    }
    res.append('>');
    return res.toString();
  }

  /**
   * SelfDAType - Can be used as a DAType in the type argument provided to the builder to specify the current DAType as
   * a type argument.
   * <p>
   * Eg. when building
   * </p>
   *
   * @author Sébastien Lesaint
   */
  public static enum SelfDAType implements DAType {
    SELF;

    @Nonnull
    @Override
    public DATypeKind getKind() {
      return DATypeKind.NONE;
    }

    @Nonnull
    @Override
    public DAName getSimpleName() {
      return DANameFactory.from("<_self_>");
    }

    @Nullable
    @Override
    public DAName getQualifiedName() {
      return null;
    }

    @Nullable
    @Override
    public DAName getPackageName() {
      return null;
    }

    @Nonnull
    @Override
    public List<DAType> getTypeArgs() {
      return Collections.emptyList();
    }

    @Nullable
    @Override
    public DAType getSuperBound() {
      return null;
    }

    @Nullable
    @Override
    public DAType getExtendsBound() {
      return null;
    }

    @Override
    public boolean isArray() {
      return false;
    }
  }
}
