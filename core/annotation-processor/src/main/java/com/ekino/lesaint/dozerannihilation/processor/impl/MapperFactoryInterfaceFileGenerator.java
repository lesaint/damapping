package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;

/**
* MapperFactoryInterfaceFileGenerator - Générateur du fichier source de l'interface MapperFactory générée dans le cas
* où il existe au moins une méthode annotée avec @MapperFactoryMethod dans la class annotée avec @Mapper.
*
* @author Sébastien Lesaint
*/
class MapperFactoryInterfaceFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperFactory";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        DAMapperClass daMapperClass = context.getMapperClass();
        appendHeader(bw, daMapperClass, context.getMapperFactoryImports());

        bw.append("public interface ").append(daMapperClass.type.simpleName).append("MapperFactory").append(" {");
        bw.newLine();
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }
}