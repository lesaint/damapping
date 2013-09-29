package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import static com.google.common.collect.FluentIterable.from;

/**
* MapperImplFileGenerator -
*
* @author Sébastien Lesaint
*/
class MapperImplFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getMapperClass().type.qualifiedName.getName() + "MapperImpl";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {
        // générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser
        DAMapperClass daMapperClass = context.getMapperClass();
        appendHeader(bw, daMapperClass, context.getMapperImplImports());

        bw.append("class ").append(daMapperClass.type.simpleName).append("MapperImpl").append(" implements ").append(daMapperClass.type.simpleName).append("Mapper").append(" {");
        bw.newLine();
        bw.newLine();

        DAInterface guavaInterface = from(daMapperClass.interfaces).firstMatch(DAInterfacePredicates.isGuavaFunction()).get();
        DAMethod guavaMethod = from(daMapperClass.methods).firstMatch(DAMethodPredicates.isGuavaFunction()).get();

        bw.append(INDENT).append("@Override");
        bw.newLine();
        bw.append(INDENT).append("public ").append(guavaMethod.returnType.simpleName);
        if (guavaMethod.returnType.isArray()) {
            bw.append("[]");
        }
        bw.append(" ").append(guavaMethod.name).append("(");
        Iterator<DAType> typeArgsIterator = guavaInterface.typeArgs.iterator();
        Iterator<DAParameter> parametersIterator = guavaMethod.parameters.iterator();
        while (hasNext(typeArgsIterator, parametersIterator)) {
            bw.append(typeArgsIterator.next().simpleName).append(" ").append(parametersIterator.next().name);
            if (hasNext(typeArgsIterator, parametersIterator)) {
                bw.append(", ");
            }
        }
        bw.append(")").append(" {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("return ").append(daMapperClass.type.simpleName).append("MapperFactory").append(".instance()").append(".").append(guavaMethod.name).append("(");
        parametersIterator = guavaMethod.parameters.iterator();
        while (parametersIterator.hasNext()) {
            bw.append(parametersIterator.next().name);
            if (parametersIterator.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(");");
        bw.newLine();
        bw.append(INDENT).append("}");
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private boolean hasNext(Iterator<DAType> typeArgsIterator, Iterator<DAParameter> parametersIterator) {
        return typeArgsIterator.hasNext() && parametersIterator.hasNext();
    }
}
