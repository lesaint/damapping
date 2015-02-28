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

import fr.javatronic.damapping.util.Optional;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Name;
import javax.lang.model.util.Elements;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * Simple implements of the ElementImports interface which uses a {@link java.util.Map} of qualifiedName as {@link String}
 * by simple names as {@link javax.lang.model.element.Name}.
 */
class ElementImportsImpl implements ElementImports {
  @Nonnull
  private final Map<Name, String> elementBySimpleName;
  @Nonnull
  private final Elements elements;

  ElementImportsImpl(@Nonnull Map<Name, String> elementBySimpleName,
                     @Nonnull Elements elements) {
    this.elementBySimpleName = checkNotNull(elementBySimpleName);
    this.elements = checkNotNull(elements);
  }

  @Nonnull
  @Override
  public Optional<String> findBySimpleName(@Nullable CharSequence simpleName) {
    return Optional.fromNullable(elementBySimpleName.get(elements.getName(simpleName)));
  }
}
