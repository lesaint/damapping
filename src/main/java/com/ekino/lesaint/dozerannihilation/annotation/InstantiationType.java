package com.ekino.lesaint.dozerannihilation.annotation;

/**
 * Différentes façon d'instancier la classe annotée @Mapper, pour lui déléguer l'implémentation
 * de l'interface générée *Mapper.
 */
public enum InstantiationType {
    SPRING_COMPONENT, CONSTRUCTOR, SINGLETON_ENUM;
}