package com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Nullable;

/**
 * EnumIntegerToStringMapperImpl - il s'agit de l'implémentation réellement exécutée au runtime.
 * Elle utilise EnumIntegerToStringMapperFactory afin de déléguer son implémentation à l'implémentation originale
 * (EnumIntegerToString).
 *
 * Cette implémentation n'est pas destinée à être utilisée directement en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {

    @Override
    public String apply(@Nullable Integer input) {
        return EnumIntegerToStringMapperFactory.instance().apply(input);
    }
}
