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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;
import fr.javatronic.damapping.util.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * ImportListBuilder -
 *
 * @author Sébastien Lesaint
 */
public class ImportListBuilder {
  private final Set<DAImport> imports = Sets.of();

  protected void addImports(@Nullable DAType daType) {
    if (daType != null) {
      imports.addAll(DATypeImportComputer.computeImports(daType));
    }
  }

  public static class DAMaethodImportFilters {
    private static final DAMaethodImportFilters NO_FILTERS = new DAMaethodImportFilters(null);

    @Nullable
    private final Predicate<DAAnnotation> methodAnnotations;

    private DAMaethodImportFilters(@Nullable Predicate<DAAnnotation> methodAnnotations) {
      this.methodAnnotations = methodAnnotations;
    }

    public static DAMaethodImportFilters from(@Nullable Predicate<DAAnnotation> methodAnnotations) {
      if (methodAnnotations == null) {
        return NO_FILTERS;
      }
      return new DAMaethodImportFilters(methodAnnotations);
    }

    @Nonnull
    public Predicate<DAAnnotation> getMethodAnnotations() {
      return methodAnnotations == null ? Predicates.<DAAnnotation>alwaysTrue() : methodAnnotations;
    }
  }

  protected void addImports(@Nullable DAMethod daMethod, @Nonnull DAMaethodImportFilters importFilters) {
    checkNotNull(importFilters);
    if (daMethod == null) {
      return;
    }
    for (DAParameter parameter : daMethod.getParameters()) {
      addImports(parameter.getAnnotations());
      addImports(parameter.getType());
    }
    if (daMethod.getReturnType() != null) {
      addImports(daMethod.getReturnType());
    }
    for (DAAnnotation daAnnotation : from(daMethod.getAnnotations()).filter(importFilters.getMethodAnnotations())) {
      addImports(daAnnotation.getType());
    }
  }

  protected void addImports(@Nullable DAMethod daMethod) {
    addImports(daMethod, DAMaethodImportFilters.from(null));
  }

  protected void addImports(@Nonnull List<DAAnnotation> annotations) {
    for (DAAnnotation annotation : annotations) {
      addImports(annotation.getType());
    }
  }

  @Nonnull
  public List<DAImport> getImports(@Nonnull final String currentPackage) {
    checkNotNull(currentPackage, "currentPackage can not be null. Use the empty string for the default pacakge");

    if (imports.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, List<DAImport>> indexBySimpleName = Maps.newHashMap();

    for (DAImport anImport : imports) {
      List<DAImport> names = indexBySimpleName.get(anImport.getSimpleName());
      if (names == null) {
        names = Collections.singletonList(anImport);
        indexBySimpleName.put(anImport.getSimpleName(), names);
      }
      else if (names.size() == 1) {
        DAImport existingImport = names.iterator().next();
        names = new ArrayList<>(2);
        names.add(existingImport);
        names.add(anImport);
        indexBySimpleName.put(anImport.getSimpleName(), names);
      }
      else {
        names.add(anImport);
      }
    }

    return from(indexBySimpleName.values())
        .transform(new ImportSelector(currentPackage))
        .filter(Predicates.notNull())
        .toList();
  }

  /**
   * This comparator orders DAName objects representing qualified names of types with the same simple name so that we
   * can then quickly choose which one to import:
   *
   */
  private static class HomonymImportsComparator implements Comparator<DAImport> {
    @Nonnull
    private final String currentPackage;

    public HomonymImportsComparator(@Nonnull String currentPackage) {
      checkNotNull(currentPackage);
      this.currentPackage = currentPackage;
    }

    @Override
    public int compare(@Nullable DAImport o1, @Nullable DAImport o2) {
      // class from the current package go last
      // other classes are sorted alphabetically

      boolean currentPckg1 = o1 == null ? false : o1.getPackageName().equals(currentPackage);
      boolean currentPckg2 = o2 == null ? false : o2.getPackageName().equals(currentPackage);

      if (currentPckg1 && currentPckg2) {
        return 0;
      }
      if (currentPckg1) {
        return 1;
      }
      if (currentPckg2) {
        return -1;
      }

      String o1Name = o1 == null ? "" : o1.getQualifiedName().getName();
      String o2Name = o2 == null ? "" : o2.getQualifiedName().getName();
      return o1Name.compareTo(o2Name);
    }
  }

  private static class ImportSelector implements Function<List<DAImport>, DAImport> {
    @Nonnull
    private final String currentPackage;
    @Nonnull
    private final Comparator<DAImport> homonymImportsComparator;

    public ImportSelector(@Nonnull String currentPackage) {
      this.currentPackage = checkNotNull(currentPackage);
      this.homonymImportsComparator = new HomonymImportsComparator(currentPackage);
    }

    @Nullable
    @Override
    public DAImport apply(@Nullable List<DAImport> daNames) {
      if (daNames == null || daNames.isEmpty()) {
        return null;
      }
      if (daNames.size() == 1) {
        DAImport anImport = daNames.iterator().next();
        if (currentPackage.equals(anImport.getPackageName())) {
          return null;
        }
        return anImport;
      }
      // the following sort is seriously border-line
      // but since ImportListBuilder is really the only user of the lists, we save a copy and just sort directly
      Collections.sort(daNames, homonymImportsComparator);
      return daNames.iterator().next();
    }
  }
}
