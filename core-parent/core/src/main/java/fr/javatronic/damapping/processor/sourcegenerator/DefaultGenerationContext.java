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
package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * DefaultGenerationContext -
 *
 * @author Sébastien Lesaint
 */
public class DefaultGenerationContext implements GenerationContext {
  @Nonnull
  private final DASourceClass sourceClass;
  @Nonnull
  private final Map<String, GeneratedFileDescriptor> descriptors;

  public static class PartialDescriptor {
    private final String key;
    private final DAType daType;
    private final List<DAName> imports;
    private final SourceGeneratorFactory sourceGeneratorFactory;

    public PartialDescriptor(String key, DAType daType, List<DAName> imports, SourceGeneratorFactory sourceGeneratorFactory) {
      this.key = key;
      this.daType = daType;
      this.imports = imports;
      this.sourceGeneratorFactory = sourceGeneratorFactory;
    }
  }

  public DefaultGenerationContext(@Nonnull DASourceClass sourceClass, @Nonnull Collection<PartialDescriptor> partialDescriptors) {
    this.sourceClass = checkNotNull(sourceClass);
    ImmutableMap.Builder<String, GeneratedFileDescriptor> builder = ImmutableMap.builder();
    for (PartialDescriptor partialDescriptor : partialDescriptors) {
      builder.put(
          partialDescriptor.key,
          new GeneratedFileDescriptorImpl(partialDescriptor.key, partialDescriptor.daType, partialDescriptor.imports, partialDescriptor.sourceGeneratorFactory, this)
      );
    }
    this.descriptors = builder.build();
  }

  @Override
  @Nullable
  public GeneratedFileDescriptor getDescriptor(String key) {
    return this.descriptors.get(key);
  }

  @Nonnull
  @Override
  public Set<String> getDescriptorKeys() {
    return this.descriptors.keySet();
  }

  @Override
  @Nonnull
  public DASourceClass getSourceClass() {
    return sourceClass;
  }

}
