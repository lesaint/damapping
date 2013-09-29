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
        return type.qualifiedName != null && Function.class.getCanonicalName().equals(type.qualifiedName.getName());
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
        // interface are declared directly only in Mapper
        // in MapperImpl there is no need to import them again since they are inherited from Mapper
    }

    @Override
    protected void visiteForMapperFactory(ImportVisitor visitor) {
        // interfaces are not used in the Factory
    }
}
