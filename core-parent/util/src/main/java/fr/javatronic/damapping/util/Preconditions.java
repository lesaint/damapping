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
