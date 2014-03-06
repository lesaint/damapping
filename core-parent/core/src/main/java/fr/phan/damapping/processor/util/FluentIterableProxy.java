package fr.phan.damapping.processor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * FluentIterableProxy -
 *
 * @author Sébastien Lesaint
 */
public final class FluentIterableProxy {

  private static final GuavaProxy GUAVA_PROXY = new GuavaProxy();

  private FluentIterableProxy() {
    // prevents instantiation
  }

  /**
   * Creates a list from the specified FluentIterable by using method toList() if it exists, otherwise methode
   * toImmutableList, thus providing support for the old version of Guava embedded in IDEA 12 while still beeing able to
   * use the latest version of Guava in project in which toImmutableList has been removed.
   */
  public static <T> ImmutableList<T> toList(FluentIterable<T> fluentIterable) {
    return GUAVA_PROXY.toList(fluentIterable);
  }

  /**
   * Creates a set from the specified FluentIterable by using method toSet() if it exists, otherwise methode
   * toImmutableSet, thus providing support for the old version of Guava embedded in IDEA 12 while still beeing able to
   * use the latest version of Guava in project in which toImmutableSet has been removed.
   */
  public static <T> ImmutableSet<T> toSet(FluentIterable<T> fluentIterable) {
    return GUAVA_PROXY.toSet(fluentIterable);
  }

  private static class GuavaProxy {
    private static final String TO_LIST = "toList";
    private static final String TO_IMMUTABLE_LIST = "toImmutableList";
    private static final String TO_SET = "toSet";
    private static final String TO_IMMUTABLE_SET = "toImmutableSet";

    private final Method toListMethod;
    private final Method toSetMethod;

    public GuavaProxy() {
      this.toListMethod = searchToListMethod();
      this.toSetMethod = searchToSetMethod();
    }

    private static Method searchToListMethod() {
      try {
        return FluentIterable.class.getMethod(TO_LIST);
      } catch (NoSuchMethodException e) {
        try {
          return FluentIterable.class.getMethod(TO_IMMUTABLE_LIST);
        } catch (NoSuchMethodException e1) {
          throw new RuntimeException("Can not find neither " + TO_LIST + " nor " + TO_IMMUTABLE_LIST + " on class FluentIterable");
        }
      }
    }

    private static Method searchToSetMethod() {
      try {
        return FluentIterable.class.getMethod(TO_SET);
      } catch (NoSuchMethodException e) {
        try {
          return FluentIterable.class.getMethod(TO_IMMUTABLE_SET);
        } catch (NoSuchMethodException e1) {
          throw new RuntimeException("Can not find neither " + TO_SET + " nor " + TO_IMMUTABLE_SET + " on class FluentIterable");
        }
      }
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableList<T> toList(@Nonnull FluentIterable<T> fluentIterable) {
      Preconditions.checkNotNull(fluentIterable);
      try {
        return (ImmutableList<T>) toListMethod.invoke(fluentIterable);
      } catch (IllegalAccessException e) {
        System.err.println("Failed to invoke " + TO_LIST + " or " + TO_IMMUTABLE_LIST + " on FluentIterable instance");
        Throwables.propagate(e);
      } catch (InvocationTargetException e) {
        System.err.println("Failed to invoke " + TO_LIST + " or " + TO_IMMUTABLE_LIST + " on FluentIterable instance");
        Throwables.propagate(e);
      }
      // this line will never be reached
      return null;
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableSet<T> toSet(FluentIterable<T> fluentIterable) {
      Preconditions.checkNotNull(fluentIterable);
      try {
        return (ImmutableSet<T>) toSetMethod.invoke(fluentIterable);
      } catch (IllegalAccessException e) {
        System.err.println("Failed to invoke " + TO_SET + " or " + TO_IMMUTABLE_SET + " on FluentIterable instance");
        Throwables.propagate(e);
      } catch (InvocationTargetException e) {
        System.err.println("Failed to invoke " + TO_SET + " or " + TO_IMMUTABLE_SET + " on FluentIterable instance");
        Throwables.propagate(e);
      }
      // this line will never be reached
      return null;
    }
  }
}
