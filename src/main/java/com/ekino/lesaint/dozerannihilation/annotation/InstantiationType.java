package com.ekino.lesaint.dozerannihilation.annotation;

/**
 * Différentes façon d'instancier la classe annotée @Mapper, pour lui déléguer l'implémentation
 * de l'interface générée *Mapper.
 */
public enum InstantiationType {
    /**
     * La classe Mapper générée sera annotée avec l'annotation @Component de Spring (sans argument)
     */
    SPRING_COMPONENT,
    /**
     * TODO : je ne sais à quel type d'instantiation correspond CONSTRUCTOR
     */
    CONSTRUCTOR,
    /**
     * La classe Mapper générée sera une enum possédant une seule valeur : INSTANCE. (singleton pattern)
     */
    SINGLETON_ENUM;
}