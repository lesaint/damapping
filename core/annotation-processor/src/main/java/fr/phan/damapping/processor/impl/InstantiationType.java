package fr.phan.damapping.processor.impl;

/**
 * Différentes façon d'instancier la classe annotée @Mapper, pour lui déléguer l'implémentation
 * de l'interface générée *Mapper.
 */
public enum InstantiationType {
    /**
     * Type d'instantiation pour une classe annotée avec @Mapper et @Component
     */
    SPRING_COMPONENT,
    /**
     * Type d'instantiation par défaut pour une classe annotée avec @Mapper
     */
    CONSTRUCTOR,
    /**
     * Type d'instantiation par défaut pour une enum annotée avec @Mapper.
     * L'enum ne doit avoir qu'une seule valeur (singleton enum pattern)
     */
    SINGLETON_ENUM,
    /**
     * Type d'instantiation pour une classe sans constructeur par défaut mais avec un ou plusieurs
     * constructeurs annotés avec @MapperFactoryMethod
     */
    CONSTRUCTOR_FACTORY,
    /**
     * Type d'instantiation pour une classe/enum avec des méthodes statiques annotées avec @MapperFactoryMethod
     */
    STATIC_FACTORY;
}