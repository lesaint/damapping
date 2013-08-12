package com.ekino.lesaint.dozerannihilation.demo;


import javax.annotation.Nullable;

class IntegerToStringMapperImpl implements IntegerToStringMapper {

    @Override
    public String apply(@Nullable Integer input) {
        return IntegerToStringMapperFactory.instance().apply(input);
    }
}
