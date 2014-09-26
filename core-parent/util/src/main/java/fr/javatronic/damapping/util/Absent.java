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
