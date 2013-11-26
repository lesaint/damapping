package fr.phan.damapping.demo;

import fr.phan.damapping.demo.StringToInteger;

/**
 * StringToIntegerMapperFactoryImpl - implémentation non exposée de la factory de mapper.
 * Elle se contente de construire la nouvelle implémentation de StringToIntegerMapper
 * en fonction des différentes méthodes d'instanciation de StringToInteger annotées
 * MapperFactoryMethod.
 *
 * Cette implémentation n'est pas destinée à être utilisée directement en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class StringToIntegerMapperFactoryImpl implements StringToIntegerMapperFactory {

    @Override
    public StringToIntegerMapper bigDecimal() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper integer() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper instance(boolean bigDecimal) {
        return new StringToIntegerMapperImpl(StringToInteger.instance(bigDecimal));
    }
}
