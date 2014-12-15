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

import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.function.DAImportFunctions;
import fr.javatronic.damapping.processor.model.predicate.DANamePredicates;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
  private static final Predicate<DAName> NOT_JAVALANG_DANAME = Predicates.not(DANamePredicates.isJavaLangType());

  private final BufferedWriter writer;
  @Nullable
  private DAName packageName;
  @Nullable
  private Set<DAName> importQualifiedNames;
  @Nullable
  private Set<DAName> importSimpleNames;

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

  /**
   * Appends import statements for the specified {@link DAImport}, only removing duplicates and imports of "java.lang"
   * and sorting them (using {@link String}'s comparable implementation on {@link DAImport#getQualifiedName()}) for
   * reproductive behavior.
   *
   * @param mapperImports a {@link Collections} of {@link DAImport}
   * @return the current {@link DAFileWriter}
   * @throws IOException if an {@link IOException} occurs writing the imports
   */
  public DAFileWriter appendImports(@Nonnull Collection<DAImport> mapperImports) throws IOException {
    checkNotNull(mapperImports, "Collection of imports can not be null");

    if (mapperImports.isEmpty()) {
      return this;
    }

    List<DAName> imports = removeDuplicatesFilterJavaLangAndSortImports(mapperImports);
    if (imports.isEmpty()) {
      this.importQualifiedNames = Collections.emptySet();
      this.importSimpleNames = Collections.emptySet();
      return this;
    }

    this.importQualifiedNames = from(mapperImports)
        .transform(DAImportFunctions.toQualifiedName())
        .filter(notNull())
        .toSet();
    this.importSimpleNames = from(mapperImports).transform(DAImportFunctions.toSimpleName())
        .filter(notNull())
        .toSet();

    for (DAName name : imports) {
      writer.append("import ").append(name).append(";");
      writer.newLine();
    }
    writer.newLine();
    return this;
  }

  private List<DAName> removeDuplicatesFilterJavaLangAndSortImports(Collection<DAImport> mapperImports) {
    List<DAName> res = Lists.copyOf(
        from(mapperImports)
            .filter(notNull())
            .transform(DAImportFunctions.toQualifiedName())
            .filter(notNull())
            .filter(NOT_JAVALANG_DANAME)
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
      if (type == null || importQualifiedNames == null || type.getQualifiedName() == null) {
        return false;
      }
      return importQualifiedNames.contains(type.getQualifiedName());
    }

    @Override
    public boolean hasHomonymousImport(@Nullable DAType type) {
      if (type == null || importSimpleNames == null) {
        return false;
      }
      return !hasExpliciteImport(type) && importSimpleNames.contains(type.getSimpleName());
    }
  }
}
