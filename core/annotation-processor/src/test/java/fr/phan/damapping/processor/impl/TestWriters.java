package fr.phan.damapping.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
* TestWriters -
*
* @author Sébastien Lesaint
*/
class TestWriters {
    final StringWriter out = new StringWriter();
    final BufferedWriter bw = new BufferedWriter(out);

    String getRes() throws IOException {
        bw.flush();
        return out.getBuffer().toString();
    }
}
