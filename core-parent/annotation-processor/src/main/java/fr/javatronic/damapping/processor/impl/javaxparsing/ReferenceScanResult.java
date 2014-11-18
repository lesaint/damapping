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
import fr.javatronic.damapping.util.Preconditions;
import fr.javatronic.damapping.util.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;

/**
 * ReferenceScanResult - Holds the result of a scan performed by {@link ReferencesScanner}.
 * <ul>
 *   <li>the imports (explicite and implicite) of the scanned {@link javax.lang.model.element.TypeElement}</li>
 *   <li>the unresolved references found during the scan</li>
 *   <li>the fixed unresolved references found during the scan</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class ReferenceScanResult {
  @Nonnull
  private final ElementImports imports;
  @Nonnull
  private final Set<Element> unresolved = Sets.of();
  @Nonnull
  private final Map<String, DAType> fixed = Maps.newHashMap();

  public ReferenceScanResult(@Nonnull ElementImports imports) {
    this.imports = Preconditions.checkNotNull(imports);
  }

  @Nonnull
  public ElementImports getImports() {
    return imports;
  }

  void addUnresolved(@Nullable Element element) {
    if (element != null) {
      unresolved.add(element);
    }
  }

  @Nonnull
  public Set<Element> getUnresolved() {
    return Collections.unmodifiableSet(unresolved);
  }

  public boolean hasUnresolved() {
    return !unresolved.isEmpty();
  }

  public boolean isUnresolved(Element typeElement) {
    return unresolved.contains(typeElement);
  }

  void addFixed(@Nullable DAType daType) {
    if (daType == null) {
      return;
    }

    if (daType.getQualifiedName() != null) {
      fixed.put(daType.getQualifiedName().getName(), daType);
    }
  }

  @Nonnull
  public Optional<DAType> findFixedByQualifiedName(@Nullable String qualifiedName) {
    if (qualifiedName == null || fixed.isEmpty()) {
      return Optional.absent();
    }

    return Optional.fromNullable(fixed.get(qualifiedName));
  }
}
