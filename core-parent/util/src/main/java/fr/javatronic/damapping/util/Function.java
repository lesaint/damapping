package fr.javatronic.damapping.util;

import javax.annotation.Nullable;

/**
 * Function - Clone of Guava's Function interface
 *
 * @author Sébastien Lesaint
 */
public interface Function <F, T>  {
  @Nullable
  T apply(@Nullable F f);

  boolean equals(@Nullable java.lang.Object o);

}
