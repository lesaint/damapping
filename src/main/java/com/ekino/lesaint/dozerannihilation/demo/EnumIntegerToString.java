package com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

/**
 * EnumIntegerToString -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public enum EnumIntegerToString implements Function<Integer, String> {
    INSTANCE;

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }
}
