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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;
import fr.javatronic.damapping.util.Sets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Processor;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;

/**
 * DAFileWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAFileWriter implements DAWriter {
  private final BufferedWriter writer;
  @Nullable
  private DAName packageName;
  @Nullable
  private Set<DAName> imports;
  @Nullable
  private Set<String> importSimpleNames;

  public DAFileWriter(BufferedWriter writer) {
    this.writer = writer;
  }

  public DAFileWriter appendPackage(@Nonnull DAName packageName) throws IOException {
    this.packageName = checkNotNull(packageName, "PackageName can not be null");
    if (packageName.length() > 0) {
      writer.append("package ").append(packageName).append(";");
      writer.newLine();
      writer.newLine();
    }
    return this;
  }

  public DAFileWriter appendImports(Collection<DAName> mapperImports) throws IOException {
    checkNotNull(mapperImports, "Collection of imports can not be null");
    this.imports = Sets.copyOf(mapperImports);
    this.importSimpleNames = from(mapperImports).transform(DANameToSimpleNameAsString.INSTANCE)
        .filter(notNull())
        .toSet();
    if (mapperImports.isEmpty()) {
      return this;
    }

    List<DAName> imports = filterAndSortImports(mapperImports, packageName);
    if (imports.isEmpty()) {
      return this;
    }

    for (DAName name : imports) {
      writer.append("import ").append(name).append(";");
      writer.newLine();
    }
    writer.newLine();
    return this;
  }

  private List<DAName> filterAndSortImports(Collection<DAName> mapperImports, @Nullable DAName packageName) {
    Predicate<DAName> notDisplayedBase = Predicates.or(
        // defense against null values, of null/empty DAName.name
        InvalidDAName.INSTANCE,
        // imports from java itself
        JavaLangDANamePredicate.INSTANCE
    );
    Predicate<DAName> notDisplayed;
    if (packageName == null) {
      notDisplayed = notDisplayedBase;
    }
    else {
      notDisplayed = Predicates.or(
          notDisplayedBase,
          // imports in the same package as the generated class (ie. the package of the Mapper class)
          new PackagePredicate(packageName)
      );
    }

    List<DAName> res = Lists.copyOf(
        from(mapperImports)
            .filter(
                Predicates.not(
                    notDisplayed
                )
            )
            .toSet()
    );
    Collections.sort(res);
    return res;
  }

  public DAFileWriter appendGeneratedAnnotation(@Nonnull Class<? extends Processor> processClass) throws IOException {
    return appendGeneratedAnnotation(processClass.getCanonicalName());
  }

  public DAFileWriter appendGeneratedAnnotation(@Nonnull String annotationProcessorQualifiedName) throws IOException {
    writer.append("@javax.annotation.Generated(\"").append(annotationProcessorQualifiedName).append("\")");
    writer.newLine();
    return this;
  }

  public DAClassWriter<DAFileWriter> newClass(DAType classType) throws IOException {
    return new DAClassWriter<DAFileWriter>(classType, new FileContextImpl(), this, 0);
  }

  public DAInterfaceWriter<DAFileWriter> newInterface(String name) throws IOException {
    return new DAInterfaceWriter<DAFileWriter>(name, new FileContextImpl(), this, 0);
  }

  public void end() throws IOException {
    writer.flush();
    writer.close();
  }

  private static enum InvalidDAName implements Predicate<DAName> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAName daName) {
      return daName == null || daName.getName() == null || daName.getName().isEmpty();
    }
  }

  private static enum JavaLangDANamePredicate implements Predicate<DAName> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAName qualifiedName) {
      return qualifiedName != null && qualifiedName.toString().startsWith("java.lang.");
    }
  }

  private static class PackagePredicate implements Predicate<DAName> {
    @Nonnull
    private final DAName packageName;

    public PackagePredicate(@Nonnull DAName packageName) {
      this.packageName = packageName;
    }

    @Override
    public boolean apply(DAName qualifiedName) {
      String name = qualifiedName.toString();
      int dotIndex = name.lastIndexOf(".");
      if (dotIndex > -1) {
        return name.substring(0, dotIndex).equals(packageName.toString());
      }
      return false;
    }
  }

  private static enum DANameToSimpleNameAsString implements Function<DAName, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable DAName daName) {
      if (daName == null) {
        return null;
      }
      return DANameFactory.simpleFromQualified(daName).getName();
    }
  }

  private class FileContextImpl implements FileContext {
    @Nonnull
    @Override
    public String getPackageName() {
      return packageName == null ? "" : packageName.getName();
    }

    @Nonnull
    @Override
    public BufferedWriter getWriter() {
      return writer;
    }

    @Override
    public boolean hasExpliciteImport(@Nullable DAType type) {
      if (type == null || imports == null || type.getQualifiedName() == null) {
        return false;
      }
      return imports.contains(type.getQualifiedName());
    }

    @Override
    public boolean hasHomonymousImport(@Nullable DAType type) {
      if (type == null || importSimpleNames == null) {
        return false;
      }
      return !hasExpliciteImport(type) && importSimpleNames.contains(type.getSimpleName().getName());
    }
  }
}
