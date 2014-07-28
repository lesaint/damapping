package fr.javatronic.damapping.processor.model.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * ImmutabilityHelper -
 *
 * @author Sébastien Lesaint
 */
public final class ImmutabilityHelper {
  private ImmutabilityHelper() {
    // prevents intantiation
  }

  public static <T> Set<T> nonNullFrom(Set<T> set) {
    if (set == null) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(set);
  }

  public static <T> List<T> nonNullFrom(List<T> list) {
    if (list == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(list);
  }
}
