package fr.javatronic.damapping.util;

/**
 * Predicate - Clone of Guava's Predicate interface
 *
 * @author Sébastien Lesaint
 */
public interface Predicate<T> {
  boolean apply(@javax.annotation.Nullable T t);

  boolean equals(@javax.annotation.Nullable java.lang.Object o);
}
