/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.model;

import fr.phan.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.phan.damapping.processor.model.visitor.DAModelVisitable;
import fr.phan.damapping.processor.model.visitor.DAModelVisitor;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.FluentIterable;

import static fr.phan.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;

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
   * non utilisé tant que pas de @MapperMethod, pour l'instant on utilise
   * {@link fr.phan.damapping.processor.model.predicate.DAMethodPredicates#isGuavaFunction()}
   */
  private final boolean mapperMethod;
  /**
   * Indique si cette méthode était annotée avec @MapperFactoryMethod
   */
  private final boolean mapperFactoryMethod;

  private DAMethod(Builder builder, boolean mapperMethod, boolean mapperFactoryMethod) {
    this.constructor = builder.constructor;
    this.name = builder.name;
    this.annotations = nonNullFrom(builder.annotations);
    this.modifiers = nonNullFrom(builder.modifiers);
    this.returnType = builder.returnType;
    this.parameters = nonNullFrom(builder.parameters);
    this.mapperMethod = mapperMethod;
    this.mapperFactoryMethod = mapperFactoryMethod;
  }

  public static Builder methodBuilder() {
    return new Builder(false);
  }

  public static Builder constructorBuilder() {
    return new Builder(true);
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

  @Override
  public void accept(DAModelVisitor visitor) {
    visitor.visit(this);
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

    private static boolean computeMapperFactoryMethodFlag(List<DAAnnotation> daAnnotations) {
      return FluentIterable.from(daAnnotations).filter(DAAnnotationPredicates.isMapperFactoryMethod()).first().isPresent();
    }
  }
}
