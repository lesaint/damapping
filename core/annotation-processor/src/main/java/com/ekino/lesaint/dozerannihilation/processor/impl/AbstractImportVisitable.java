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
        visiteForMapperFactory(visitor);
        visiteForMapperImpl(visitor);
    }

    protected abstract void visiteForMapper(ImportVisitor visitor);

    protected abstract void visiteForMapperFactory(ImportVisitor visitor);

    protected abstract void visiteForMapperImpl(ImportVisitor visitor);
}
