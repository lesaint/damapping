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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.util.FluentIterable;
import fr.javatronic.damapping.util.Function;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DATypeImportComputer - This class compute the name of Types to import for a specific DAType.
 * <p>Isolation of this processing is meant for easier testing of this very important piece of code and for futur
 * implementation of some caching behavior if found necessary</p>
 *
 * TODO : cache the list of imports for a specific DAType, remove static method
 *
 * @author Sébastien Lesaint
 */
public class DATypeImportComputer {

  private static final Function<DAType,Collection<DAName>> COMPUTE_IMPORTS = new Function<DAType, Collection<DAName>>() {
    @Override
    public Collection<DAName> apply(DAType daType) {
      return computeImports(daType);
    }
  };

  public static Collection<DAName> computeImports(DAType daType) {
    List<DAName> qualifiedName = hasQualifiedNameToImport(daType)
        ? Collections.<DAName>emptyList() : Collections.singletonList(daType.getQualifiedName());

    Set<DAName> res = new HashSet<DAName>();
    res.addAll(qualifiedName);
    List<Collection<DAName>> argsImportLists = FluentIterable.from(daType.getTypeArgs()).transform(COMPUTE_IMPORTS).toList();
    for (Collection<DAName> importList : argsImportLists) {
      res.addAll(importList);
    }
    res.addAll(daType.getSuperBound() == null ? Collections.<DAName>emptyList() : computeImports(daType.getSuperBound()));
    res.addAll(daType.getExtendsBound() == null ? Collections.<DAName>emptyList() : computeImports(daType.getExtendsBound()));
    return res;
  }

  private static boolean hasQualifiedNameToImport(DAType daType) {
    return hasNoName(daType.getKind())
        // importing a type from default package is illegal
        || daType.getQualifiedName() == null || daType.getSimpleName().equals(daType.getQualifiedName());
  }

  private static boolean hasNoName(DATypeKind kind) {
    return kind.isPrimitive() || kind == DATypeKind.WILDCARD;
  }
}
