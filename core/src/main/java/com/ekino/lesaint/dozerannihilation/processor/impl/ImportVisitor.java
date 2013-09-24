package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.annotation.Nullable;
import javax.lang.model.element.Name;

/**
* ImportVisitor -
*
* @author Sébastien Lesaint
*/
interface ImportVisitor {
    void addMapperImport(@Nullable Name qualifiedName);
    void addMapperFactoryImport(@Nullable Name qualifiedName);
    void addMapperImplImport(@Nullable Name qualifiedName);
}
