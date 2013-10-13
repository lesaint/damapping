package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

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

}
