package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * DAWriter -
 *
 * @author Sébastien Lesaint
 */
class DAWriter {
    protected static final String INDENT = "    ";

    void appendIndent(BufferedWriter bw, int count) throws IOException {
        for (int i = 0 ; i < count ; i++) {
            bw.append(INDENT);
        }
    }

    protected void appendType(BufferedWriter bw, DAType type) throws IOException {
        bw.append(type.simpleName);
        appendTypeArgs(bw, type.typeArgs);
        if (type.isArray()) {
            bw.append("[]");
        }
    }

    protected void appendTypeArgs(BufferedWriter bw, List<DAType> typeArgs) throws IOException {
        if (!typeArgs.isEmpty()) {
            Iterator<DAType> iterator = typeArgs.iterator();
            bw.append("<");
            while (iterator.hasNext()) {
                DAType arg = iterator.next();
                appendType(bw, arg);
                if (iterator.hasNext()) {
                    bw.append(", ");
                }
            }
            bw.append(">");
        }
    }

}
