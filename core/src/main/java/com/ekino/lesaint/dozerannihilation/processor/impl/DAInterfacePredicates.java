package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * DAInterfacePredicates -
 *
 * @author Sébastien Lesaint
 */
public final class DAInterfacePredicates {
    private DAInterfacePredicates() {
        // prevents instantiation
    }

    private static enum GuavaFunction implements Predicate<DAInterface> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAInterface daInterface) {
            return daInterface != null && daInterface.isGuavaFunction();
        }
    }

    public static Predicate<DAInterface> isGuavaFunction() {
        return GuavaFunction.INSTANCE;
    }
}
