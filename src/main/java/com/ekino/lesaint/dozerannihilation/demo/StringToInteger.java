package com.ekino.lesaint.dozerannihilation.demo;

import java.math.BigDecimal;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.FactoryMethod;
import com.ekino.lesaint.dozerannihilation.annotation.MapperFactory;

/**
 * StringToInteger - Exemple de mapper nécessitant d'être instancié avec un paramètre (implémenté sous forme d'enum,
 * mais ce n'est pas obligatoire)
 *
 * @author Sébastien Lesaint
 */
@MapperFactory
public enum StringToInteger implements Function<String, Integer> {
    BIG_DECIMAL(true),
    INTEGER(false);

    private final boolean bigDecimal;

    @FactoryMethod
    public static StringToInteger bigDecimal() {
        return BIG_DECIMAL;
    }

    /**
     * Factory method
     */
    @FactoryMethod
    public static StringToInteger integer() {
        return INTEGER;
    }

    /**
     * Exemple de factory method à paramètre
     */
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
