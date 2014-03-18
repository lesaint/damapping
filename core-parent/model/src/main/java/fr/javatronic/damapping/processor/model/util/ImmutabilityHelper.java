package fr.javatronic.damapping.processor.model.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
    if (set instanceof ImmutableSet) {
      return set;
    }
    return ImmutableSet.copyOf(set);
  }

  public static <T> List<T> nonNullFrom(List<T> list) {
    if (list == null) {
      return Collections.emptyList();
    }
    if (list instanceof ImmutableList) {
      return list;
    }
    return ImmutableList.copyOf(list);
  }
}
