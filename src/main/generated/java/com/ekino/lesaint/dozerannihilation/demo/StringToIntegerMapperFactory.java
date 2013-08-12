package com.ekino.lesaint.dozerannihilation.demo;

/**
 * StringToIntegerMapperFactory - il s'agit d'une Factory injectable qui expose les différentes stratégies
 * d'instanciation de l'interface de mapper générée (StringToIntegerMapper), telles que définies par les
 * méthodes annotées @FactoryMethod de StringToInteger.
 *
 * Cette factory EST destinée à être injectée en lieu et place de StringToInteger.
 *
 * @author Sébastien Lesaint
 */
public interface StringToIntegerMapperFactory {

    StringToIntegerMapper bigDecimal();

    StringToIntegerMapper integer();

    StringToIntegerMapper instance(boolean bigDecimal);
}
