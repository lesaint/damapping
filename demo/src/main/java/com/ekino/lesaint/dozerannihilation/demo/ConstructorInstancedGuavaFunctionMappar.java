package com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.google.common.base.Function;

import javax.annotation.Nullable;

@Mapper(InstantiationType.CONSTRUCTOR)
public class ConstructorInstancedGuavaFunctionMappar implements Function<Integer, String> {

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }
}
