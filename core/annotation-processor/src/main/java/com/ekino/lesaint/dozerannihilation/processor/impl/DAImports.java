package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.List;

/**
 * DAImports -
 *
 * @author Sébastien Lesaint
 */
public interface DAImports {
    List<DAName> getMapperImports();

    List<DAName> getMapperImplImports();

    List<DAName> getMapperFactoryImports();
}
