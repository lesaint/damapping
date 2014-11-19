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
package fr.javatronic.damapping.processor.model;

import fr.javatronic.damapping.util.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
* DAImport -
*
* @author Sébastien Lesaint
*/
@Immutable
public class DAImport {
  private final boolean defaultPackage;
  @Nonnull
  private final DAName qualifiedName;
  @Nonnull
  private final String packageName;
  @Nonnull
  private final String simpleName;

  private DAImport(@Nonnull DAName qualifiedName, @Nonnull String packageName, @Nonnull String simpleName) {
    this.qualifiedName = checkNotNull(qualifiedName);
    this.packageName = checkNotNull(packageName);
    this.defaultPackage = packageName.isEmpty();
    this.simpleName = simpleName;
  }

  @Nonnull
  public static DAImport from(@Nonnull DAName daName) {
    checkNotNull(daName);

    String qualifiedName = daName.getName();
    String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    String packageName = simpleName.equals(qualifiedName) ? "" : qualifiedName.substring(0, qualifiedName.length() - simpleName.length() - 1);
    return new DAImport(daName, packageName, simpleName);
  }

  public boolean isDefaultPackage() {
    return defaultPackage;
  }

  @Nonnull
  public DAName getQualifiedName() {
    return qualifiedName;
  }

  @Nonnull
  public String getPackageName() {
    return packageName;
  }

  @Nonnull
  public String getSimpleName() {
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

    DAImport daImport = (DAImport) o;

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
