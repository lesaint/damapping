package fr.javatronic.damapping.util;

/**
 * Preconditions - Partial clone of Guava's Preconditions class
 *
 * @author Sébastien Lesaint
 */
public final class Preconditions {
  private Preconditions() {
    // prevents instantiation
  }

  public static <T> T checkNotNull(T obj) {
    return checkNotNull(obj, "object can not be null");
  }

  public static <T> void checkArgument(boolean test) {
    if (!test) {
      throw new IllegalArgumentException("Argument is not valid");
    }
  }

  public static <T> T checkNotNull(T obj, String message) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
    return obj;
  }
}
