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

    DefaultFileGeneratorContext(DAMapperClass mapperClass, DefaultImportVisitor importVisitor) {
        this.mapperClass = mapperClass;
        this.importVisitor = importVisitor;
    }

    @Override
    public DAMapperClass getMapperClass() {
        return mapperClass;
    }

    @Override
    public List<Name> getMapperImports() {
        return importVisitor.getMapperImports();
    }

    @Override
    public List<Name> getMapperImplImports() {
        return importVisitor.getMapperImplImports();
    }

    @Override
    public List<Name> getMapperFactoryImports() {
        return importVisitor.getMapperFactoryImports();
    }
}
