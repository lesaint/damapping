package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.Collections;
import java.util.List;

/**
 * DAWriterTestUtil -
 *
 * @author Sébastien Lesaint
 */
final class DAWriterTestUtil {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private DAWriterTestUtil() {
        // prevents instantiation
    }

    static DAType daType(String qualifiedName) {
        return daType(qualifiedName, Collections.<DAType>emptyList());
    }

    static DAType daType(String qualifiedName, List<DAType> typeArgs) {
        DAType annotationDAType = new DAType();
        annotationDAType.simpleName = DANameFactory.from(qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1));
        annotationDAType.qualifiedName = DANameFactory.from(qualifiedName);
        annotationDAType.typeArgs = typeArgs;
        return annotationDAType;
    }

    static DAParameter daParameter(String typeQualifiedName, String name) {
        DAParameter res = new DAParameter();
        res.name = DANameFactory.from(name);
        res.type = daType(typeQualifiedName);
        return res;
    }
}
