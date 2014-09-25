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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Lists;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ImportListBuilder -
 *
 * @author Sébastien Lesaint
 */
public class ImportListBuilder {
  private final List<DAName> imports = Lists.of();

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

  protected void addImports(@Nullable DAMethod daMethod) {
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
    for (DAAnnotation daAnnotation : daMethod.getAnnotations()) {
      addImport(daAnnotation.getType());
    }
  }

  protected void addImports(@Nonnull List<DAAnnotation> annotations) {
    for (DAAnnotation annotation : annotations) {
      addImports(annotation.getType());
    }
  }

  @Nonnull
  public List<DAName> getImports() {
    return Collections.unmodifiableList(imports);
  }
}
