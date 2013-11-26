package fr.phan.damapping.demo;

import com.google.common.base.Function;

/**
 * EnumIntegerToStringMapper - cette interface est directement déduite de EnumIntegerToString.
 * Elle se contente de tirer la même interface que l'implémentation originale du Mapper annoté.
 *
 * Comme il s'agit ici d'une implémentation de Function Guava, la méthode automatiquement exportée est apply.
 *
 * Lorsqu'un mapper est annoté @Mapper (comme ici pour EnumIntegerToString), n'importe quel collaborateur
 * devra alors se faire injecter EnumIntegerToStringMapper (et non pas EnumIntegerToString directement).
 *
 * @author Sébastien Lesaint
 */
public interface EnumIntegerToStringMapper extends Function<Integer, String> {
}
