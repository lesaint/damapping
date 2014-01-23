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
package fr.phan.damapping.processor.sourcegenerator;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FileGeneratorContext -
 *
 * @author Sébastien Lesaint
 */
public interface FileGeneratorContext {
  @Nullable
  DASourceClass getSourceClass();

  @Nonnull
  List<DAName> getMapperImports();

  @Nonnull
  List<DAName> getMapperImplImports();

  @Nonnull
  List<DAName> getMapperFactoryInterfaceImports();

  @Nonnull
  List<DAName> getMapperFactoryClassImports();

  @Nonnull
  List<DAName> getMapperFactoryImplImports();

  @Nonnull
  DAType getMapperDAType();

  @Nonnull
  DAType getMapperImplDAType();

  @Nonnull
  DAType getMapperFactoryClassDAType();

  @Nonnull
  DAType getMapperFactoryInterfaceDAType();

  @Nonnull
  DAType getMapperFactoryImplDAType();
}
