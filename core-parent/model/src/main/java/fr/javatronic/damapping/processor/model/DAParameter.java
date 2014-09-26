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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAParameter - Représente un paramètre de méthode avec son nom, son type et ses éventuels
 * modifieurs ("final" en particulier).
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAParameter {
  /*nom du paramètre*/
  @Nonnull
  private final DAName name;
  @Nonnull
  private final DAType type;
  @Nonnull
  private final Set<DAModifier> modifiers;
  @Nonnull
  private final List<DAAnnotation> annotations;

  private DAParameter(Builder builder) {
    name = builder.name;
    type = builder.type;
    modifiers = nonNullFrom(builder.modifiers);
    annotations = nonNullFrom(builder.annotations);
  }

  @Nonnull
  public static Builder builder(@Nonnull DAName name, @Nonnull DAType type) {
    return new Builder(name, type);
  }

  @Nonnull
  public DAName getName() {
    return name;
  }

  @Nonnull
  public DAType getType() {
    return type;
  }

  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  @Nonnull
  public Set<DAModifier> getModifiers() {
    return modifiers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAParameter that = (DAParameter) o;

    if (!modifiers.equals(that.modifiers)) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    if (!type.equals(that.type)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + modifiers.hashCode();
    return result;
  }

  public static class Builder {
    @Nonnull
    private final DAName name;
    @Nonnull
    private final DAType type;
    @Nullable
    private Set<DAModifier> modifiers;
    @Nullable
    private List<DAAnnotation> annotations;

    public Builder(@Nonnull DAName name, @Nonnull DAType type) {
      this.name = checkNotNull(name);
      this.type = checkNotNull(type);
    }

    public Builder withModifiers(@Nullable Set<DAModifier> modifiers) {
      this.modifiers = modifiers;
      return this;
    }

    public Builder withAnnotations(@Nullable List<DAAnnotation> annotations) {
      this.annotations = annotations;
      return this;
    }

    public DAParameter build() {
      return new DAParameter(this);
    }
  }
}
