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

/**
 * DAEnumValue - Represents a value of an enum.
 * <p>
 * In the following example, there will be a DAEnumValue object for {@code BLUE}, {@link RED} and
 * {@code GREEN}.
 * <pre>
 * public enum Color {
 *   BLUE, RED, GREEN;
 * }
 * </pre>
 * </p>
 *
 * @author Sébastien Lesaint
 */
public interface DAEnumValue extends CharSequence {
  /**
   * The name of the enum value, ie. the value returned by the {@link Enum#name()} method.
   *
   * @return a {@link String}
   */
  @Nonnull
  String getName();
}
