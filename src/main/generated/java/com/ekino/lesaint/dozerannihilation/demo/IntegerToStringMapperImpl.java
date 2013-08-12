package com.ekino.lesaint.dozerannihilation.demo;


import javax.annotation.Nullable;

/**
 * IntegerToStringMapperImpl - il s'agit de l'implémentation réellement exécutée au runtime.
 * Elle utilise IntegerToStringMapperFactory afin de déléguer son implémentation à l'implémentation originale
 * (IntegerToString).
 *
 * Cette implémentation n'est pas destinée à être utilisée directement en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class IntegerToStringMapperImpl implements IntegerToStringMapper {

    @Override
    public String apply(@Nullable Integer input) {
        return IntegerToStringMapperFactory.instance().apply(input);
    }
}
