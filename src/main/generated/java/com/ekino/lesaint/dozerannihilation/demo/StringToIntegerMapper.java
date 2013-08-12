package com.ekino.lesaint.dozerannihilation.demo;

import com.google.common.base.Function;

/**
 * StringToIntegerMapper - cette interface est directement déduite de StringToInteger.
 * Elle se contente de tirer la même interface que l'implémentation originale du Mapper annoté.
 *
 * Comme il s'agit ici d'une implémentation de Function Guava, la méthode automatiquement exportée est apply.
 *
 * Lorsqu'un mapper est annoté @MapperFactory (comme ici pour StringToInteger), n'importe quel collaborateur
 * devra alors se faire injecter StringToIntegerMapperFactory (et non pas StringToInteger directement),
 * responsable de l'instanciation des StringToIntegerMapper.
 *
 * @author Sébastien Lesaint
 */
public interface StringToIntegerMapper extends Function<String, Integer> {
}
