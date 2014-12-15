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
package fr.javatronic.damapping.processor.model.function;

import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.util.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DAImportFunctions -
 *
 * @author Sébastien Lesaint
 */
public final class DAImportFunctions {
  private DAImportFunctions() {
    // prevents instantiation
  }

  @Nonnull
  public static Function<DAImport, DAName> toQualifiedName() {
    return DAImportToQualifiedName.INSTANCE;
  }

  private static enum DAImportToQualifiedName implements Function<DAImport, DAName> {
    INSTANCE;

    @Nullable
    @Override
    public DAName apply(@Nullable DAImport daImport) {
      if (daImport == null) {
        return null;
      }
      return daImport.getQualifiedName();
    }
  }

  @Nonnull
  public static Function<DAImport, DAName> toSimpleName() {
    return DAImportToSimpleName.INSTANCE;
  }

  private static enum DAImportToSimpleName implements Function<DAImport, DAName> {
    INSTANCE;

    @Nullable
    @Override
    public DAName apply(@Nullable DAImport daImport) {
      if (daImport == null) {
        return null;
      }
      return daImport.getSimpleName();
    }
  }
}
