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

import fr.phan.damapping.processor.model.visitor.DAModelVisitable;
import fr.phan.damapping.processor.model.visitor.DAModelVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;

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
  private final Set<DAModifier> modifiers;
  @Nonnull
  private final List<DAInterface> interfaces;
  @Nonnull
  private final List<DAMethod> methods;
  @Nonnull
  private final List<DAEnumValue> enumValues;
  @Nonnull
  private final InstantiationType instantiationType;

  private DASourceClass(Builder<?> builder) {
    this.type = builder.getType();
    this.packageName = builder.getPackageName();
    this.modifiers = builder.getModifiers() == null ? Collections.<DAModifier>emptySet() : ImmutableSet.copyOf(
        builder.getModifiers()
    );
    this.interfaces = builder.getInterfaces() == null ? Collections.<DAInterface>emptyList() : ImmutableList.copyOf(
        builder.getInterfaces()
    );
    this.methods = builder.getMethods() == null ? Collections.<DAMethod>emptyList() : ImmutableList.copyOf(
        builder.getMethods()
    );
    this.enumValues = builder.getEnumValues() == null ? Collections.<DAEnumValue>emptyList() : ImmutableList.copyOf(
        builder.getEnumValues()
    );
    this.instantiationType = builder.getInstantiationType();
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

    T withModifiers(Set<DAModifier> modifiers);

    T withInterfaces(List<DAInterface> interfaces);

    T withMethods(List<DAMethod> methods);

    T withInstantiationType(InstantiationType instantiationType);

    DASourceClass build();

    DAType getType();

    DAName getPackageName();

    Set<DAModifier> getModifiers();

    List<DAInterface> getInterfaces();

    List<DAMethod> getMethods();

    InstantiationType getInstantiationType();

    List<DAEnumValue> getEnumValues();

  }

  public static abstract class AbstractBuilder<T extends Builder> implements Builder<T> {
    private final Class<T> clazz;
    private final DAType type;
    private DAName packageName;
    private Set<DAModifier> modifiers;
    private List<DAInterface> interfaces;
    private List<DAMethod> methods;
    // specific to the class annoted with @Mapper
    private InstantiationType instantiationType;

    protected AbstractBuilder(Class<T> clazz, @Nonnull DAType type) {
      this.clazz = clazz;
      this.type = checkNotNull(type);
    }

    public T withPackageName(DAName packageName) {
      this.packageName = packageName;
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

    public T withInstantiationType(InstantiationType instantiationType) {
      this.instantiationType = instantiationType;
      return clazz.cast(this);
    }

    public DASourceClass build() {
      checkNotNull(this.instantiationType, "IntantiationType is mandatory");
      return new DASourceClass(this);
    }

    @Override
    public DAType getType() {
      return type;
    }

    @Override
    public DAName getPackageName() {
      return packageName;
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

    @Override
    public InstantiationType getInstantiationType() {
      return instantiationType;
    }
  }

  public static class ClassBuilder extends AbstractBuilder<ClassBuilder> {

    public ClassBuilder(@Nonnull DAType type) {
      super(ClassBuilder.class, type);
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
      super(EnumBuilder.class, type);
      this.enumValues = enumValues;
    }

    @Override
    @Nullable
    public List<DAEnumValue> getEnumValues() {
      return enumValues;
    }

  }

}
