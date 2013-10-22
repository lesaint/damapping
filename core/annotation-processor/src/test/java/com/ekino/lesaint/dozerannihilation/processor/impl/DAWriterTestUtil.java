package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;

import javax.lang.model.type.TypeKind;

import static com.ekino.lesaint.dozerannihilation.processor.impl.DATypeFactory.declared;

/**
 * DAWriterTestUtil -
 *
 * @author Sébastien Lesaint
 */
final class DAWriterTestUtil {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static final DAType OVERRIDE_ANNOTATION = DATypeFactory.declared("java.lang.Override");
    static final DAType NULLABLE_ANNOTATION = DATypeFactory.declared("javax.annotation.Nullable");
    static final DAType SERIALIZABLE_INTERFACE = DATypeFactory.declared("java.io.Serializable");
    static final DAType FUNCTION_INTEGER_TO_STRING_INTERFACE = DATypeFactory.declared("com.google.common.base.Function",
            ImmutableList.of(DATypeFactory.declared("java.lang.Integer"), DATypeFactory.declared("java.lang.String"))
    );
    static final DAType DAWRITER_ABSTACT_CLASS = DATypeFactory.declared("com.ekino.lesaint.dozerannihilation.processor.impl.DAWriter");
    static final DAType BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS = DATypeFactory.declared("com.acme.Bidon",
            ImmutableList.of(DATypeFactory.declared("java.lang.Integer"), DATypeFactory.declared("java.lang.String"))
    );
    static final DAParameter STRING_TOTO_PARAMETER = daParameter("java.lang.String", "toto");
    static final DAParameter STRING_TITI_PARAMETER = daParameter("java.lang.String", "titi");
    static final DAParameter FUNCTION_STRING_INTEGER_ARRAY_PARAMETER = functionStringToIntegerArray("complexeParam");
    static final DAType NAME_DATYPE = declared("com.acme.Name");

    private DAWriterTestUtil() {
        // prevents instantiation
    }

    static DAParameter daParameter(String typeQualifiedName, String name) {
        DAParameter res = new DAParameter();
        res.name = DANameFactory.from(name);
        res.type = DATypeFactory.declared(typeQualifiedName);
        return res;
    }

    /**
     * Un paramètre de type tableau de Function<String, Integer>
     */
    private static DAParameter functionStringToIntegerArray(String name) {
        DAParameter res = new DAParameter();
        res.name = DANameFactory.from(name);
        DAType parameterType = declared("com.google.common.base.Function",
                ImmutableList.of(DATypeFactory.declared("java.lang.String"), DATypeFactory.declared("java.lang.Integer")));
        parameterType.kind = TypeKind.ARRAY;
        res.type = parameterType;
        return res;
    }
}
