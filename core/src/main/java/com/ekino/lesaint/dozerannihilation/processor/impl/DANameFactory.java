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

    public static DAName from(@Nonnull Name name) {
        return new DAName(name.toString());
    }
}
