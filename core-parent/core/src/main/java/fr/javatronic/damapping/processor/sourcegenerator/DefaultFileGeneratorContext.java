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
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperFactoryClassImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperFactoryImplImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperFactoryInterfaceImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperImplImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperInterfaceImportsModelVisitor;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DefaultFileGeneratorContext -
 *
 * @author Sébastien Lesaint
 */
public class DefaultFileGeneratorContext implements FileGeneratorContext {
  @Nonnull
  private final DASourceClass sourceClass;
  @Nonnull
  private final DAType mapperInterfaceDAType;
  @Nonnull
  private final DAType mapperFactoryInterfaceDAType;
  @Nonnull
  private final DAType mapperImplDAType;
  @Nonnull
  private final DAType mapperFactoryClassDAType;
  @Nonnull
  private final DAType mapperFactoryImplDAType;

  public DefaultFileGeneratorContext(DASourceClass sourceClass) {
    this.sourceClass = sourceClass;
    this.mapperInterfaceDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "Mapper");
    this.mapperFactoryInterfaceDAType = DATypeFactory.declared(
        sourceClass.getType().getQualifiedName() + "MapperFactory"
    );
    this.mapperImplDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperImpl");
    this.mapperFactoryClassDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperFactory");
    this.mapperFactoryImplDAType = DATypeFactory.declared(
        sourceClass.getType().getQualifiedName() + "MapperFactoryImpl"
    );
  }

  @Override
  @Nonnull
  public DASourceClass getSourceClass() {
    return sourceClass;
  }

  @Override
  @Nonnull
  public List<DAName> getMapperInterfaceImports() {
    MapperInterfaceImportsModelVisitor visitor = new MapperInterfaceImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports();
  }

  @Override
  @Nonnull
  public List<DAName> getMapperFactoryInterfaceImports() {
    MapperFactoryInterfaceImportsModelVisitor visitor = new MapperFactoryInterfaceImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports();
  }

  @Override
  @Nonnull
  public List<DAName> getMapperImplImports() {
    MapperImplImportsModelVisitor visitor = new MapperImplImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports();
  }

  @Override
  @Nonnull
  public List<DAName> getMapperFactoryClassImports() {
    MapperFactoryClassImportsModelVisitor visitor = new MapperFactoryClassImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports();
  }

  @Override
  @Nonnull
  public List<DAName> getMapperFactoryImplImports() {
    MapperFactoryImplImportsModelVisitor visitor = new MapperFactoryImplImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports();
  }

  @Override
  @Nonnull
  public DAType getMapperInterfaceDAType() {
    return mapperInterfaceDAType;
  }

  @Override
  @Nonnull
  public DAType getMapperFactoryInterfaceDAType() {
    return mapperFactoryInterfaceDAType;
  }

  @Override
  @Nonnull
  public DAType getMapperImplDAType() {
    return mapperImplDAType;
  }

  @Override
  @Nonnull
  public DAType getMapperFactoryClassDAType() {
    return mapperFactoryClassDAType;
  }

  @Override
  @Nonnull
  public DAType getMapperFactoryImplDAType() {
    return mapperFactoryImplDAType;
  }
}
