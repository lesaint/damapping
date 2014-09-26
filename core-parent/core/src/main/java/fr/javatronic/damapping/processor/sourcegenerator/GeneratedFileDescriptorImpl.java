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
package fr.javatronic.damapping.processor.sourcegenerator;


import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * GeneratedFileDescriptor -
 *
 * @author Sébastien Lesaint
 */
public class GeneratedFileDescriptorImpl implements GeneratedFileDescriptor {
  @Nonnull
  private final String key;
  @Nonnull
  private final DAType type;
  @Nonnull
  private final List<DAName> imports;
  @Nonnull
  private final SourceGenerator sourceGenerator;
  @Nonnull
  private final GenerationContext context;

  public GeneratedFileDescriptorImpl(@Nonnull String key, @Nonnull DAType type,
                                     @Nonnull List<DAName> imports,
                                     @Nonnull SourceGeneratorFactory sourceGeneratorFactory,
                                     @Nonnull GenerationContext context) {
    this.key = key;
    this.type = checkNotNull(type);
    this.imports = checkNotNull(imports);
    this.sourceGenerator = sourceGeneratorFactory.instance(this);
    this.context = checkNotNull(context);
  }

  @Override
  @Nonnull
  public String getKey() {
    return key;
  }

  @Override
  @Nonnull
  public DAType getType() {
    return type;
  }

  @Override
  @Nonnull
  public List<DAName> getImports() {
    return imports;
  }

  @Override
  @Nonnull
  public SourceGenerator getSourceGenerator() {
    return sourceGenerator;
  }

  @Override
  @Nonnull
  public GenerationContext getContext() {
    return context;
  }
}
