package com.ekino.lesaint.dozerannihilation.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.subpackage.OutOfPackage;

@Mapper(InstantiationType.CONSTRUCTOR)
public class HandleCustomTypes implements Function<OutOfPackage, InPackage> {

    @Override
    public InPackage apply(@Nullable OutOfPackage input) {
        return new InPackage();
    }
}
