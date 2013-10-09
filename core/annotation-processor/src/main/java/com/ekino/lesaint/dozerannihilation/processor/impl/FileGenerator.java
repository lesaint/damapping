package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

/**
* FileGenerator -
*
* @author Sébastien Lesaint
*/
interface FileGenerator {
    String fileName(FileGeneratorContext context);
    void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException;
}
