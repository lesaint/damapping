package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

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

    T end() throws IOException {
        bw.append(";");
        bw.newLine();
        return parent;
    }
}
