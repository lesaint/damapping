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
package fr.javatronic.damapping.processor.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DAName - Represents the name of a class, parameter, package, etc. It can not be empty.
 * <p>
 * This type is essentially here to enforce better typing in the model and enforcing that a name can not be empty
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface DAName extends CharSequence, Comparable<DAName>, DAElement {
  /**
   * The String representation of the DAName.
   *
   * @return a non-empty {@link String}
   */
  @Nonnull
  String getName();

  /**
   * Convenience method equalivalent to {@code getName().equals(packageName)} but with {@code null} support.
   *
   * @param packageName a {@link String} or {@code null}
   *
   * @return a boolean
   */
  boolean equals(@Nullable String packageName);
}
