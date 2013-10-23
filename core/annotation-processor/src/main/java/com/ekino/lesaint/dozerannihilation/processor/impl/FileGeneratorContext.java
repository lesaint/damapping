package com.ekino.lesaint.dozerannihilation.processor.impl;

/**
* FileGeneratorContext -
*
* @author Sébastien Lesaint
*/
interface FileGeneratorContext extends DAImports {
    DASourceClass getSourceClass();
    DAType getMapperDAType();
    DAType getMapperImplDAType();
    DAType getMapperFactoryClassDAType();
    DAType getMapperFactoryInterfaceDAType();
    DAType getMapperFactoryImplDAType();
}
