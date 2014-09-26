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

import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitable;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAMethod -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAMethod implements DAModelVisitable {
  private final boolean constructor;
  /**
   * nom de la méthode
   */
  @Nonnull
  private final DAName name;
  /**
   * annotations de la méthode
   */
  @Nonnull
  private final List<DAAnnotation> annotations;
  /**
   * modifiers de la méthode (private, final, static, abstract, ...)
   */
  @Nonnull
  private final Set<DAModifier> modifiers;
  /**
   * le type de retour de la méthode. Null si la méthode est un constructeur
   */
  @Nullable
  private final DAType returnType; // TOIMPROVE : attention au cas des primitifs si on ajoute @MapperMethod !
  /**
   * Paramètres de la méthode
   */
  @Nonnull
  private final List<DAParameter> parameters;
  /**
   * Indique que cette méthode est annotée avec @MapperMethod
   */
  private final boolean mapperMethod;
  /**
   * Indique si cette méthode était annotée avec @MapperFactory
   */
  private final boolean mapperFactoryMethod;
  /**
   * Indique que cette méthode est l'implémentation de la méthode apply définie par l'interface Function de Guava.
   */
  private final boolean guavaFunctionApplyMethod;
  /**
   * Indique que la méthode courante est la mapper méthode implicite de la {@link DASourceClass}
   */
  private final boolean implicitMapperMethod;

  private DAMethod(Builder builder, boolean mapperMethod, boolean mapperFactoryMethod) {
    this.constructor = builder.constructor;
    this.name = builder.name;
    this.annotations = nonNullFrom(builder.annotations);
    this.modifiers = nonNullFrom(builder.modifiers);
    this.returnType = builder.returnType;
    this.parameters = nonNullFrom(builder.parameters);
    this.mapperMethod = mapperMethod;
    this.mapperFactoryMethod = mapperFactoryMethod;
    this.guavaFunctionApplyMethod = false;
    this.implicitMapperMethod = false;
  }

  private DAMethod(DAMethod from, boolean guavaFunctionApplyMethod, boolean implicitMapperMethod) {
    this.constructor = from.constructor;
    this.name = from.name;
    this.annotations = from.annotations;
    this.modifiers = from.modifiers;
    this.returnType = from.returnType;
    this.parameters = from.parameters;
    this.mapperMethod = from.mapperMethod;
    this.mapperFactoryMethod = from.mapperFactoryMethod;
    this.guavaFunctionApplyMethod = guavaFunctionApplyMethod;
    this.implicitMapperMethod = implicitMapperMethod;
  }

  public static Builder methodBuilder() {
    return new Builder(false);
  }

  public static Builder constructorBuilder() {
    return new Builder(true);
  }

  @Nonnull
  public static DAMethod makeImpliciteMapperMethod(@Nonnull DAMethod daMethod) {
    return new DAMethod(checkNotNull(daMethod), false, true);
  }

  @Nonnull
  public static DAMethod makeGuavaFunctionApplyMethod(@Nonnull DAMethod daMethod) {
    return new DAMethod(checkNotNull(daMethod), true, false);
  }

  public boolean isConstructor() {
    return constructor;
  }

  @Nonnull
  public DAName getName() {
    return name;
  }

  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  @Nonnull
  public Set<DAModifier> getModifiers() {
    return modifiers;
  }

  @Nullable
  public DAType getReturnType() {
    return returnType;
  }

  @Nonnull
  public List<DAParameter> getParameters() {
    return parameters;
  }

  public boolean isMapperMethod() {
    return mapperMethod;
  }

  public boolean isMapperFactoryMethod() {
    return mapperFactoryMethod;
  }

  public boolean isImplicitMapperMethod() {
    return implicitMapperMethod;
  }

  public boolean isGuavaFunctionApplyMethod() {
    return guavaFunctionApplyMethod;
  }

  @Override
  public void accept(DAModelVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAMethod daMethod = (DAMethod) o;
    if (!name.equals(daMethod.name)) {
      return false;
    }
    if (constructor != daMethod.constructor) {
      return false;
    }
    if (guavaFunctionApplyMethod != daMethod.guavaFunctionApplyMethod) {
      return false;
    }
    if (implicitMapperMethod != daMethod.implicitMapperMethod) {
      return false;
    }
    if (mapperFactoryMethod != daMethod.mapperFactoryMethod) {
      return false;
    }
    if (mapperMethod != daMethod.mapperMethod) {
      return false;
    }
    if (!modifiers.equals(daMethod.modifiers)) {
      return false;
    }
    if (returnType == null ? daMethod.returnType != null : !returnType.equals(daMethod.returnType)) {
      return false;
    }
    if (!annotations.equals(daMethod.annotations)) {
      return false;
    }
    if (!parameters.equals(daMethod.parameters)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = (constructor ? 1 : 0);
    result = 31 * result + name.hashCode();
    result = 31 * result + annotations.hashCode();
    result = 31 * result + modifiers.hashCode();
    result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
    result = 31 * result + parameters.hashCode();
    result = 31 * result + (mapperMethod ? 1 : 0);
    result = 31 * result + (mapperFactoryMethod ? 1 : 0);
    result = 31 * result + (guavaFunctionApplyMethod ? 1 : 0);
    result = 31 * result + (implicitMapperMethod ? 1 : 0);
    return result;
  }

  public static class Builder {
    @Nonnull
    private final boolean constructor;
    @Nonnull
    private DAName name;
    @Nullable
    private List<DAAnnotation> annotations;
    @Nullable
    private Set<DAModifier> modifiers;
    @Nullable
    private DAType returnType;
    @Nullable
    private List<DAParameter> parameters;

    public Builder(boolean constructor) {
      this.constructor = constructor;
    }

    public Builder withName(@Nonnull DAName name) {
      this.name = name;
      return this;
    }

    public Builder withAnnotations(@Nullable List<DAAnnotation> annotations) {
      this.annotations = annotations;
      return this;
    }

    public Builder withModifiers(@Nullable Set<DAModifier> modifiers) {
      this.modifiers = modifiers;
      return this;
    }

    public Builder withReturnType(@Nullable DAType returnType) {
      this.returnType = returnType;
      return this;
    }

    public Builder withParameters(@Nullable List<DAParameter> parameters) {
      this.parameters = parameters;
      return this;
    }

    public DAMethod build() {
      List<DAAnnotation> daAnnotations = nonNullFrom(this.annotations);
      return new DAMethod(this, computeMapperMethodFlag(daAnnotations), computeMapperFactoryMethodFlag(daAnnotations));
    }

    private static boolean computeMapperMethodFlag(List<DAAnnotation> daAnnotations) {
      // TODO implementer isMapperMethod si on ajoute une annotation MapperMethod
      return false;
    }

    private static boolean computeMapperFactoryMethodFlag(@Nonnull List<DAAnnotation> daAnnotations) {
      for (DAAnnotation daAnnotation : daAnnotations) {
        if (DAAnnotationPredicates.isMapperFactoryMethod().apply(daAnnotation)) {
          return true;
        }
      }
      return false;
    }
  }
}
