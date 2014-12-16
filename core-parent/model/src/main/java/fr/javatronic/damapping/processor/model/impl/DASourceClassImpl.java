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

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;
import fr.javatronic.damapping.util.Optional;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates.isInjectable;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isConstructor;
import static fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates.isNotPrivate;
import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DASourceClassImpl - Implements DASourceClass as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DASourceClassImpl implements DASourceClass {
  @Nonnull
  private final DAType type;
  @Nonnull
  private final List<DAAnnotation> annotations;
  @Nonnull
  private final Optional<DAAnnotation> injectableAnnotation;
  @Nonnull
  private final Set<DAModifier> modifiers;
  @Nonnull
  private final List<DAInterface> interfaces;
  @Nonnull
  private final List<DAMethod> methods;
  @Nonnull
  private final List<DAMethod> accessibleConstructors;
  @Nonnull
  private final List<DAEnumValue> enumValues;
  @Nonnull
  private final InstantiationType instantiationType;

  private DASourceClassImpl(Builder<?> builder, List<DAMethod> daMethods, InstantiationType instantiationType) {
    this.type = builder.getType();
    this.annotations = nonNullFrom(builder.getAnnotations());
    this.injectableAnnotation = from(this.annotations).firstMatch(isInjectable());
    this.modifiers = nonNullFrom(builder.getModifiers());
    this.interfaces = nonNullFrom(builder.getInterfaces());
    this.methods = nonNullFrom(daMethods);
    this.accessibleConstructors = from(this.methods).filter(isConstructor()).filter(isNotPrivate()).toList();
    this.enumValues = nonNullFrom(builder.getEnumValues());
    this.instantiationType = instantiationType;
  }

  public static ClassBuilder classbuilder(@Nonnull DAType type) {
    return new ClassBuilder(type);
  }

  public static EnumBuilder enumBuilder(@Nonnull DAType type, @Nullable List<DAEnumValue> enumValues) {
    return new EnumBuilder(type, enumValues);
  }

  @Override
  @Nonnull
  public DAType getType() {
    return type;
  }

  @Override
  @Nullable
  public DAName getPackageName() {
    return type.getPackageName();
  }

  @Override
  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * The {@link fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl} from {@link #annotations} which
   * represents the {@link Injectable} annotation on the
   * dedicated class (if it exists).
   *
   * @return a {@link Optional} of {@link fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl}
   */
  @Override
  @Nonnull
  public Optional<DAAnnotation> getInjectableAnnotation() {
    return injectableAnnotation;
  }

  @Override
  @Nonnull
  public Set<DAModifier> getModifiers() {
    return modifiers;
  }

  @Override
  @Nonnull
  public List<DAInterface> getInterfaces() {
    return interfaces;
  }

  @Override
  @Nonnull
  public List<DAMethod> getMethods() {
    return methods;
  }

  /**
   * The {@link fr.javatronic.damapping.processor.model.impl.DAMethodImpl}(s) from {@link #methods} which represents
   * a non-private constructor in the dedicated class
   * (if any).
   *
   * @return a {@link List} of {@link fr.javatronic.damapping.processor.model.impl.DAMethodImpl}
   */
  @Override
  @Nonnull
  public List<DAMethod> getAccessibleConstructors() {
    return accessibleConstructors;
  }

  @Override
  @Nonnull
  public List<DAEnumValue> getEnumValues() {
    return enumValues;
  }

  @Override
  @Nonnull
  public InstantiationType getInstantiationType() {
    return instantiationType;
  }

  @Override
  public void accept(DAModelVisitor visitor) {
    visitor.visit(this);
    for (DAInterface daInterface : interfaces) {
      daInterface.accept(visitor);
    }
    for (DAMethod method : methods) {
      method.accept(visitor);
    }
  }

  public static interface Builder<T extends Builder> {

    T withAnnotations(List<DAAnnotation> annotations);

    T withModifiers(Set<DAModifier> modifiers);

    T withInterfaces(List<DAInterface> interfaces);

    T withMethods(List<DAMethod> methods);

    DASourceClass build();

    DAType getType();

    List<DAAnnotation> getAnnotations();

    Set<DAModifier> getModifiers();

    List<DAInterface> getInterfaces();

    List<DAMethod> getMethods();

    List<DAEnumValue> getEnumValues();

  }

  public static abstract class AbstractBuilder<T extends Builder> implements Builder<T> {

    private final boolean isEnum;
    private final Class<T> clazz;
    private final DAType type;
    private List<DAAnnotation> annotations;
    private Set<DAModifier> modifiers;
    private List<DAInterface> interfaces;
    private List<DAMethod> methods;

    protected AbstractBuilder(Class<T> clazz, boolean isEnum, @Nonnull DAType type) {
      this.clazz = clazz;
      this.isEnum = isEnum;
      this.type = checkNotNull(type);
    }

    @Override
    public T withAnnotations(List<DAAnnotation> annotations) {
      this.annotations = annotations;
      return clazz.cast(this);
    }

    public T withModifiers(Set<DAModifier> modifiers) {
      this.modifiers = modifiers;
      return clazz.cast(this);
    }

    public T withInterfaces(List<DAInterface> interfaces) {
      this.interfaces = interfaces;
      return clazz.cast(this);
    }

    public T withMethods(List<DAMethod> methods) {
      this.methods = methods;
      return clazz.cast(this);
    }

    public DASourceClass build() {
      return new DASourceClassImpl(
          this,
          this.methods,
          computeInstantiationType(this.methods, this.isEnum)
      );
    }

    private static InstantiationType computeInstantiationType(@Nonnull List<DAMethod> daMethods,
                                                              boolean isEnumFlag) {
      Optional<DAMethod> mapperFactoryConstructor = from(daMethods)
          .filter(isConstructor())
          .filter(DAMethodPredicates.isMapperFactoryMethod())
          .first();
      if (mapperFactoryConstructor.isPresent()) {
        return InstantiationType.CONSTRUCTOR_FACTORY;
      }

      Optional<DAMethod> mapperFactoryStaticMethods = from(daMethods)
          .filter(DAMethodPredicates.isStatic())
          .filter(DAMethodPredicates.isMapperFactoryMethod())
          .first();
      if (mapperFactoryStaticMethods.isPresent()) {
        return InstantiationType.STATIC_FACTORY;
      }

      if (isEnumFlag) {
        return InstantiationType.SINGLETON_ENUM;
      }

      return InstantiationType.CONSTRUCTOR;
    }

    @Override
    public DAType getType() {
      return type;
    }

    public List<DAAnnotation> getAnnotations() {
      return annotations;
    }

    @Override
    public Set<DAModifier> getModifiers() {
      return modifiers;
    }

    @Override
    public List<DAInterface> getInterfaces() {
      return interfaces;
    }

    @Override
    public List<DAMethod> getMethods() {
      return methods;
    }
  }

  public static class ClassBuilder extends AbstractBuilder<ClassBuilder> {

    public ClassBuilder(@Nonnull DAType type) {
      super(ClassBuilder.class, false, type);
    }

    @Override
    public List<DAEnumValue> getEnumValues() {
      return null;
    }
  }

  public static class EnumBuilder extends AbstractBuilder<EnumBuilder> {
    @Nullable
    private final List<DAEnumValue> enumValues;

    public EnumBuilder(@Nonnull DAType type, List<DAEnumValue> enumValues) {
      super(EnumBuilder.class, true, type);
      this.enumValues = enumValues;
    }

    @Override
    @Nullable
    public List<DAEnumValue> getEnumValues() {
      return enumValues;
    }

  }

}
