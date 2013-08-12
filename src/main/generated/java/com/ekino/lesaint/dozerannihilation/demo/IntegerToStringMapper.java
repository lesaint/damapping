package com.ekino.lesaint.dozerannihilation.demo;

import com.google.common.base.Function;

/**
 * IntegerToStringMapper - cette interface est directement déduite de IntegerToString.
 * Elle se contente de tirer la même interface que l'implémentation originale du Mapper annoté.
 *
 * Lorsqu'un mapper est annoté @Mapper (comme ici pour IntegerToString), n'importe quel collaborateur
 * devra alors se faire injecter IntegerToStringMapper (et non pas IntegerToString directement).
 *
 * @author Sébastien Lesaint
 */
public interface IntegerToStringMapper extends Function<Integer, String> {

}
