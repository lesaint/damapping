package com.ekino.lesaint.dozerannihilation.test;

import com.ekino.lesaint.dozerannihilation.annotation.MapperFactory;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactoryMethod;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * StringToInteger - Exemple de mapper nécessitant d'être instancié avec un paramètre (implémenté sous forme d'enum,
 * mais ce n'est pas obligatoire)
 *
 * @author Sébastien Lesaint
 */
@MapperFactory
public enum MultipleImplementationAsEnum implements Function<String, Integer> {
    BIG_DECIMAL(true),
    INTEGER(false);

    private final boolean bigDecimal;

    @MapperFactoryMethod
    public static MultipleImplementationAsEnum bigDecimal() {
        return BIG_DECIMAL;
    }

    /**
     * Factory method
     */
    @MapperFactoryMethod
    public static MultipleImplementationAsEnum integer() {
        return INTEGER;
    }

    /**
     * Exemple de factory method à paramètre
     */
    @MapperFactoryMethod
    public static MultipleImplementationAsEnum instance(boolean bigDecimal) {
        if (bigDecimal) {
            return BIG_DECIMAL;
        }
        return INTEGER;
    }

    private MultipleImplementationAsEnum(boolean bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    @Override
    public Integer apply(@Nullable String input) {
        if (bigDecimal) {
            return new BigDecimal(input).intValue();
        }
        return Integer.parseInt(input);
    }
}
