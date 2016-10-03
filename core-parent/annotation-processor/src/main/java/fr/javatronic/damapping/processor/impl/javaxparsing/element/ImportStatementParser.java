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
package fr.javatronic.damapping.processor.impl.javaxparsing.element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A type capable of parsing import statements in a source file.
 */
interface ImportStatementParser {

  /**
   * Parses the specified CharSequence and extracts the qualified names of the explicitly imported types.
   *
   * @param charSequence a {@link CharSequence} or {@code null}
   *
   * @return a {@link Iterable} of {@link String}
   */
  @Nonnull
  Iterable<String> qualifiedNames(@Nullable CharSequence charSequence);

}