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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ElementImports -
 *
 * @author Sébastien Lesaint
 */
public interface ElementImports {

  /**
   * Lookup amoung the explicit qualified imports, the explicit star imports and the implicit imports of the current
   * Element if any type has the specified simpleName.
   *
   * @param simpleName a {@link CharSequence} or {@code null}
   * @return a {@link Optional} of a {@link String} representing the qualified name of the imported type
   */
  @Nonnull
  Optional<String> findBySimpleName(@Nullable CharSequence simpleName);
}
