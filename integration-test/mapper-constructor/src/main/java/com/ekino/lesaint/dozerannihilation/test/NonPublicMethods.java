package com.ekino.lesaint.dozerannihilation.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.subpackage.OutOfPackage;

@Mapper
public class NonPublicMethods implements Function<Integer, String> {

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }

    private OutOfPackage method_a(String a_param) {
        return null; // implementation doesn't matter
    }

    protected OutOfPackage method_b(String b_param) {
        return null; // implementation doesn't matter
    }

    OutOfPackage method_c(String c_param) {
        return null; // implementation doesn't matter
    }

    private void method_d(OutOfPackage bru) {
        // implementation doesn't matter
    }
}
