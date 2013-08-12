package com.ekino.lesaint.dozerannihilation.demo;

import java.com.ekino.lesaint.dozerannihilation.demo.StringToIntegerMapper;
import java.math.BigDecimal;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.FactoryMethod;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactory;

/**
 * StringToInteger -
 *
 * @author Sébastien Lesaint
 */
@MapperFactory
public enum StringToInteger implements Function<String, Integer>, StringToIntegerMapper {
    BIG_DECIMAL(true),
    INTEGER(false);

    private final boolean bigDecimal;

    @FactoryMethod
    public static StringToInteger bigDecimal() {
        return BIG_DECIMAL;
    }

    @FactoryMethod
    public static StringToInteger integer() {
        return INTEGER;
    }

    @FactoryMethod
    public static StringToInteger instance(boolean bigDecimal) {
        if (bigDecimal) {
            return BIG_DECIMAL;
        }
        return INTEGER;
    }

    private StringToInteger(boolean bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    @Override
    public Integer apply(java.lang.String input) {
        if (bigDecimal) {
            return new BigDecimal(input).intValue();
        }
        return Integer.parseInt(input);
    }
}
