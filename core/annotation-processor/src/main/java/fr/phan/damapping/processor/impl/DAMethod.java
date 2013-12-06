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

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.ImportVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
* DAMethod -
*
* @author Sébastien Lesaint
*/
@Immutable
class DAMethod extends AbstractImportVisitable {
    /**
     * Le ElementKind de la méthode : soit {@link ElementKind.CONSTRUCTOR}, soit {@ŀink ElementKind.METHOD}
     */
    @Nonnull
    private final ElementKind kind;
    /**
     * nom de la méthode
     */
    @Nonnull
    private final DAName name;
    /**
     * modifiers de la méthode (private, final, static, abstract, ...)
     */
    @Nonnull
    private final Set<Modifier> modifiers;
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
     * non utilisé tant que pas de @MapperMethod, pour l'instant on utilise {@link #isGuavaFunction()}
     */
    private final boolean mapperMethod;
    /**
     * Indique si cette méthode était annotée avec @MapperFactoryMethod
     */
    private final boolean mapperFactoryMethod;

    private DAMethod(Builder builder) {
        this.kind = builder.kind;
        this.name = builder.name;
        this.modifiers = builder.modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(builder.modifiers);
        this.returnType = builder.returnType;
        this.parameters = builder.parameters == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(builder.parameters);
        this.mapperMethod = builder.mapperMethod;
        this.mapperFactoryMethod = builder.mapperFactoryMethod;
    }

    public static Builder builder(@Nonnull ElementKind kind) {
        return new Builder(kind);
    }

    @Nonnull
    ElementKind getKind() {
        return kind;
    }

    @Nonnull
    DAName getName() {
        return name;
    }

    @Nonnull
    Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Nullable
    DAType getReturnType() {
        return returnType;
    }

    @Nonnull
    List<DAParameter> getParameters() {
        return parameters;
    }

    boolean isMapperMethod() {
        return mapperMethod;
    }

    boolean isMapperFactoryMethod() {
        return mapperFactoryMethod;
    }

    public boolean isDefaultConstructor() {
        return isConstructor() && parameters.isEmpty();
    }

    public boolean isConstructor() {
        return kind == ElementKind.CONSTRUCTOR;
    }

    public boolean isGuavaFunction() {
        // TOIMPROVE, check more specific info in the model, can we know if method override from an interface ? we should check the parameter type and the return type
        return kind == ElementKind.METHOD && "apply".equals(name.getName());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        if (isGuavaFunction()) {
            // guava function is not generated in Mapper interface because it is declared by implemented Function interface
            return;
        }
        if (isDefaultConstructor()) {
            // constructor is not generated in Mapper interface
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImport(parameter.getType().getImports());
        }
        if (returnType != null) {
            visitor.addMapperImport(returnType.getImports());
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        if (isDefaultConstructor()) {
            // constructor is not generated in MapperImpl class
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImplImport(parameter.getType().getImports());
        }
        if (returnType != null) {
            visitor.addMapperImplImport(returnType.getImports());
        }
    }

    @Override
    protected void visiteForMapperFactoryClass(ImportVisitor visitor) {
        // none
    }

    @Override
    protected void visiteForMapperFactoryInterface(ImportVisitor visitor) {
        // mapperFactoryMethod are exposed as methods of the MapperFactory
        if (mapperFactoryMethod) {
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryInterfaceImport(parameter.getType().getImports());
            }
        }
    }

    @Override
    protected void visiteForMapperFactoryImpl(ImportVisitor visitor) {
        // mapperFactoryMethod are exposed as methods of the MapperFactory
        if (isConstructor()&& mapperFactoryMethod) {
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryImplImport(parameter.getType().getImports());
            }
        }

        if (isGuavaFunction()) { // remplacer par isMapperMethod
            for (DAParameter parameter : parameters) {
                visitor.addMapperFactoryImplImport(parameter.getType().getImports());
            }
        }
    }

    public static class Builder {
        @Nonnull
        private final ElementKind kind;
        @Nonnull
        private DAName name;
        @Nullable
        private Set<Modifier> modifiers;
        @Nullable
        private DAType returnType;
        @Nullable
        private List<DAParameter> parameters;
        private boolean mapperMethod;
        private boolean mapperFactoryMethod;

        public Builder(@Nonnull ElementKind kind) {
            checkArgument(kind == ElementKind.CONSTRUCTOR || kind == ElementKind.METHOD,
                    "ElementKind of a DAMethod instance can only be either CONSTRUCTOR or METHOD"
            );
            this.kind = kind;
        }

        public Builder withName(@Nonnull DAName name) {
            this.name = name;
            return this;
        }

        public Builder withModifiers(@Nullable Set<Modifier> modifiers) {
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

        public Builder withMapperMethod(boolean mapperMethod) {
            this.mapperMethod = mapperMethod;
            return this;
        }

        public Builder withMapperFactoryMethod(boolean mapperFactoryMethod) {
            this.mapperFactoryMethod = mapperFactoryMethod;
            return this;
        }

        public DAMethod build() {
            return new DAMethod(this);
        }
    }
}
