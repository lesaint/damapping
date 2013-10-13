package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

/**
* DefaultImportVisitor -
*
* @author Sébastien Lesaint
*/
class DefaultImportVisitor implements ImportVisitor, DAImports {
    private final ImmutableList.Builder<DAName> mapperImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperFactoryImports = ImmutableList.builder();
    private final ImmutableList.Builder<DAName> mapperImplImports = ImmutableList.builder();

    @Override
    public void addMapperImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperImplImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperImplImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperImplImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperImplImports.addAll(qualifiedNames);
        }
    }

    @Override
    public void addMapperFactoryClassImport(@Nullable DAName qualifiedDAName) {
        if (qualifiedDAName != null) {
            mapperFactoryImports.add(qualifiedDAName);
        }
    }

    @Override
    public void addMapperFactoryClassImport(@Nullable Iterable<DAName> qualifiedNames) {
        if (qualifiedNames != null) {
            mapperFactoryImports.addAll(qualifiedNames);
        }
    }

    @Override
    public List<DAName> getMapperImports() {
        return mapperImports.build();
    }

    @Override
    public List<DAName> getMapperImplImports() {
        return mapperImplImports.build();
    }

    @Override
    public List<DAName> getMapperFactoryImports() {
        return mapperFactoryImports.build();
    }
}
