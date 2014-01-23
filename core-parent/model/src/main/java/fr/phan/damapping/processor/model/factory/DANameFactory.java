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
package fr.phan.damapping.processor.model.factory;

import com.google.common.base.Preconditions;
import fr.phan.damapping.processor.model.DAName;

import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import java.util.Locale;

/**
 * DANameFactory - final class exposing static factory methods for DAName class
 *
 * @author Sébastien Lesaint
 */
public final class DANameFactory {

    private static final DAName WILCARD = from("?");
    private static final DAName VOID = from("void");

    private DANameFactory() {
        // prevents instantiation
    }

    /**
     * Crée un objet DAName à partir d'une String non {@code null}
     *
     * @param string un {@link String}
     *
     * @return un {@link DAName}
     */
    @Nonnull
    public static DAName from(@Nonnull String string) {
        return new DAName(string);
    }

    /**
     * Crée un objet DAName à partir d'un TypeKind représentant un type primitif
     *
     * @param kind un {@link TypeKind} primitif
     *
     * @return a {@link DAName}
     *
     * @throws IllegalArgumentException si {@code kink.isPrimitive()} retourne false
     *
     * TOIMPROVE : DAName for each TypeKind with flag primitive = true can be cached into a Map and used as constants
     */
    @Nonnull
    public static DAName fromPrimitiveKind(@Nonnull TypeKind kind) {
        Preconditions.checkArgument(kind.isPrimitive());
        return from(kind.name().toLowerCase(Locale.US));
    }

    /**
     * Crée un objet DAName contenant un simpleName à partir du DAName spécifié.
     * <br/>
     * En pratique, cela consiste à parser le name de {@code daName} et extraire tout ce qui suit le dernier point
     * (s'il y en a un).
     *
     * @param daName a {@link DAName}
     *
     * @return a {@link DAName}
     */
    @Nonnull
    public static DAName simpleFromQualified(@Nonnull DAName daName) {
        String qualifiedName = daName.getName();
        return from(qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1));
    }

    /**
     * Le DAName représentant le wildcard générique "?".
     */
    @Nonnull
    public static DAName wildcard() {
        return WILCARD;
    }

    /**
     * Le DAName représentant le wildcard void "void".
     */
    @Nonnull
    public static DAName voidDAName() {
        return VOID;
    }
}
