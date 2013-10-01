package com.ekino.lesaint.dozerannihilation.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

@Mapper
public class ConstructorInstancedGuavaFunction implements Function<Integer, String> {

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }
}
