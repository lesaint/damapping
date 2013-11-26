package fr.phan.damapping.processor.impl;

import java.util.List;

/**
 * DAImports -
 *
 * @author Sébastien Lesaint
 */
public interface DAImports {

    List<DAName> getMapperImports();

    List<DAName> getMapperImplImports();

    List<DAName> getMapperFactoryClassImports();

    List<DAName> getMapperFactoryInterfaceImports();

    List<DAName> getMapperFactoryImplImports();
}
