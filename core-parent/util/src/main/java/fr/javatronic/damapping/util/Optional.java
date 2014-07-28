package fr.javatronic.damapping.util;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * Optional - Clone of Guava's Optional class
 *
 * @author Sébastien Lesaint
 */
public abstract class Optional<T> {

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> absent() {
    return (Optional<T>) Absent.INSTANCE;
  }

  public static <T> Optional<T> of(final T reference) {
    return new Present<T>(checkNotNull(reference, "Argument of \"of\" method can not be null"));
  }

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> fromNullable(@javax.annotation.Nullable T nullableReference) {
    if (nullableReference == null) {
      return Optional.<T>absent();
    }
    return new Present<T>(nullableReference);
  }

  public abstract boolean isPresent();

  public abstract T get();

  public abstract T or(T t);

  public abstract Optional<T> or(Optional<? extends T> optional);

  @javax.annotation.Nullable
  public abstract T orNull();

}
