package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * DAName -
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAName implements CharSequence {
    @Nonnull
    private final String name;

    public DAName(@Nonnull String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public String toString() {
        return name;
    }
}
