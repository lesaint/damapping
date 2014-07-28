package fr.javatronic.damapping.util;

import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
* Absent -
*
* @author Sébastien Lesaint
*/
final class Absent extends Optional<Object> {
  public static final Absent INSTANCE = new Absent();

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public Object get() {
    throw new IllegalArgumentException();
  }

  @Override
  public Object or(Object t) {
    return checkNotNull(t, "Argument of or method can not be null");
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Object> or(Optional<?> optional) {
    return (Optional<Object>) checkNotNull(optional);
  }

  @Nullable
  @Override
  public Object orNull() {
    return null;
  }

  @Override
  public boolean equals(@Nullable Object object) {
    return object == this;
  }

  @Override
  public int hashCode() {
    return 0x599df91c;
  }

  @Override
  public String toString() {
    return "Optional.absent()";
  }
}
