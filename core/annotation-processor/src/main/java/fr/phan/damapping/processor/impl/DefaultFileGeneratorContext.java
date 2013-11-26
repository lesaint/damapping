package fr.phan.damapping.processor.impl;

import java.util.List;

/**
* DefaultFileGeneratorContext -
*
* @author Sébastien Lesaint
*/
class DefaultFileGeneratorContext implements FileGeneratorContext {
    private final DASourceClass sourceClass;
    private final DefaultImportVisitor importVisitor;
    private final DAType mapperDAType;
    private final DAType mapperImplDAType;
    private final DAType mapperFactoryClassDAType;
    private final DAType mapperFactoryInterfaceDAType;
    private final DAType mapperFactoryImplDAType;

    DefaultFileGeneratorContext(DASourceClass sourceClass, DefaultImportVisitor importVisitor) {
        this.sourceClass = sourceClass;
        this.importVisitor = importVisitor;
        this.mapperDAType = DATypeFactory.declared(sourceClass.type.qualifiedName + "Mapper");
        this.mapperImplDAType = DATypeFactory.declared(sourceClass.type.qualifiedName + "MapperImpl");
        this.mapperFactoryClassDAType = DATypeFactory.declared(sourceClass.type.qualifiedName + "MapperFactory");
        this.mapperFactoryInterfaceDAType = DATypeFactory.declared(sourceClass.type.qualifiedName + "MapperFactory");
        this.mapperFactoryImplDAType = DATypeFactory.declared(sourceClass.type.qualifiedName + "MapperFactoryImpl");
    }

    @Override
    public DASourceClass getSourceClass() {
        return sourceClass;
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
    public List<DAName> getMapperFactoryClassImports() {
        return importVisitor.getMapperFactoryClassImports();
    }

    @Override
    public List<DAName> getMapperFactoryInterfaceImports() {
        return importVisitor.getMapperFactoryInterfaceImports();
    }

    @Override
    public List<DAName> getMapperFactoryImplImports() {
        return importVisitor.getMapperFactoryImplImports();
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
