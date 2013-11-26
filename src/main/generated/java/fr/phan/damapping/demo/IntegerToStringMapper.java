package fr.phan.damapping.demo;

import com.google.common.base.Function;

/**
 * IntegerToStringMapper - cette interface est directement déduite de IntegerToString.
 * Elle se contente de tirer la même interface que l'implémentation originale du Mapper annoté.
 *
 * Comme il s'agit ici d'une implémentation de Function Guava, la méthode automatiquement exportée est apply.
 *
 * Lorsqu'un mapper est annoté @Mapper (comme ici pour IntegerToString), n'importe quel collaborateur
 * devra alors se faire injecter IntegerToStringMapper (et non pas IntegerToString directement).
 *
 * @author Sébastien Lesaint
 */
public interface IntegerToStringMapper extends Function<Integer, String> {

}
