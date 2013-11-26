package fr.phan.damapping.demo;

import fr.phan.damapping.demo.EnumIntegerToString;

/**
 * EnumIntegerToStringMapperFactory - il s'agit d'une Factory statique permettant à EnumIntegerToStringMapperImpl
 * de se faire injecter l'implémentation originale.
 * Ici, nous sommes en mode InstantiationType.SINGLETON_ENUM, la méthode d'instanciation se contente donc de retourner
 * l'unique instance de l'enum annoté @Mapper.
 *
 * Cette factory n'est pas destinée à être utilisée en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class EnumIntegerToStringMapperFactory {

    public static EnumIntegerToString instance() {
        return EnumIntegerToString.INSTANCE;
    }
}
