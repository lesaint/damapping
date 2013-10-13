package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

/**
* MapperFactoryImplFileGenerator - Générateur du fichier source de la classe MapperFactoryImpl générée dans le cas
* où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
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

        appendFactoryMethods(bw, context);
        bw.newLine();

        appendPrivateMapperImpl(bw, context);
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private void appendFactoryMethods(BufferedWriter bw, FileGeneratorContext context) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void appendPrivateMapperImpl(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAName simpleName = context.getMapperClass().type.simpleName;
        bw.append(INDENT).append("private static class ").append(simpleName).append("MapperImpl").append(" implements ").append(simpleName).append("Mapper").append(" {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("private final ").append(simpleName).append(" instance;");
        bw.newLine();
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("public ").append(simpleName).append("MapperImpl").append("(").append(simpleName).append(" instance").append(") {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append(INDENT).append("this.instance = instance;");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("}");
        bw.newLine();
        bw.newLine();
//        bw.append(INDENT).append(INDENT).append("public ")
        bw.append(INDENT).append("}");
    }
}
