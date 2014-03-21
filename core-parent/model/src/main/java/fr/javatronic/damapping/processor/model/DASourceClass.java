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
package fr.javatronic.damapping.processor.model;

import fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates;
import fr.javatronic.damapping.processor.model.predicate.DAMethodPredicates;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitable;
import fr.javatronic.damapping.processor.model.visitor.DAModelVisitor;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static fr.javatronic.damapping.processor.model.util.ImmutabilityHelper.nonNullFrom;

/**
 * DASourceClass - Représente la classe annotée avec @Mapper
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DASourceClass implements DAModelVisitable {
  @Nonnull
  private final DAType type;
  @Nullable
  private final DAName packageName;
  @Nonnull
  private final List<DAAnnotation> annotations;
  @Nonnull
  private final Set<DAModifier> modifiers;
  @Nonnull
  private final List<DAInterface> interfaces;
  @Nonnull
  private final List<DAMethod> methods;
  @Nonnull
  private final List<DAEnumValue> enumValues;
  @Nonnull
  private final InstantiationType instantiationType;

  private DASourceClass(Builder<?> builder, InstantiationType instantiationType) {
    this.type = builder.getType();
    this.packageName = builder.getPackageName();
    this.annotations = nonNullFrom(builder.getAnnotations());
    this.modifiers = nonNullFrom(builder.getModifiers());
    this.interfaces = nonNullFrom(builder.getInterfaces());
    this.methods = nonNullFrom(builder.getMethods());
    this.enumValues = nonNullFrom(builder.getEnumValues());
    this.instantiationType = instantiationType;
  }

  public static ClassBuilder classbuilder(@Nonnull DAType type) {
    return new ClassBuilder(type);
  }

  public static EnumBuilder enumBuilder(@Nonnull DAType type, @Nullable List<DAEnumValue> enumValues) {
    return new EnumBuilder(type, enumValues);
  }

  @Nonnull
  public DAType getType() {
    return type;
  }

  @Nullable
  public DAName getPackageName() {
    return packageName;
  }

  @Nonnull
  public List<DAAnnotation> getAnnotations() {
    return annotations;
  }

  @Nonnull
  public Set<DAModifier> getModifiers() {
    return modifiers;
  }

  @Nonnull
  public List<DAInterface> getInterfaces() {
    return interfaces;
  }

  @Nonnull
  public List<DAMethod> getMethods() {
    return methods;
  }

  @Nonnull
  public List<DAEnumValue> getEnumValues() {
    return enumValues;
  }

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

    T withPackageName(DAName packageName);

    T withAnnotations(List<DAAnnotation> annotations);

    T withModifiers(Set<DAModifier> modifiers);

    T withInterfaces(List<DAInterface> interfaces);

    T withMethods(List<DAMethod> methods);

    DASourceClass build();

    DAType getType();

    DAName getPackageName();

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
    private DAName packageName;
    private List<DAAnnotation> annotations;
    private Set<DAModifier> modifiers;
    private List<DAInterface> interfaces;
    private List<DAMethod> methods;

    protected AbstractBuilder(Class<T> clazz, boolean isEnum, @Nonnull DAType type) {
      this.clazz = clazz;
      this.isEnum = isEnum;
      this.type = checkNotNull(type);
    }

    public T withPackageName(DAName packageName) {
      this.packageName = packageName;
      return clazz.cast(this);
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
      return new DASourceClass(
          this,
          computeInstantiationType(nonNullFrom(this.annotations), nonNullFrom(this.methods), this.isEnum)
      );
    }

    private static InstantiationType computeInstantiationType(@Nonnull List<DAAnnotation> daAnnotations,
                                                              @Nonnull List<DAMethod> daMethods,
                                                              boolean isEnumFlag) {
      Optional<DAMethod> mapperFactoryConstructor = from(daMethods)
          .filter(DAMethodPredicates.isConstructor())
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

      Optional<DAAnnotation> springComponentAnnotation = from(daAnnotations).filter(
          DAAnnotationPredicates.isSpringComponent()).first();
      if (springComponentAnnotation.isPresent()) {
        return InstantiationType.SPRING_COMPONENT;
      }
      return InstantiationType.CONSTRUCTOR;
    }

    @Override
    public DAType getType() {
      return type;
    }

    @Override
    public DAName getPackageName() {
      return packageName;
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