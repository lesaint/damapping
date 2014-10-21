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
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Preconditions;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Processor;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DAFileWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAFileWriter implements DAWriter {
  private final BufferedWriter bw;
  private DAName packageName;

  public DAFileWriter(BufferedWriter bw) {
    this.bw = bw;
  }

  public DAFileWriter appendPackage(@Nonnull DAName packageName) throws IOException {
    this.packageName = Preconditions.checkNotNull(packageName, "PackageName can not be null");
    if (packageName.length() > 0) {
      bw.append("package ").append(packageName).append(";");
      bw.newLine();
      bw.newLine();
    }
    return this;
  }

  public DAFileWriter appendImports(Collection<DAName> mapperImports) throws IOException {
    if (mapperImports.isEmpty()) {
      return this;
    }

    List<DAName> imports = filterAndSortImports(mapperImports, packageName);
    if (imports.isEmpty()) {
      return this;
    }

    for (DAName name : imports) {
      bw.append("import ").append(name).append(";");
      bw.newLine();
    }
    bw.newLine();
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
    } else {
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
    bw.append("@javax.annotation.Generated(\"").append(annotationProcessorQualifiedName).append("\")");
    bw.newLine();
    return this;
  }

  public DAClassWriter<DAFileWriter> newClass(DAType classType) throws IOException {
    return new DAClassWriter<DAFileWriter>(classType, bw, this, 0);
  }

  public DAInterfaceWriter<DAFileWriter> newInterface(String name) throws IOException {
    return new DAInterfaceWriter<DAFileWriter>(name, bw, this, 0);
  }

  public void end() throws IOException {
    bw.flush();
    bw.close();
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
}
