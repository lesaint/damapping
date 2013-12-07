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

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.type.TypeKind;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * DAType - Représente class, array, enum, type primitif avec support des générics afin de générer du code source
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAType {
    @Nonnull
    private final TypeKind kind;
    /**
     * Name du type, sauf :
     * <ul>
     *     <li>dans le cas des tableaux où il s'agit du name du type contenu dans le tableau</li>
     *     <li>dans le cas des types avec wildcard générique où il s'agit de la constante {@link fr.phan.damapping.processor.model.factory.DANameFactory.wildcard()}</li>
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
    @Nonnull
    private final List<DAType> typeArgs;
    @Nullable
    private final DAType superBound;
    @Nullable
    private final DAType extendsBound;

    private DAType(Builder builder) {
        this.kind = builder.kind;
        this.simpleName = builder.simpleName;
        this.qualifiedName = builder.qualifiedName;
        this.typeArgs = builder.typeArgs == null ? ImmutableList.<DAType>of() : ImmutableList.copyOf(builder.typeArgs);
        this.superBound = builder.superBound;
        this.extendsBound = builder.extendsBound;
    }

    @Nonnull
    public TypeKind getKind() {
        return kind;
    }

    @Nonnull
    public DAName getSimpleName() {
        return simpleName;
    }

    @Nullable
    public DAName getQualifiedName() {
        return qualifiedName;
    }

    @Nonnull
    public List<DAType> getTypeArgs() {
        return typeArgs;
    }

    @Nullable
    public DAType getSuperBound() {
        return superBound;
    }

    @Nullable
    public DAType getExtendsBound() {
        return extendsBound;
    }

    public boolean isArray() {
        return kind == TypeKind.ARRAY;
    }

    // TODO : cache the list of imports for a specific DAType
    public Iterable<DAName> getImports() {
        ImmutableList<DAName> qualifiedName = hasNoName(kind) ? ImmutableList.<DAName>of() : ImmutableList.of(this.qualifiedName);
        Iterable<Iterable<DAName>> typesImports = Iterables.transform(
                typeArgs,
                new Function<DAType, Iterable<DAName>>() {
                    @Override
                    public Iterable<DAName> apply(DAType daType) {
                        return daType.getImports();
                    }
                }
        );
        return Iterables.concat(
                qualifiedName,
                Iterables.concat(typesImports),
                superBound == null ? ImmutableList.<DAName>of() : ImmutableList.copyOf(superBound.getImports()),
                extendsBound == null ? ImmutableList.<DAName>of() : ImmutableList.copyOf(extendsBound.getImports())
        );
    }

    private static boolean hasNoName(TypeKind kind) {
        return kind.isPrimitive() || kind == TypeKind.WILDCARD;
    }

    public static Builder builder(@Nonnull TypeKind kind) {
        return new Builder(kind);
    }

    public static class Builder {
        private final TypeKind kind;
        private DAName simpleName;
        DAName qualifiedName;
        List<DAType> typeArgs;
        DAType superBound;
        DAType extendsBound;

        public Builder(@Nonnull TypeKind kind) {
            this.kind = checkNotNull(kind);
        }

        public Builder withQualifiedName(DAName qualifiedName) {
            this.qualifiedName = qualifiedName;
            return this;
        }

        public Builder withSimpleName(DAName simpleName) {
            this.simpleName = simpleName;
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
            return new DAType(this);
        }
    }
}
