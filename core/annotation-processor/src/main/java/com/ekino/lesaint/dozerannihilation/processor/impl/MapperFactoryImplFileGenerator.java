package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

/**
* MapperFactoryImplFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperFactoryImplFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperFactoryImpl";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();
        appendHeader(bw, daMapperClass, context.getMapperFactoryImports());

        bw.append("public class ").append(daMapperClass.type.simpleName).append("MapperFactoryImpl").append(" {");
        bw.newLine();
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }
}
