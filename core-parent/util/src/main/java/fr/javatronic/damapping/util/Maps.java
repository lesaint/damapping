package fr.javatronic.damapping.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps -
 *
 * @author Sébastien Lesaint
 */
public final class Maps {
  private Maps() {
    // prevents instantiation
  }

  public static <T, V> Map<T, V> newHashMap() {
    return new HashMap<T, V>();
  }
}
