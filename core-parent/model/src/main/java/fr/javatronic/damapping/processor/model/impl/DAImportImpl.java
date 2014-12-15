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
package fr.javatronic.damapping.processor.model.impl;

import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkArgument;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DAImportImpl - Implementation of DAImport as an immutable object.
 *
 * @author Sébastien Lesaint
 */
@Immutable
public class DAImportImpl implements DAImport {
  @Nonnull
  private final DAName qualifiedName;
  @Nonnull
  private final DAName packageName;
  @Nonnull
  private final DAName simpleName;

  private DAImportImpl(@Nonnull DAName qualifiedName, @Nonnull DAName packageName, @Nonnull DAName simpleName) {
    this.qualifiedName = checkNotNull(qualifiedName);
    this.packageName = checkNotNull(packageName);
    this.simpleName = checkNotNull(simpleName);
  }

  /**
   *
   *
   * @param daName a {@link DAName}
   *
   * @return a {@link DAImportImpl} instance.
   *
   * @throws IllegalArgumentException if the specified DAName has no package
   */
  @Nonnull
  public static DAImport from(@Nonnull DAName daName) {
    checkNotNull(daName);

    DAName packageName = DANameFactory.packageNameFromQualified(daName);
    checkArgument(packageName != null, "importing a type from the default/unamed package is illegal");

    return new DAImportImpl(
        daName,
        packageName,
        DANameFactory.simpleFromQualified(daName)
    );
  }

  @Override
  @Nonnull
  public DAName getQualifiedName() {
    return qualifiedName;
  }

  @Override
  @Nonnull
  public DAName getPackageName() {
    return packageName;
  }

  @Override
  @Nonnull
  public DAName getSimpleName() {
    return simpleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DAImportImpl daImport = (DAImportImpl) o;

    if (!qualifiedName.equals(daImport.qualifiedName)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return qualifiedName.hashCode();
  }
}
