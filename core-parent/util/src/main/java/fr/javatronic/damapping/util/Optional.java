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
