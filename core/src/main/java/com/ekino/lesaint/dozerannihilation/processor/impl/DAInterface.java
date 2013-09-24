package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Function;

import java.util.List;

/**
* DAInterface -
*
* @author Sébastien Lesaint
*/
class DAInterface extends AbstractImportVisitable {
    DAType type;
    List<DAType> typeArgs;

    public boolean isGuavaFunction() {
        return type.qualifiedName != null && Function.class.getCanonicalName().equals(type.qualifiedName.toString());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        visitor.addMapperImport(type.qualifiedName);
        for (DAType typeArg : typeArgs) {
            visitor.addMapperImport(typeArg.qualifiedName);
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        visitor.addMapperImport(type.qualifiedName);
        for (DAType typeArg : typeArgs) {
            visitor.addMapperImport(typeArg.qualifiedName);
        }
    }

    @Override
    protected void visiteForMapperFactory(ImportVisitor visitor) {
        // interfaces are not used in the Factory
    }
}
