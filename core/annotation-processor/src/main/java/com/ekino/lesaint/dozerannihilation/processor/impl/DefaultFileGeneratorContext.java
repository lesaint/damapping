package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Name;
import java.util.List;

/**
* DefaultFileGeneratorContext -
*
* @author Sébastien Lesaint
*/
class DefaultFileGeneratorContext implements FileGeneratorContext {
    private final DAMapperClass mapperClass;
    private final DefaultImportVisitor importVisitor;
    private final DAType mapperDAType;
    private final DAType mapperImplDAType;
    private final DAType mapperFactoryClassDAType;
    private final DAType mapperFactoryInterfaceDAType;
    private final DAType mapperFactoryImplDAType;

    DefaultFileGeneratorContext(DAMapperClass mapperClass, DefaultImportVisitor importVisitor) {
        this.mapperClass = mapperClass;
        this.importVisitor = importVisitor;
        this.mapperDAType = DATypeFactory.declared(mapperClass.type.qualifiedName + "Mapper");
        this.mapperImplDAType = DATypeFactory.declared(mapperClass.type.qualifiedName + "MapperImpl");
        this.mapperFactoryClassDAType = DATypeFactory.declared(mapperClass.type.qualifiedName + "MapperFactory");
        this.mapperFactoryInterfaceDAType = DATypeFactory.declared(mapperClass.type.qualifiedName + "MapperFactory");
        this.mapperFactoryImplDAType = DATypeFactory.declared(mapperClass.type.qualifiedName + "MapperFactoryImpl");
    }

    @Override
    public DAMapperClass getMapperClass() {
        return mapperClass;
    }

    @Override
    public List<DAName> getMapperImports() {
        return importVisitor.getMapperImports();
    }

    @Override
    public List<DAName> getMapperImplImports() {
        return importVisitor.getMapperImplImports();
    }

    @Override
    public List<DAName> getMapperFactoryImports() {
        return importVisitor.getMapperFactoryImports();
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
