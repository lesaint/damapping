package com.ekino.lesaint.dozerannihilation.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

@Mapper(InstantiationType.SINGLETON_ENUM)
public enum ReturnArray implements Function<Integer, String[]> {
    INSTANCE;

    @Override
    public String[] apply(@Nullable Integer input) {
        return new String[]{input.toString()};
    }
}
