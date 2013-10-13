package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.annotation.Nullable;

/**
* ImportVisitor -
*
* @author Sébastien Lesaint
*/
interface ImportVisitor {
    void addMapperImport(@Nullable DAName qualifiedName);
    void addMapperImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperFactoryClassImport(@Nullable DAName qualifiedName);
    void addMapperFactoryClassImport(@Nullable Iterable<DAName> qualifiedNames);
    void addMapperImplImport(@Nullable DAName qualifiedName);
    void addMapperImplImport(@Nullable Iterable<DAName> qualifiedNames);
}
