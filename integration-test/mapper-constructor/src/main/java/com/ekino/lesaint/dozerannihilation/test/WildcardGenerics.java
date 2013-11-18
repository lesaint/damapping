package com.ekino.lesaint.dozerannihilation.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.subpackage.OutOfPackage;

/**
 * WildcardGenerics -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class WildcardGenerics implements Function<Optional<? extends OutOfPackage>, String> {
    @Nullable
    @Override
    public String apply(@Nullable Optional<? extends OutOfPackage> optional) {
        return "resultDoesNotMatter";
    }
}
