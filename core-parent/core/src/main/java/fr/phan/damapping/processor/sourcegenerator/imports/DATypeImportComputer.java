/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.sourcegenerator.imports;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.DATypeKind;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;

/**
 * DATypeImportComputer -
 *
 * @author Sébastien Lesaint
 */
public class DATypeImportComputer {

  // TODO : cache the list of imports for a specific DAType
  public static Iterable<DAName> computeImports(DAType daType) {
    ImmutableList<DAName> empty = ImmutableList.<DAName>of();
    ImmutableList<DAName> qualifiedName = hasNoName(daType.getKind()) ? empty : of(daType.getQualifiedName());
    Iterable<Iterable<DAName>> typesImports = Iterables.transform(
        daType.getTypeArgs(),
        new Function<DAType, Iterable<DAName>>() {
          @Override
          public Iterable<DAName> apply(DAType daType) {
            return computeImports(daType);
          }
        }
    );
    return Iterables.concat(
        qualifiedName,
        Iterables.concat(typesImports),
        daType.getSuperBound() == null ? empty : copyOf(computeImports(daType.getSuperBound())),
        daType.getExtendsBound() == null ? empty : copyOf(computeImports(daType.getExtendsBound()))
    );
  }

  private static boolean hasNoName(DATypeKind kind) {
    return kind.isPrimitive() || kind == DATypeKind.WILDCARD;
  }
}