package fr.phan.damapping.demo;

import fr.phan.damapping.demo.StringToInteger;

import javax.annotation.Nullable;

/**
 * StringToIntegerMapperImpl - il s'agit de l'implémentation réellement exécutée au runtime.
 *
 * Elle utilise StringToIntegerMapperFactory afin de déléguer son implémentation à l'une des implémentations originales
 * (StringToInteger) déterminées par StringToIntegerMapperFactoryImpl.
 *
 * Cette implémentation n'est pas destinée à être utilisée directement en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class StringToIntegerMapperImpl implements StringToIntegerMapper {
    private final StringToInteger stringToInteger;

    protected StringToIntegerMapperImpl(StringToInteger stringToInteger) {
        this.stringToInteger = stringToInteger;
    }

    @Override
    public Integer apply(@Nullable String input) {
        return stringToInteger.apply(input);
    }
}
