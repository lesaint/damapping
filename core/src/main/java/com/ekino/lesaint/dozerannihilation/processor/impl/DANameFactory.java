package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.annotation.Nonnull;
import javax.lang.model.element.Name;

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
}
