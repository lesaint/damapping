package com.ekino.lesaint.dozerannihilation.processor.impl;

/**
* AbstractImportVisitable -
*
* @author Sébastien Lesaint
*/
abstract class AbstractImportVisitable implements ImportVisitable {

    @Override
    public void visite(ImportVisitor visitor) {
        visiteForMapper(visitor);
        visiteForMapperImpl(visitor);
        visiteForMapperFactoryClass(visitor);
        visiteForMapperFactoryInterface(visitor);
        visiteForMapperFactoryImpl(visitor);
    }

    protected abstract void visiteForMapper(ImportVisitor visitor);

    protected abstract void visiteForMapperImpl(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryClass(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryInterface(ImportVisitor visitor);

    protected abstract void visiteForMapperFactoryImpl(ImportVisitor visitor);
}
