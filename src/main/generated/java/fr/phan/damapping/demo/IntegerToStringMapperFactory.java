package fr.phan.damapping.demo;

import fr.phan.damapping.demo.IntegerToString;

/**
 * IntegerToStringMapperFactory - il s'agit d'une Factory statique permettant à IntegerToStringMapperImpl
 * de se faire injecter l'implémentation originale.
 * Ici, nous sommes en mode InstantiationType.CONSTRUCTOR, la méthode d'instanciation se contente donc d'exécuter le
 * constructeur par défaut. Dans une réelle implémentation, il faudrait tout d'abord s'assurer que celui-ci existe.
 *
 * Cette factory n'est pas destinée à être utilisée en dehors de ce framework.
 *
 * @author Sébastien Lesaint
 */
class IntegerToStringMapperFactory {

    public static IntegerToString instance() {
        return new IntegerToString();
    }
}
