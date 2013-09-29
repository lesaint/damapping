package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.List;
import javax.lang.model.type.TypeKind;

/**
 * DAType -
 *
 * @author Sébastien Lesaint
 */
class DAType {
    TypeKind kind;
    /* QualifiedName du type, sauf dans le cas des tableaux où il s'agit du qualifedName type contenu dans le tableau */
    DAName qualifiedName;
    /* Name du type, sauf dans le cas des tableaux où il s'agit du name type contenu dans le tableau */
    DAName simpleName;
    List<DAType> types;

    public boolean isArray() {
        return kind == TypeKind.ARRAY;
    }

}
