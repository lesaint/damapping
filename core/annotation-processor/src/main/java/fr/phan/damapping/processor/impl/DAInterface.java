package fr.phan.damapping.processor.impl;

import com.google.common.base.Function;

import java.util.List;

/**
* DAInterface -
*
* @author Sébastien Lesaint
*/
class DAInterface extends AbstractImportVisitable {
    DAType type;

    public boolean isGuavaFunction() {
        return type.qualifiedName != null && Function.class.getCanonicalName().equals(type.qualifiedName.getName());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        visitor.addMapperImport(type.getImports());
        for (DAType typeArg : type.typeArgs) {
            visitor.addMapperImport(typeArg.getImports());
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        // interface are declared directly only in Mapper
        // in MapperImpl there is no need to import them again since they are inherited from Mapper
    }

    @Override
    protected void visiteForMapperFactoryClass(ImportVisitor visitor) {
        // interfaces are not used in the Factory
    }

    @Override
    protected void visiteForMapperFactoryInterface(ImportVisitor visitor) {
        // interfaces are not used in MapperFactory interface
    }

    @Override
    protected void visiteForMapperFactoryImpl(ImportVisitor visitor) {
        // interfaces are not used in MapperFactory impl
    }
}
