package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import javax.lang.model.element.Name;
import java.util.List;

/**
* DefaultImportVisitor -
*
* @author Sébastien Lesaint
*/
class DefaultImportVisitor implements ImportVisitor, DAImports {
    private final ImmutableList.Builder<Name> mapperImports = ImmutableList.builder();
    private final ImmutableList.Builder<Name> mapperFactoryImports = ImmutableList.builder();
    private final ImmutableList.Builder<Name> mapperImplImports = ImmutableList.builder();

    @Override
    public void addMapperImport(@Nullable Name qualifiedName) {
        if (qualifiedName != null) {
            mapperImports.add(qualifiedName);
        }
    }

    @Override
    public void addMapperImplImport(@Nullable Name qualifiedName) {
        if (qualifiedName != null) {
            mapperImplImports.add(qualifiedName);
        }
    }

    @Override
    public void addMapperFactoryImport(@Nullable Name qualifiedName) {
        if (qualifiedName != null) {
            mapperFactoryImports.add(qualifiedName);
        }
    }

    @Override
    public List<Name> getMapperImports() {
        return mapperImports.build();
    }

    @Override
    public List<Name> getMapperImplImports() {
        return mapperImplImports.build();
    }

    @Override
    public List<Name> getMapperFactoryImports() {
        return mapperFactoryImports.build();
    }
}
