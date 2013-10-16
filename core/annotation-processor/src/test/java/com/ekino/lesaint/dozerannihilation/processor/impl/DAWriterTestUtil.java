package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

/**
 * DAWriterTestUtil -
 *
 * @author Sébastien Lesaint
 */
final class DAWriterTestUtil {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static final DAType OVERRIDE_ANNOTATION = DAWriterTestUtil.daType("java.lang.Override");
    static final DAType NULLABLE_ANNOTATION = DAWriterTestUtil.daType("javax.annotation.Nullable");
    static final DAType SERIALIZABLE_INTERFACE = DAWriterTestUtil.daType("java.io.Serializable");
    static final DAType FUNCTION_INTEGER_TO_STRING_INTERFACE = DAWriterTestUtil.daType("com.google.common.base.Function",
            ImmutableList.of(DAWriterTestUtil.daType("java.lang.Integer"), DAWriterTestUtil.daType("java.lang.String"))
    );
    static final DAType DAWRITER_ABSTACT_CLASS = DAWriterTestUtil.daType("com.ekino.lesaint.dozerannihilation.processor.impl.DAWriter");
    static final DAType BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS = DAWriterTestUtil.daType("com.acme.Bidon",
            ImmutableList.of(DAWriterTestUtil.daType("java.lang.Integer"), DAWriterTestUtil.daType("java.lang.String"))
    );

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
