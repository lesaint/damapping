package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import com.google.common.base.Preconditions;

/**
 * DANameFactory -
 *
 * @author Sébastien Lesaint
 */
public final class DANameFactory {
    private DANameFactory() {
        // prevents instantiation
    }

    /**
     * Crée un objet DAName à partir d'un objet Name non {@code null}
     *
     * @param name un {@link Name}
     *
     * @return un {@link DAName}
     */
    @Nonnull
    public static DAName from(@Nonnull Name name) {
        return new DAName(name.toString());
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
     * @return
     *
     * @throws IllegalArgumentException si {@code kink.isPrimitive()} retourne false
     * TOIMPROVE :
     */
    @Nonnull
    public static DAName fromPrimitiveKind(TypeKind kind) {
        Preconditions.checkArgument(kind.isPrimitive());
        return from(kind.name().toLowerCase(Locale.US));
    }
}
