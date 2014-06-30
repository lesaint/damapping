


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

import fr.javatronic.damapping.processor.model.DASourceClass;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * GenerationContext -
 *
 * @author Sébastien Lesaint
 */
public interface GenerationContext {
  static final String MAPPER_INTERFACE_KEY = "MapperInterface";
  static final String MAPPER_FACTORY_INTERFACE_KEY = "MapperFactoryInterface";
  static final String MAPPER_IMPL_KEY = "MapperImpl";
  static final String MAPPER_FACTORY_CLASS_KEY = "MapperFactoryClass";
  static final String MAPPER_FACTORY_IMPL_KEY = "MapperFactoryImpl";

  @Nonnull
  DASourceClass getSourceClass();

  @Nullable
  GeneratedFileDescriptor getDescriptor(String key);

  @Nonnull
  Set<String> getDescriptorKeys();

}