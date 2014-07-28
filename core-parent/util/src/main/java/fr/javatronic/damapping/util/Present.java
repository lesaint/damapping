package fr.javatronic.damapping.util;

import javax.annotation.Nullable;

/**
* Present -
*
* @author Sébastien Lesaint
*/
final class Present<T> extends Optional<T> {
  private final T reference;

  Present(T reference) {
    this.reference = reference;
  }

  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public T get() {
    return reference;
  }

  @Override
  public T or(T t) {
    return reference;
  }

  @Override
  public Optional<T> or(Optional<? extends T> optional) {
    return this;
  }

  @Nullable
  @Override
  public T orNull() {
    return reference;
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (object instanceof Present) {
      Present<?> other = (Present<?>) object;
      return reference.equals(other.reference);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 0x599df91c + reference.hashCode();
  }

  @Override
  public String toString() {
    return "Optional.of(" + reference + ")";
  }
}
