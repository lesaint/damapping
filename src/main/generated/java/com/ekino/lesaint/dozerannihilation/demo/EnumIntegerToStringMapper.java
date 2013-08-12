package com.ekino.lesaint.dozerannihilation.demo;

import com.google.common.base.Function;

/**
 * EnumIntegerToStringMapper - cette interface est directement déduite de EnumIntegerToString.
 * Elle se contente de tirer la même interface que l'implémentation originale du Mapper annoté.
 *
 * Lorsqu'un mapper est annoté @Mapper (comme ici pour EnumIntegerToString), n'importe quel collaborateur
 * devra alors se faire injecter EnumIntegerToStringMapper (et non pas EnumIntegerToString directement).
 *
 * @author Sébastien Lesaint
 */
public interface EnumIntegerToStringMapper extends Function<Integer, String> {
}
