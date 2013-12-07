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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.ImportVisitable;
import fr.phan.damapping.processor.model.ImportVisitor;
import fr.phan.damapping.processor.model.InstantiationType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* DASourceClass - Représente la class annotée avec @Mapper
*
* @author Sébastien Lesaint
*/
@Immutable
class DASourceClass implements ImportVisitable {
    @Nonnull
    private final TypeElement classElement;
    @Nonnull
    private final DAType type;
    @Nullable
    private final DAName packageName;
    @Nonnull
    private final Set<Modifier> modifiers;
    @Nonnull
    private final List<DAInterface> interfaces;
    @Nonnull
    private final List<DAMethod> methods;
    @Nonnull
    // specific to the class annoted with @Mapper
    private final InstantiationType instantiationType;

    private DASourceClass(Builder builder) {
        this.classElement = builder.classElement;
        this.type = builder.type;
        this.packageName = builder.packageName;
        this.modifiers = builder.modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(builder.modifiers);
        this.interfaces = builder.interfaces == null ? Collections.<DAInterface>emptyList() : ImmutableList.copyOf(builder.interfaces);
        this.methods = builder.methods == null ? Collections.<DAMethod>emptyList() : ImmutableList.copyOf(builder.methods);
        this.instantiationType = builder.instantiationType;
    }

    public static Builder builder(@Nonnull TypeElement classElement, @Nonnull DAType type) {
        return new Builder(classElement, type);
    }

    @Nonnull
    TypeElement getClassElement() {
        return classElement;
    }

    @Nonnull
    DAType getType() {
        return type;
    }

    @Nullable
    DAName getPackageName() {
        return packageName;
    }

    @Nonnull
    Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Nonnull
    List<DAInterface> getInterfaces() {
        return interfaces;
    }

    @Nonnull
    List<DAMethod> getMethods() {
        return methods;
    }

    @Nonnull
    InstantiationType getInstantiationType() {
        return instantiationType;
    }

    @Override
    public void visite(ImportVisitor visitor) {
        visitor.addMapperImport(type.getQualifiedName());
        visitor.addMapperImplImport(type.getQualifiedName());
        visitor.addMapperFactoryClassImport(type.getQualifiedName());
        visitor.addMapperFactoryInterfaceImport(type.getQualifiedName());
        visitor.addMapperFactoryImplImport(type.getQualifiedName());
        for (DAInterface daInterface : interfaces) {
            daInterface.visite(visitor);
        }
        for (DAMethod daMethod : Iterables.filter(methods, DAMethodPredicates.isGuavaFunction())) {
            daMethod.visite(visitor);
        }
    }

    public static class Builder {
        private final TypeElement classElement;
        private final DAType type;
        private DAName packageName;
        private Set<Modifier> modifiers;
        private List<DAInterface> interfaces;
        private List<DAMethod> methods;
        // specific to the class annoted with @Mapper
        private InstantiationType instantiationType;

        public Builder(@Nonnull TypeElement classElement, @Nonnull DAType type) {
            this.classElement = checkNotNull(classElement);
            this.type = checkNotNull(type);
        }

        public Builder withPackageName(DAName packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withModifiers(Set<Modifier> modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public Builder withInterfaces(List<DAInterface> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder withMethods(List<DAMethod> methods) {
            this.methods = methods;
            return this;
        }

        public Builder withInstantiationType(InstantiationType instantiationType) {
            this.instantiationType = instantiationType;
            return this;
        }

        public DASourceClass build() {
            checkNotNull(this.instantiationType, "IntantiationType is mandatory");
            return new DASourceClass(this);
        }
    }

}
