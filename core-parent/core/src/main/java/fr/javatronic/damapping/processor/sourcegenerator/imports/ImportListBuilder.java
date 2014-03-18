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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

/**
 * ImportListBuilder -
 *
 * @author Sébastien Lesaint
 */
public class ImportListBuilder {
  private final ImmutableList.Builder<DAName> imports = ImmutableList.builder();

  protected void addImport(@Nullable DAName qualifiedDAName) {
    if (qualifiedDAName != null) {
      imports.add(qualifiedDAName);
    }
  }

  protected void addImport(@Nullable DAType daType) {
    if (daType != null) {
      imports.addAll(DATypeImportComputer.computeImports(daType));
    }
  }

  protected void addImports(@Nullable DAType daType) {
    if (daType != null) {
      imports.addAll(DATypeImportComputer.computeImports(daType));
    }
  }

  @Nonnull
  public List<DAName> getImports() {
    return imports.build();
  }
}
