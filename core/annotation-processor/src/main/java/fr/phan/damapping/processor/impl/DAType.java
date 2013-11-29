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

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.type.TypeKind;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * DAType - Représente class, array, enum, type primitif avec support des générics afin de générer du code source
 *
 * @author Sébastien Lesaint
 */
class DAType {
    @Nonnull
    TypeKind kind;
    /* QualifiedName du type, sauf dans le cas des tableaux où il s'agit du qualifedName type contenu dans le tableau.
       De plus, si le type est primitif, qualifiedName est null
    */
    @Nullable
    DAName qualifiedName;
    /* Name du type, sauf dans le cas des tableaux où il s'agit du name type contenu dans le tableau */
    @Nonnull
    DAName simpleName;
    @Nonnull
    List<DAType> typeArgs;
    @Nullable
    DAType superBound;
    @Nullable
    DAType extendsBound;

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
}
