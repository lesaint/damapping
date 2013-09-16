package com.ekino.lesaint.dozerannihilation.test;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.google.common.base.Function;

import javax.annotation.Nullable;

@Mapper(InstantiationType.CONSTRUCTOR)
public class ConstructorInstancedGuavaFunction implements Function<Integer, String> {

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }
}
