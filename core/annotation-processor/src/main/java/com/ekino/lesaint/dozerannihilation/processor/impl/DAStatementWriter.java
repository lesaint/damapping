package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * DAStatementWriter -
 *
 * @author Sébastien Lesaint
 */
public class DAStatementWriter<T extends DAWriter> extends AbstractDAWriter<T> {

    DAStatementWriter(BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
    }

    DAStatementWriter<T> start() throws IOException {
        appendIndent();
        return this;
    }

    DAStatementWriter<T> append(CharSequence s) throws IOException {
        bw.append(s);
        return this;
    }

    DAStatementWriter<T> append(char c) throws IOException {
        bw.append(c);
        return this;
    }

    DAStatementWriter<T> appendType(DAType type) throws IOException {
        appendType(bw, type);
        return this;
    }

    DAStatementWriter<T> appendTypeArgs(List<DAType> typeArgs) throws IOException {
        appendTypeArgs(bw, typeArgs);
        return this;
    }

    DAStatementWriter<T> appendParamValues(List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            bw.append("()");
            return this;
        }

        bw.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            bw.append(parameter.name);
            if (it.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(")");
        return this;
    }

    T end() throws IOException {
        bw.append(";");
        bw.newLine();
        return parent;
    }
}
