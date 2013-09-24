package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.lang.model.element.Name;
import java.util.List;

/**
 * DAImports -
 *
 * @author Sébastien Lesaint
 */
public interface DAImports {
    List<Name> getMapperImports();

    List<Name> getMapperImplImports();

    List<Name> getMapperFactoryImports();
}
