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

import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * SourceGenerationService -
 *
 * @author Sébastien Lesaint
 */
public class SourceGenerationServiceImpl implements SourceGenerationService {
  private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
  private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

  @Override
  public void generateAll(@Nonnull GenerationContext generationContext,
                          @Nonnull SourceWriterDelegate delegate) throws IOException {
    for (String key : generationContext.getDescriptorKeys()) {
      generate(generationContext, key, delegate);
    }
  }

  @Override
  public void generate(@Nonnull GenerationContext generationContext,
                       @Nonnull String key,
                       @Nonnull SourceWriterDelegate delegate) throws IOException {
    GeneratedFileDescriptor descriptor = generationContext.getDescriptor(key);
    if (descriptor == null) {
      return;
    }

    delegate.generateFile(descriptor);
  }

}
