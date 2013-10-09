package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * DAMethodPredicates -
 *
 * @author Sébastien Lesaint
 */
public class DAMethodPredicates {

    private DAMethodPredicates() {
        // prevents instantiation
    }

    private static enum GuavaFunction implements  Predicate<DAMethod> {
        INSTANCE;

        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod != null && daMethod.isGuavaFunction();
        }
    }

    public static Predicate<DAMethod> isGuavaFunction() {
        return GuavaFunction.INSTANCE;
    }
}
