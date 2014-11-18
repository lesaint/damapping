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

import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Optional;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;

/**
* UnresolvedTypeScanResult -
*
* @author Sébastien Lesaint
*/
class UnresolvedTypeScanResult {
  @Nonnull
  private final Map<Element, DAType> unresolved = Maps.newHashMap();
  @Nonnull
  private final Map<String, DAType> fixedBySimple = Maps.newHashMap();
  @Nonnull
  private final Map<String, DAType> fixedByQualified= Maps.newHashMap();

  @Nonnull
  public Map<Element, DAType> getUnresolved() {
    return unresolved;
  }

  public void addFixed(@Nullable DAType daType) {
    if (daType == null) {
      return;
    }
    fixedBySimple.put(daType.getSimpleName().getName(), daType);

    if (daType.getQualifiedName() != null) {
      fixedByQualified.put(daType.getQualifiedName().getName(), daType);
    }
  }

  @Nonnull
  public Optional<DAType> findFixedBySimpleName(@Nullable String simpleName) {
    if (simpleName == null || simpleName.isEmpty()) {
      return Optional.absent();
    }
    return Optional.fromNullable(fixedBySimple.get(simpleName));
  }

  @Nonnull
  public Optional<DAType> findFixedByQualifiedName(@Nullable String qualifiedName) {
    if (qualifiedName == null || qualifiedName.isEmpty()) {
      return Optional.absent();
    }

    return Optional.fromNullable(fixedByQualified.get(qualifiedName));
  }
}
