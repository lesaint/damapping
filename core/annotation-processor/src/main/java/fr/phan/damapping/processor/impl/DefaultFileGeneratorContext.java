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
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.impl.imports.MapperFactoryClassImportsModelVisitor;
import fr.phan.damapping.processor.impl.imports.MapperFactoryImplImportsModelVisitor;
import fr.phan.damapping.processor.impl.imports.MapperFactoryInterfaceImportsModelVisitor;
import fr.phan.damapping.processor.impl.imports.MapperImplImportsModelVisitor;
import fr.phan.damapping.processor.impl.imports.MapperImportsModelVisitor;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import java.util.List;

/**
* DefaultFileGeneratorContext -
*
* @author Sébastien Lesaint
*/
class DefaultFileGeneratorContext implements FileGeneratorContext {
    private final DASourceClass sourceClass;
    private final DAType mapperDAType;
    private final DAType mapperImplDAType;
    private final DAType mapperFactoryClassDAType;
    private final DAType mapperFactoryInterfaceDAType;
    private final DAType mapperFactoryImplDAType;

    DefaultFileGeneratorContext(DASourceClass sourceClass) {
        this.sourceClass = sourceClass;
        this.mapperDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "Mapper");
        this.mapperImplDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperImpl");
        this.mapperFactoryClassDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperFactory");
        this.mapperFactoryInterfaceDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperFactory");
        this.mapperFactoryImplDAType = DATypeFactory.declared(sourceClass.getType().getQualifiedName() + "MapperFactoryImpl");
    }

    @Override
    public DASourceClass getSourceClass() {
        return sourceClass;
    }

    @Override
    public List<DAName> getMapperImports() {
        MapperImportsModelVisitor visitor = new MapperImportsModelVisitor();
        sourceClass.accept(visitor);
        return visitor.getImports();
    }

    @Override
    public List<DAName> getMapperImplImports() {
        MapperImplImportsModelVisitor visitor = new MapperImplImportsModelVisitor();
        sourceClass.accept(visitor);
        return visitor.getImports();
    }

    @Override
    public List<DAName> getMapperFactoryInterfaceImports() {
        MapperFactoryInterfaceImportsModelVisitor visitor = new MapperFactoryInterfaceImportsModelVisitor();
        sourceClass.accept(visitor);
        return visitor.getImports();
    }

    @Override
    public List<DAName> getMapperFactoryClassImports() {
        MapperFactoryClassImportsModelVisitor visitor = new MapperFactoryClassImportsModelVisitor();
        sourceClass.accept(visitor);
        return visitor.getImports();
    }

    @Override
    public List<DAName> getMapperFactoryImplImports() {
        MapperFactoryImplImportsModelVisitor visitor = new MapperFactoryImplImportsModelVisitor();
        sourceClass.accept(visitor);
        return visitor.getImports();
    }

    @Override
    public DAType getMapperDAType() {
        return mapperDAType;
    }

    @Override
    public DAType getMapperImplDAType() {
        return mapperImplDAType;
    }

    @Override
    public DAType getMapperFactoryClassDAType() {
        return mapperFactoryClassDAType;
    }

    @Override
    public DAType getMapperFactoryInterfaceDAType() {
        return mapperFactoryInterfaceDAType;
    }

    @Override
    public DAType getMapperFactoryImplDAType() {
        return mapperFactoryImplDAType;
    }
}
