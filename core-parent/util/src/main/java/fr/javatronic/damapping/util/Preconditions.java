/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Preconditions - Partial clone of Guava's Preconditions class
 *
 * @author Sébastien Lesaint
 */
public final class Preconditions {

  private static final String NPE_DEFAULT_MSG = "object can not be null";
  private static final String IAE_DEFAULT_MSG = "Argument is not valid";

  private Preconditions() {
    // prevents instantiation
  }

  /**
   * Throws a NullPointerException with a generic message if the specified object is {@code null}, otherwise
   * returns it.
   *
   * @param obj an object of any type or {@code null}
   * @param <T> any type
   *
   * @return the argument
   */
  @Nonnull
  public static <T> T checkNotNull(@Nullable T obj) {
    return checkNotNull(obj, NPE_DEFAULT_MSG);
  }

  /**
   * Throws a NullPointerException with the specified message if the specified object is {@code null}, otherwise
   * returns it.
   * <p>
   * A default message will be used if the specified message is {@code null} or empty.
   * </p>
   *
   * @param obj     an object of any type or {@code null}
   * @param message a {@link String} or {@code null}
   * @param <T>     any type
   *
   * @return the argument
   */
  @Nonnull
  public static <T> T checkNotNull(@Nullable T obj, @Nullable String message) {
    if (obj == null) {
      throw new NullPointerException(message == null || message.isEmpty() ? NPE_DEFAULT_MSG : message);
    }
    return obj;
  }

  /**
   * Throws a {@link IllegalArgumentException} with a generic message if the specified boolean value is false.
   *
   * @param test a boolean value
   */
  public static void checkArgument(boolean test) {
    if (!test) {
      throw new IllegalArgumentException(IAE_DEFAULT_MSG);
    }
  }

  /**
   * Throws a {@link IllegalArgumentException} with the specified message if the specified boolean
   * value is false.
   * <p>
   * A default message will be used if the specified message is {@code null} or empty.
   * </p>
   *
   * @param test    a boolean value
   * @param message a {@link String} or {@code null}
   */
  public static void checkArgument(boolean test, @Nullable String message) {
    if (!test) {
      throw new IllegalArgumentException(message == null || message.isEmpty() ? IAE_DEFAULT_MSG : message);
    }
  }
}
