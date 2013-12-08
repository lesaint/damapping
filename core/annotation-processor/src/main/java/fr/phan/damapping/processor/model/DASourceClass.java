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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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
    public TypeElement getClassElement() {
        return classElement;
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
    public Set<Modifier> getModifiers() {
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
