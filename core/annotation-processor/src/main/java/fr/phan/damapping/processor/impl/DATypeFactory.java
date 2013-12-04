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
import fr.phan.damapping.processor.model.DAType;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import com.google.common.base.Preconditions;

/**
 * DATypeFactory -
 *
 * @author Sébastien Lesaint
 */
public final class DATypeFactory {
    private DATypeFactory() {
        // prevents instantiation
    }

    /**
     * Créer un objet DAType à partir de la Class d'une enum/classe/annotation.
     * <br/>
     * La liste <code>typeArgs</code> est vide.
     *
     * @param clazz une {@link Class}
     * @return un objet {@link fr.phan.damapping.processor.model.DAType}
     */
    static DAType from(@Nonnull Class<?> clazz) {
        return instance(clazz.getSimpleName(), clazz.getCanonicalName(), Collections.<DAType>emptyList());
    }

    /**
     * Créer un objet DAType à partir de la Class d'une enum/classe/annotation et la liste typeArgs.
     *
     * @param clazz une {@link Class}
     * @return un objet {@link DAType}
     */
    static DAType from(@Nonnull Class<?> clazz, @Nonnull List<DAType> typeArgs) {
        return instance(clazz.getSimpleName(), clazz.getCanonicalName(), typeArgs);
    }

    /**
     * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une enum/classe/interface.
     * <br/>
     * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
     * au caractère après le dernier point.
     * <br/>
     * La liste <code>typeArgs</code> est vide.
     *
     * @param qualifiedName un {@link String}
     * @return un objet {@link DAType}
     */
    static DAType declared(@Nonnull String qualifiedName) {
        return declared(qualifiedName, Collections.<DAType>emptyList());
    }

    /**
     * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une enum/classe/interface
     * et la liste typeArgs.
     * <br/>
     * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
     * au caractère après le dernier point
     *
     * @param qualifiedName un {@link String}
     * @param typeArgs      une {@link List} de {@link DAType}
     *
     * @return un objet {@link DAType}
     */
    static DAType declared(@Nonnull String qualifiedName, @Nonnull List<DAType> typeArgs) {
        String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        return instance(simpleName, qualifiedName, typeArgs);
    }

    static DAType wildcardWithSuperBound(@Nonnull DAType superbound) {
        return DAType.builder(TypeKind.WILDCARD)
                .withSimpleName(DANameFactory.wildcard())
                .withSuperBound(superbound)
                .build();
    }

    static DAType wildcardWithExtendsBound(@Nonnull DAType extendsBound) {
        return DAType.builder(TypeKind.WILDCARD)
                .withSimpleName(DANameFactory.wildcard())
                .withExtendsBound(extendsBound)
                .build();
    }

    private static DAType instance(String simpleName, String qualifiedName, List<DAType> typeArgs) {
        return instance(DANameFactory.from(simpleName), DANameFactory.from(qualifiedName), typeArgs);
    }

    private static DAType instance(DAName simpleName, DAName qualifiedName, List<DAType> typeArgs) {
        return DAType.builder(TypeKind.DECLARED)
                .withSimpleName(simpleName)
                .withQualifiedName(qualifiedName)
                .withTypeArgs(Preconditions.checkNotNull(typeArgs))
                .build();
    }
}
