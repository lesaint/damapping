package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.List;
import javax.annotation.Nullable;
import javax.lang.model.type.TypeKind;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * DAType - Représente class, array, enum avec support des générics afin de générer du code source
 *
 * @author Sébastien Lesaint
 */
class DAType {
    TypeKind kind;
    /* QualifiedName du type, sauf dans le cas des tableaux où il s'agit du qualifedName type contenu dans le tableau */
    DAName qualifiedName;
    /* Name du type, sauf dans le cas des tableaux où il s'agit du name type contenu dans le tableau */
    DAName simpleName;
    List<DAType> typeArgs;

    public boolean isArray() {
        return kind == TypeKind.ARRAY;
    }

    // TODO : cache the list of imports for a specific DAType
    public Iterable<DAName> getImports() {
        return Iterables.concat(
                Iterables.concat(
                        ImmutableList.of(ImmutableList.of(qualifiedName)),
                        Iterables.transform(
                                typeArgs,
                                new Function<DAType, Iterable<DAName>>() {
                                    @Override
                                    public Iterable<DAName> apply(DAType daType) {
                                        return daType.getImports();
                                    }
                                }
                        )
                )
        );
    }
}
